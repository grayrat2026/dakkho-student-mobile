package com.dakkho.android.presentation.screens.quizzes

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.domain.model.Quiz
import com.dakkho.android.domain.model.QuizQuestion
import com.dakkho.android.domain.model.QuizQuestionResult
import com.dakkho.android.domain.model.QuizSubmitResult
import com.dakkho.android.domain.repository.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

enum class QuizScreenState {
    LIST, PLAYING, RESULT
}

data class CourseQuizzesUiState(
    val isLoading: Boolean = true,
    val quizzes: List<Quiz> = emptyList(),
    val screenState: QuizScreenState = QuizScreenState.LIST,
    // Quiz play state
    val currentQuiz: Quiz? = null,
    val questions: List<QuizQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedAnswers: Map<String, String> = emptyMap(),
    val remainingTimeMs: Long = 0,
    val isTimerRunning: Boolean = false,
    // Quiz result state
    val quizResult: QuizSubmitResult? = null,
    val questionResults: List<QuizQuestionResult> = emptyList(),
    val isSubmitting: Boolean = false,
    val showExplanationIndex: Int? = null,
    // General
    val error: String? = null
)

@HiltViewModel
class CourseQuizzesViewModel @Inject constructor(
    private val quizRepository: QuizRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CourseQuizzesUiState())
    val uiState: StateFlow<CourseQuizzesUiState> = _uiState.asStateFlow()

    private var courseId: String = ""
    private var countDownTimer: CountDownTimer? = null

    fun initialize(courseId: String) {
        this.courseId = courseId
        loadQuizzes()
    }

    fun loadQuizzes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            quizRepository.getQuizzesForCourse(courseId)
                .onSuccess { quizzes ->
                    _uiState.value = _uiState.value.copy(
                        quizzes = quizzes,
                        isLoading = false
                    )
                }
                .onFailure { error ->
                    Timber.e(error, "Load quizzes failed")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load quizzes"
                    )
                }
        }
    }

    fun startQuiz(quiz: Quiz) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            quizRepository.getQuizDetail(courseId, quiz.id)
                .onSuccess { (quizDetail, questions) ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        screenState = QuizScreenState.PLAYING,
                        currentQuiz = quizDetail,
                        questions = questions,
                        currentQuestionIndex = 0,
                        selectedAnswers = emptyMap(),
                        quizResult = null,
                        questionResults = emptyList()
                    )
                    // Start countdown timer if time limit is set
                    quizDetail.timeLimitMinutes?.let { minutes ->
                        startTimer(minutes * 60 * 1000L)
                    }
                }
                .onFailure { error ->
                    Timber.e(error, "Start quiz failed")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to start quiz"
                    )
                }
        }
    }

    fun selectAnswer(questionId: String, option: String) {
        val currentAnswers = _uiState.value.selectedAnswers.toMutableMap()
        currentAnswers[questionId] = option
        _uiState.value = _uiState.value.copy(selectedAnswers = currentAnswers)
    }

    fun goToNextQuestion() {
        val currentIndex = _uiState.value.currentQuestionIndex
        val totalQuestions = _uiState.value.questions.size
        if (currentIndex < totalQuestions - 1) {
            _uiState.value = _uiState.value.copy(
                currentQuestionIndex = currentIndex + 1,
                showExplanationIndex = null
            )
        } else {
            // Last question reached, can submit
            submitQuiz()
        }
    }

    fun goToPreviousQuestion() {
        val currentIndex = _uiState.value.currentQuestionIndex
        if (currentIndex > 0) {
            _uiState.value = _uiState.value.copy(
                currentQuestionIndex = currentIndex - 1,
                showExplanationIndex = null
            )
        }
    }

    fun goToQuestion(index: Int) {
        if (index in _uiState.value.questions.indices) {
            _uiState.value = _uiState.value.copy(
                currentQuestionIndex = index,
                showExplanationIndex = null
            )
        }
    }

    fun submitQuiz() {
        val quiz = _uiState.value.currentQuiz ?: return
        val answers = _uiState.value.selectedAnswers

        if (answers.isEmpty()) {
            _uiState.value = _uiState.value.copy(error = "Please answer at least one question")
            return
        }

        stopTimer()
        _uiState.value = _uiState.value.copy(isSubmitting = true)

        viewModelScope.launch {
            quizRepository.submitQuiz(quiz.id, answers)
                .onSuccess { result ->
                    // Build question results from the submission response
                    val questions = _uiState.value.questions
                    val questionResults = result.answers.mapNotNull { scored ->
                        val question = questions.find { it.id == scored.questionId }
                        question?.let {
                            QuizQuestionResult(
                                questionId = it.id,
                                questionText = it.questionText,
                                optionA = it.optionA,
                                optionB = it.optionB,
                                optionC = it.optionC,
                                optionD = it.optionD,
                                selected = scored.selected,
                                correct = scored.correct ?: "",
                                isCorrect = scored.isCorrect,
                                explanation = it.explanation
                            )
                        }
                    }
                    _uiState.value = _uiState.value.copy(
                        isSubmitting = false,
                        screenState = QuizScreenState.RESULT,
                        quizResult = result,
                        questionResults = questionResults
                    )
                }
                .onFailure { error ->
                    Timber.e(error, "Submit quiz failed")
                    _uiState.value = _uiState.value.copy(
                        isSubmitting = false,
                        error = error.message ?: "Failed to submit quiz"
                    )
                }
        }
    }

    fun backToList() {
        stopTimer()
        _uiState.value = _uiState.value.copy(
            screenState = QuizScreenState.LIST,
            currentQuiz = null,
            questions = emptyList(),
            selectedAnswers = emptyMap(),
            quizResult = null,
            questionResults = emptyList(),
            currentQuestionIndex = 0,
            remainingTimeMs = 0
        )
        loadQuizzes()
    }

    fun retryQuiz() {
        val quiz = _uiState.value.currentQuiz ?: return
        stopTimer()
        _uiState.value = _uiState.value.copy(
            screenState = QuizScreenState.PLAYING,
            selectedAnswers = emptyMap(),
            currentQuestionIndex = 0,
            quizResult = null,
            questionResults = emptyList()
        )
        quiz.timeLimitMinutes?.let { minutes ->
            startTimer(minutes * 60 * 1000L)
        }
    }

    fun toggleExplanation(index: Int) {
        val current = _uiState.value.showExplanationIndex
        _uiState.value = _uiState.value.copy(
            showExplanationIndex = if (current == index) null else index
        )
    }

    private fun startTimer(totalTimeMs: Long) {
        countDownTimer?.cancel()
        _uiState.value = _uiState.value.copy(
            remainingTimeMs = totalTimeMs,
            isTimerRunning = true
        )
        countDownTimer = object : CountDownTimer(totalTimeMs, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _uiState.value = _uiState.value.copy(remainingTimeMs = millisUntilFinished)
            }

            override fun onFinish() {
                _uiState.value = _uiState.value.copy(
                    remainingTimeMs = 0,
                    isTimerRunning = false
                )
                // Auto-submit when timer runs out
                if (_uiState.value.selectedAnswers.isNotEmpty()) {
                    submitQuiz()
                }
            }
        }.start()
    }

    private fun stopTimer() {
        countDownTimer?.cancel()
        countDownTimer = null
        _uiState.value = _uiState.value.copy(isTimerRunning = false)
    }

    fun formatTime(ms: Long): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%d:%02d", minutes, seconds)
    }

    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel()
    }
}
