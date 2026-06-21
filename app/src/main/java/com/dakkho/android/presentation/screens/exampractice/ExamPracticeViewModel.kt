package com.dakkho.android.presentation.screens.exampractice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.data.api.ExamApiService
import com.dakkho.android.data.db.EncryptedPrefsHelper
import com.dakkho.android.domain.model.ExamModels.PracticeAttempt
import com.dakkho.android.domain.model.ExamModels.PracticeQuestion
import com.dakkho.android.domain.model.ExamModels.PracticeTest
import com.dakkho.android.domain.model.ExamModels.PracticeTestType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class PracticePhase {
    LIST, QUIZ, RESULT
}

data class ExamPracticeUiState(
    val isLoading: Boolean = false,
    val phase: PracticePhase = PracticePhase.LIST,
    val tests: List<PracticeTest> = emptyList(),
    val selectedTest: PracticeTest? = null,
    val questions: List<PracticeQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedAnswers: Map<Int, Int> = emptyMap(),
    val remainingSeconds: Int = 0,
    val totalSeconds: Int = 0,
    val isTimerRunning: Boolean = false,
    val error: String? = null,
    val score: Int = 0,
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val skippedCount: Int = 0,
    val showReview: Boolean = false
)

@HiltViewModel
class ExamPracticeViewModel @Inject constructor(
    private val examApiService: ExamApiService,
    private val encryptedPrefsHelper: EncryptedPrefsHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExamPracticeUiState())
    val uiState: StateFlow<ExamPracticeUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        loadPracticeTests()
    }

    private fun loadPracticeTests() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val response = examApiService.getPracticeTests()
                if (response.isSuccessful) {
                    val apiResult = response.body()
                    val tests = apiResult?.data ?: emptyList()
                    _uiState.update { it.copy(isLoading = false, tests = tests) }
                } else {
                    loadFallbackTests()
                }
            } catch (e: Exception) {
                loadFallbackTests()
            }
        }
    }

    private fun loadFallbackTests() {
        val sampleTests = listOf(
            PracticeTest(
                id = "pt1", title = "মডেল টেস্ট - গণিত", subject = "গণিত",
                duration = 30, totalQuestions = 10, marks = 10,
                testType = PracticeTestType.MODEL_TEST
            ),
            PracticeTest(
                id = "pt2", title = "পূর্ববর্তী বছর - পদার্থবিজ্ঞান ২০২৪", subject = "পদার্থবিজ্ঞান",
                duration = 45, totalQuestions = 15, marks = 15,
                year = "2024", testType = PracticeTestType.PREVIOUS_YEAR
            ),
            PracticeTest(
                id = "pt3", title = "অনুশীলন - রসায়ন", subject = "রসায়ন",
                duration = 20, totalQuestions = 8, marks = 8,
                testType = PracticeTestType.PRACTICE
            )
        )
        _uiState.update { it.copy(isLoading = false, tests = sampleTests) }
    }

    private fun loadFallbackQuestions(): List<PracticeQuestion> {
        return listOf(
            PracticeQuestion(
                id = "q1", question = "দ্বিঘাত সমীকরণ x² - 5x + 6 = 0 এর সমাধান কী?",
                options = listOf("x = 2, 3", "x = 1, 6", "x = -2, -3", "x = -1, -6"),
                correctOptionIndex = 0, marks = 1, subject = "গণিত"
            ),
            PracticeQuestion(
                id = "q2", question = "নিউটনের দ্বিতীয় সূত্র অনুসারে F = কী?",
                options = listOf("ma", "mv", "m/a", "m+v"),
                correctOptionIndex = 0, marks = 1, subject = "পদার্থবিজ্ঞান"
            ),
            PracticeQuestion(
                id = "q3", question = "নিম্নলিখিত কোনটি জৈব যৌগ?",
                options = listOf("NaCl", "CH₄", "H₂O", "CO₂"),
                correctOptionIndex = 1, marks = 1, subject = "রসায়ন"
            ),
            PracticeQuestion(
                id = "q4", question = "sin 30° এর মান কত?",
                options = listOf("1", "0.5", "0.707", "0.866"),
                correctOptionIndex = 1, marks = 1, subject = "গণিত"
            ),
            PracticeQuestion(
                id = "q5", question = "আলোর বেগ শূন্যে কত?",
                options = listOf("3×10⁸ m/s", "3×10⁶ m/s", "3×10¹⁰ m/s", "3×10⁴ m/s"),
                correctOptionIndex = 0, marks = 1, subject = "পদার্থবিজ্ঞান"
            )
        )
    }

    fun startTest(test: PracticeTest) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val response = examApiService.getPracticeQuestions(test.id)
                if (response.isSuccessful) {
                    val apiResult = response.body()
                    val questions = apiResult?.data ?: emptyList()
                    if (questions.isEmpty()) {
                        startTestWithQuestions(test, loadFallbackQuestions())
                    } else {
                        startTestWithQuestions(test, questions)
                    }
                } else {
                    startTestWithQuestions(test, loadFallbackQuestions())
                }
            } catch (e: Exception) {
                startTestWithQuestions(test, loadFallbackQuestions())
            }
        }
    }

    private fun startTestWithQuestions(test: PracticeTest, questions: List<PracticeQuestion>) {
        val totalSeconds = test.duration * 60
        _uiState.update {
            it.copy(
                isLoading = false,
                phase = PracticePhase.QUIZ,
                selectedTest = test,
                questions = questions,
                currentQuestionIndex = 0,
                selectedAnswers = emptyMap(),
                remainingSeconds = totalSeconds,
                totalSeconds = totalSeconds,
                isTimerRunning = true
            )
        }
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch(Dispatchers.Default) {
            while (isActive) {
                val current = _uiState.value
                if (current.remainingSeconds <= 0) {
                    submitTest()
                    break
                }
                _uiState.update { it.copy(remainingSeconds = current.remainingSeconds - 1) }
                delay(1000)
            }
        }
    }

    fun selectAnswer(optionIndex: Int) {
        _uiState.update {
            it.copy(selectedAnswers = it.selectedAnswers + (it.currentQuestionIndex to optionIndex))
        }
    }

    fun goToNextQuestion() {
        _uiState.update {
            if (it.currentQuestionIndex < it.questions.size - 1) {
                it.copy(currentQuestionIndex = it.currentQuestionIndex + 1)
            } else it
        }
    }

    fun goToPreviousQuestion() {
        _uiState.update {
            if (it.currentQuestionIndex > 0) {
                it.copy(currentQuestionIndex = it.currentQuestionIndex - 1)
            } else it
        }
    }

    fun goToQuestion(index: Int) {
        _uiState.update {
            if (index in it.questions.indices) it.copy(currentQuestionIndex = index) else it
        }
    }

    fun submitTest() {
        timerJob?.cancel()
        val current = _uiState.value
        val questions = current.questions
        val answers = current.selectedAnswers

        var correct = 0
        var wrong = 0
        var skipped = 0

        questions.forEachIndexed { index, question ->
            val selectedOption = answers[index]
            if (selectedOption == null) {
                skipped++
            } else if (selectedOption == question.correctOptionIndex) {
                correct++
            } else {
                wrong++
            }
        }

        val score = correct // 1 mark per correct answer

        _uiState.update {
            it.copy(
                phase = PracticePhase.RESULT,
                isTimerRunning = false,
                score = score,
                correctCount = correct,
                wrongCount = wrong,
                skippedCount = skipped
            )
        }

        // Submit attempt to backend
        viewModelScope.launch {
            try {
                val timeTaken = current.totalSeconds - current.remainingSeconds
                val attempt = PracticeAttempt(
                    testId = current.selectedTest?.id ?: "",
                    score = score,
                    totalMarks = current.selectedTest?.marks ?: 0,
                    timeTakenSeconds = timeTaken,
                    answers = answers,
                    isSubmitted = true
                )
                // Note: ExamApiService doesn't have a submit endpoint yet
                // examApiService.submitPracticeAttempt(attempt)
            } catch (_: Exception) {
                // Silently fail
            }
        }
    }

    fun toggleReview() {
        _uiState.update { it.copy(showReview = !it.showReview) }
    }

    fun backToList() {
        timerJob?.cancel()
        _uiState.update { ExamPracticeUiState() }
        loadPracticeTests()
    }

    fun retry() {
        loadPracticeTests()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }

    fun toBengaliNumber(number: Number): String {
        val bengaliDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
        return number.toString().map { c ->
            if (c.isDigit()) bengaliDigits[c.digitToInt()] else c
        }.joinToString("")
    }

    fun formatTime(seconds: Int): String {
        val mins = seconds / 60
        val secs = seconds % 60
        return "${toBengaliNumber(mins)}:${toBengaliNumber(secs.toString().padStart(2, '0'))}"
    }
}
