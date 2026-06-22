package com.dakkho.android.presentation.screens.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.domain.model.Curriculum
import com.dakkho.android.domain.model.WatchHistoryItem
import com.dakkho.android.domain.repository.CourseRepository
import com.dakkho.android.domain.repository.WatchHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class WeeklyStudyDay(
    val dayName: String,
    val hoursStudied: Float,
    val isToday: Boolean = false
)

data class LearningPathStep(
    val title: String,
    val isCompleted: Boolean,
    val isCurrent: Boolean = false
)

data class CourseProgressUiState(
    val isLoading: Boolean = true,
    val courseId: String = "",
    val courseTitle: String = "",
    val progressPercent: Int = 0,
    val totalLessons: Int = 0,
    val completedLessons: Int = 0,
    val totalHours: Float = 0f,
    val studiedHours: Float = 0f,
    val weeklyStudyData: List<WeeklyStudyDay> = emptyList(),
    val learningPath: List<LearningPathStep> = emptyList(),
    val watchHistory: List<WatchHistoryItem> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class CourseProgressViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val watchHistoryRepository: WatchHistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CourseProgressUiState())
    val uiState: StateFlow<CourseProgressUiState> = _uiState.asStateFlow()

    private var courseId: String = ""

    fun initialize(courseId: String, courseTitle: String = "") {
        this.courseId = courseId
        _uiState.value = _uiState.value.copy(courseId = courseId, courseTitle = courseTitle)
        loadProgress()
    }

    fun loadProgress() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                // Load curriculum to calculate total/completed lessons
                val curriculumResult = courseRepository.getCourseCurriculum(courseId)
                val curriculum = curriculumResult.getOrNull()

                // Load watch history for study time calculation
                val history = watchHistoryRepository.getWatchHistory()

                // Filter history for this course
                val courseHistory = history.filter { it.courseId == courseId }

                // Calculate progress from curriculum (Subject → Class → Unit → Lesson)
                var totalLessons = 0
                var completedLessons = 0
                val learningPathSteps = mutableListOf<LearningPathStep>()

                curriculum?.let { curr ->
                    for (subject in curr.sections) {
                        var subjectTotal = 0
                        var subjectCompleted = 0

                        for (cls in subject.classes) {
                            for (unit in cls.units) {
                                for (lesson in unit.lessons) {
                                    totalLessons++
                                    subjectTotal++
                                    if (lesson.isCompleted) {
                                        completedLessons++
                                        subjectCompleted++
                                    }
                                }
                            }
                        }

                        if (subjectTotal > 0) {
                            learningPathSteps.add(
                                LearningPathStep(
                                    title = subject.title,
                                    isCompleted = subjectCompleted == subjectTotal,
                                    isCurrent = subjectCompleted > 0 && subjectCompleted < subjectTotal
                                )
                            )
                        }
                    }
                }

                val progressPercent = if (totalLessons > 0) {
                    ((completedLessons.toFloat() / totalLessons) * 100).toInt().coerceIn(0, 100)
                } else 0

                // Calculate total course hours
                val totalHours = curriculum?.sections?.sumOf { subject ->
                    subject.classes.sumOf { cls ->
                        cls.units.sumOf { unit ->
                            unit.lessons.sumOf { lesson ->
                                (lesson.durationSeconds ?: 0) / 3600.0
                            }
                        }
                    }
                }?.toFloat() ?: 0f

                // Calculate studied hours from watch history
                val studiedHours = courseHistory.sumOf { item ->
                    item.progressSeconds / 3600.0
                }.toFloat()

                // Build weekly study data (last 7 days)
                val weeklyData = buildWeeklyStudyData(courseHistory)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    progressPercent = progressPercent,
                    totalLessons = totalLessons,
                    completedLessons = completedLessons,
                    totalHours = totalHours,
                    studiedHours = studiedHours,
                    weeklyStudyData = weeklyData,
                    learningPath = learningPathSteps,
                    watchHistory = courseHistory
                )
            } catch (e: Exception) {
                Timber.e(e, "Load progress failed")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load progress"
                )
            }
        }
    }

    private fun buildWeeklyStudyData(history: List<WatchHistoryItem>): List<WeeklyStudyDay> {
        val dayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        val calendar = java.util.Calendar.getInstance()
        val todayDayOfWeek = (calendar.get(java.util.Calendar.DAY_OF_WEEK) + 5) % 7 // 0=Mon, 6=Sun

        // Calculate hours studied per day for the last 7 days
        val dailyHours = mutableMapOf<Int, Float>()
        val now = System.currentTimeMillis()
        val sevenDaysAgo = now - (7 * 24 * 60 * 60 * 1000L)

        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        for (item in history) {
            try {
                val watchedAt = item.lastWatchedAt ?: continue
                val date = sdf.parse(watchedAt.substring(0, 10))
                if (date != null && date.time > sevenDaysAgo) {
                    val cal = java.util.Calendar.getInstance().apply { time = date }
                    val dayIndex = (cal.get(java.util.Calendar.DAY_OF_WEEK) + 5) % 7
                    val hours = item.progressSeconds.toFloat() / 3600f
                    dailyHours[dayIndex] = (dailyHours[dayIndex] ?: 0f) + hours
                }
            } catch (e: Exception) {
                // Skip items with unparseable dates
            }
        }

        return dayNames.mapIndexed { index, name ->
            WeeklyStudyDay(
                dayName = name,
                hoursStudied = dailyHours[index] ?: 0f,
                isToday = index == todayDayOfWeek
            )
        }
    }
}
