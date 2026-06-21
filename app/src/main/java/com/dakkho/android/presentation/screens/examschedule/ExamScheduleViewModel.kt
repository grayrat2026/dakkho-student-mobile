package com.dakkho.android.presentation.screens.examschedule

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.data.api.ExamApiService
import com.dakkho.android.data.db.EncryptedPrefsHelper
import com.dakkho.android.domain.model.ExamModels.ExamSchedule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

data class ExamScheduleUiState(
    val isLoading: Boolean = false,
    val exams: List<ExamSchedule> = emptyList(),
    val filteredExams: List<ExamSchedule> = emptyList(),
    val selectedSemester: Int? = null,
    val availableSemesters: List<Int> = emptyList(),
    val error: String? = null,
    val calendarAddedExamIds: Set<String> = emptySet(),
    val reminderSetExamIds: Set<String> = emptySet()
)

@HiltViewModel
class ExamScheduleViewModel @Inject constructor(
    private val examApiService: ExamApiService,
    private val encryptedPrefsHelper: EncryptedPrefsHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExamScheduleUiState())
    val uiState: StateFlow<ExamScheduleUiState> = _uiState.asStateFlow()

    init {
        loadExamSchedules()
    }

    private fun loadExamSchedules() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val department = encryptedPrefsHelper.getString("department") ?: "cse"
                val response = examApiService.getExamSchedule(department, 0)
                if (response.isSuccessful) {
                    val apiResult = response.body()
                    val exams = apiResult?.data?.map { it.toDomain() } ?: emptyList()
                    val semesters = exams.map { it.semester }.distinct().sorted()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            exams = exams,
                            filteredExams = exams,
                            availableSemesters = semesters
                        )
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
        val sampleExams = listOf(
            ExamSchedule(
                id = "1", subject = "গণিত", subjectCode = "MATH101",
                date = "2025-08-15", time = "সকাল ১০:০০ - দুপুর ১:০০",
                duration = "৩ ঘন্টা", examType = "ফাইনাল", room = "১০১", semester = 1
            ),
            ExamSchedule(
                id = "2", subject = "পদার্থবিজ্ঞান", subjectCode = "PHY101",
                date = "2025-08-18", time = "সকাল ১০:০০ - দুপুর ১:০০",
                duration = "৩ ঘন্টা", examType = "ফাইনাল", room = "২০৩", semester = 1
            ),
            ExamSchedule(
                id = "3", subject = "রসায়ন", subjectCode = "CHEM101",
                date = "2025-08-20", time = "দুপুর ২:০০ - বিকাল ৫:০০",
                duration = "৩ ঘন্টা", examType = "মিডটার্ম", room = "৩০৫", semester = 2
            ),
            ExamSchedule(
                id = "4", subject = "বাংলা", subjectCode = "BAN101",
                date = "2025-09-01", time = "সকাল ৯:০০ - সকাল ১১:০০",
                duration = "২ ঘন্টা", examType = "কুইজ", room = "১০২", semester = 3
            ),
            ExamSchedule(
                id = "5", subject = "ইংরেজি", subjectCode = "ENG101",
                date = "2025-09-10", time = "দুপুর ১:০০ - বিকাল ৪:০০",
                duration = "৩ ঘন্টা", examType = "ফাইনাল", room = "১০৩", semester = 2
            )
        )
        val semesters = sampleExams.map { it.semester }.distinct().sorted()
        _uiState.update {
            it.copy(
                isLoading = false,
                exams = sampleExams,
                filteredExams = sampleExams,
                availableSemesters = semesters
            )
        }
    }

    fun filterBySemester(semester: Int?) {
        val current = _uiState.value
        val filtered = if (semester == null) {
            current.exams
        } else {
            current.exams.filter { it.semester == semester }
        }
        _uiState.update { it.copy(selectedSemester = semester, filteredExams = filtered) }
    }

    fun getDaysLeft(examDate: String): Long {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val exam = sdf.parse(examDate) ?: return -1L
            val now = Date()
            val diff = exam.time - now.time
            TimeUnit.MILLISECONDS.toDays(diff)
        } catch (e: Exception) {
            -1L
        }
    }

    fun markCalendarAdded(examId: String) {
        _uiState.update {
            it.copy(calendarAddedExamIds = it.calendarAddedExamIds + examId)
        }
    }

    fun markReminderSet(examId: String) {
        _uiState.update {
            it.copy(reminderSetExamIds = it.reminderSetExamIds + examId)
        }
    }

    fun toBengaliNumber(number: Number): String {
        val bengaliDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
        return number.toString().map { c ->
            if (c.isDigit()) bengaliDigits[c.digitToInt()] else c
        }.joinToString("")
    }

    fun retry() {
        loadExamSchedules()
    }
}
