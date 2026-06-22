package com.dakkho.android.presentation.screens.examprep

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.data.api.ExamApiService
import com.dakkho.android.data.db.EncryptedPrefsHelper
import com.dakkho.android.domain.model.ExamModels.ExamPrepInfo
import com.dakkho.android.domain.model.ExamModels.ImportantTopic
import com.dakkho.android.domain.model.ExamModels.StudyPlanItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExamPrepUiState(
    val examPrepInfo: ExamPrepInfo? = null,
    val importantTopics: List<ImportantTopic> = emptyList(),
    val studyPlan: List<StudyPlanItem> = emptyList(),
    val examDateMillis: Long? = null,
    val remainingDays: Long = 0,
    val remainingHours: Long = 0,
    val remainingMinutes: Long = 0,
    val remainingSeconds: Long = 0,
    val isLoading: Boolean = true,
    val isCountdownActive: Boolean = false,
    val errorMessage: String? = null,
    val checkedTopics: Set<String> = emptySet()
)

@HiltViewModel
class ExamPrepViewModel @Inject constructor(
    private val examApiService: ExamApiService,
    private val encryptedPrefsHelper: EncryptedPrefsHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExamPrepUiState())
    val uiState: StateFlow<ExamPrepUiState> = _uiState.asStateFlow()

    init {
        loadExamPrepData()
    }

    private fun loadExamPrepData() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val userId = encryptedPrefsHelper.getUserId() ?: "anonymous"
                val response = examApiService.getExamPrepInfo(userId, semester = 1)
                if (response.isSuccessful) {
                    val result = response.body()
                    val prepInfo = result?.data
                    if (prepInfo != null) {
                        _uiState.update {
                            it.copy(
                                examPrepInfo = prepInfo,
                                importantTopics = prepInfo.importantTopics,
                                studyPlan = prepInfo.studyPlan,
                                examDateMillis = prepInfo.examDateMillis,
                                isLoading = false,
                                isCountdownActive = prepInfo.examDateMillis != null
                            )
                        }
                        if (prepInfo.examDateMillis != null) {
                            startCountdown(prepInfo.examDateMillis)
                        }
                    } else {
                        loadFallbackData()
                    }
                } else {
                    loadFallbackData()
                }
            } catch (e: Exception) {
                loadFallbackData()
            }
        }
    }

    private fun loadFallbackData() {
        val sampleTopics = listOf(
            ImportantTopic(id = "1", title = "বীজগণিত ও ত্রিকোণমিতি", subject = "গণিত", isCompleted = false),
            ImportantTopic(id = "2", title = "জ্যামিতি ও মাপজোক", subject = "গণিত", isCompleted = false),
            ImportantTopic(id = "3", title = "পদার্থবিজ্ঞান - বল ও গতি", subject = "পদার্থবিজ্ঞান", isCompleted = true),
            ImportantTopic(id = "4", title = "রসায়ন - জৈব রসায়ন", subject = "রসায়ন", isCompleted = false),
            ImportantTopic(id = "5", title = "জীববিজ্ঞান - কোষ বিভাজন", subject = "জীববিজ্ঞান", isCompleted = true),
            ImportantTopic(id = "6", title = "বাংলা সাহিত্যের ইতিহাস", subject = "বাংলা", isCompleted = false),
            ImportantTopic(id = "7", title = "ইংরেজি ব্যাকরণ", subject = "ইংরেজি", isCompleted = false),
            ImportantTopic(id = "8", title = "সাধারণ জ্ঞান ও সমসাময়িক", subject = "সাধারণ জ্ঞান", isCompleted = false)
        )

        val sampleStudyPlan = listOf(
            StudyPlanItem(
                id = "1",
                dayNumber = 1,
                title = "বীজগণিত পুনরালোচনা",
                duration = "2 ঘন্টা",
                topics = listOf("দ্বিঘাত সমীকরণ", "অনুপাত ও সমানুপাত")
            ),
            StudyPlanItem(
                id = "2",
                dayNumber = 2,
                title = "জ্যামিতি অনুশীলন",
                duration = "1.5 ঘন্টা",
                topics = listOf("ত্রিভুজ", "বৃত্তের ধর্ম")
            ),
            StudyPlanItem(
                id = "3",
                dayNumber = 3,
                title = "পদার্থবিজ্ঞান - বল ও গতি",
                duration = "2.5 ঘন্টা",
                topics = listOf("নিউটনের সূত্র", "কাজ ও শক্তি")
            ),
            StudyPlanItem(
                id = "4",
                dayNumber = 4,
                title = "রসায়ন মডেল টেস্ট",
                duration = "2 ঘন্টা",
                topics = listOf("জৈব যৌগ", "রাসায়নিক বন্ধ")
            ),
            StudyPlanItem(
                id = "5",
                dayNumber = 5,
                title = "জীববিজ্ঞান ও সাধারণ বিজ্ঞান",
                duration = "1.5 ঘন্টা",
                topics = listOf("কোষ বিভাজন", "জিনতত্ত্ব")
            ),
            StudyPlanItem(
                id = "6",
                dayNumber = 6,
                title = "বাংলা ও ইংরেজি",
                duration = "1.5 ঘন্টা",
                topics = listOf("সাহিত্য ইতিহাস", "Grammar & Composition")
            ),
            StudyPlanItem(
                id = "7",
                dayNumber = 7,
                title = "ফুল মডেল টেস্ট",
                duration = "3 ঘন্টা",
                topics = listOf("সকল বিষয়", "সময় ব্যবস্থাপনা")
            )
        )

        val examDate = System.currentTimeMillis() + (14L * 24 * 60 * 60 * 1000)
        val checkedSet = sampleTopics.filter { it.isCompleted }.map { it.id }.toSet()

        _uiState.update {
            it.copy(
                importantTopics = sampleTopics,
                studyPlan = sampleStudyPlan,
                examDateMillis = examDate,
                isLoading = false,
                isCountdownActive = true,
                checkedTopics = checkedSet
            )
        }
        startCountdown(examDate)
    }

    private fun startCountdown(examDateMillis: Long) {
        viewModelScope.launch {
            while (true) {
                val now = System.currentTimeMillis()
                val diff = examDateMillis - now

                if (diff <= 0) {
                    _uiState.update {
                        it.copy(
                            remainingDays = 0,
                            remainingHours = 0,
                            remainingMinutes = 0,
                            remainingSeconds = 0,
                            isCountdownActive = false
                        )
                    }
                    break
                }

                val days = diff / (24 * 60 * 60 * 1000)
                val hours = (diff % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000)
                val minutes = (diff % (60 * 60 * 1000)) / (60 * 1000)
                val seconds = (diff % (60 * 1000)) / 1000

                _uiState.update {
                    it.copy(
                        remainingDays = days,
                        remainingHours = hours,
                        remainingMinutes = minutes,
                        remainingSeconds = seconds
                    )
                }

                delay(1000)
            }
        }
    }

    fun toggleTopicChecked(topicId: String) {
        _uiState.update { currentState ->
            val currentChecked = currentState.checkedTopics.toMutableSet()
            if (currentChecked.contains(topicId)) {
                currentChecked.remove(topicId)
            } else {
                currentChecked.add(topicId)
            }
            currentState.copy(checkedTopics = currentChecked)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
