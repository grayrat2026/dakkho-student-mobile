package com.dakkho.android.presentation.screens.curriculum

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.domain.model.Curriculum
import com.dakkho.android.domain.model.Lesson
import com.dakkho.android.domain.model.Subject
import com.dakkho.android.domain.model.SubjectClass
import com.dakkho.android.domain.model.Unit
import com.dakkho.android.domain.repository.CourseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * UI state for the full-screen Course Curriculum page.
 */
data class CurriculumUiState(
    val isLoading: Boolean = true,
    val curriculum: Curriculum? = null,
    val isEnrolled: Boolean = false,
    val courseTitle: String = "",
    val expandedSubjects: Set<String> = emptySet(),
    val expandedClasses: Set<String> = emptySet(),
    val expandedUnits: Set<String> = emptySet(),
    val completedLessons: Set<String> = emptySet(),
    val downloadingLessons: Set<String> = emptySet(),
    val error: String? = null
) {
    /** Total lessons across the entire curriculum tree. */
    val totalLessons: Int
        get() = curriculum?.sections?.sumOf { subject ->
            subject.classes.sumOf { cls -> cls.units.sumOf { unit -> unit.lessons.size } }
        } ?: 0

    /** Completed lessons count. */
    val completedCount: Int
        get() = curriculum?.sections?.sumOf { subject ->
            subject.classes.sumOf { cls ->
                cls.units.sumOf { unit ->
                    unit.lessons.count { it.isCompleted || completedLessons.contains(it.id) }
                }
            }
        } ?: 0

    /** Overall progress as a fraction 0..1. */
    val overallProgress: Float
        get() = if (totalLessons == 0) 0f else completedCount.toFloat() / totalLessons

    /** Total duration in seconds across all lessons. */
    val totalDurationSeconds: Int
        get() = curriculum?.sections?.sumOf { subject ->
            subject.classes.sumOf { cls ->
                cls.units.sumOf { unit ->
                    unit.lessons.mapNotNull { it.durationSeconds }.sum()
                }
            }
        } ?: 0
}

@HiltViewModel
class CourseCurriculumViewModel @Inject constructor(
    private val courseRepository: CourseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CurriculumUiState())
    val uiState: StateFlow<CurriculumUiState> = _uiState.asStateFlow()

    private var courseId: String = ""

    fun initialize(courseId: String, courseTitle: String, isEnrolled: Boolean) {
        this.courseId = courseId
        _uiState.value = _uiState.value.copy(
            courseTitle = courseTitle,
            isEnrolled = isEnrolled
        )
        loadCurriculum()
    }

    fun loadCurriculum() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            courseRepository.getCourseCurriculum(courseId)
                .onSuccess { curriculum ->
                    // Auto-expand all subjects initially
                    val expandedSubjects = curriculum.sections.map { it.id }.toSet()
                    // Auto-expand all classes
                    val expandedClasses = curriculum.sections
                        .flatMap { it.classes }
                        .map { it.id }
                        .toSet()
                    // Collect completed lesson IDs
                    val completedLessons = curriculum.sections
                        .flatMap { it.classes }
                        .flatMap { it.units }
                        .flatMap { it.lessons }
                        .filter { it.isCompleted }
                        .map { it.id }
                        .toSet()

                    _uiState.value = _uiState.value.copy(
                        curriculum = curriculum,
                        isLoading = false,
                        expandedSubjects = expandedSubjects,
                        expandedClasses = expandedClasses,
                        expandedUnits = emptySet(),
                        completedLessons = completedLessons
                    )
                }
                .onFailure { error ->
                    Timber.e(error, "Load curriculum failed")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load curriculum"
                    )
                }
        }
    }

    fun toggleSubject(subjectId: String) {
        val current = _uiState.value.expandedSubjects
        _uiState.value = _uiState.value.copy(
            expandedSubjects = if (current.contains(subjectId)) {
                current - subjectId
            } else {
                current + subjectId
            }
        )
    }

    fun toggleClass(classId: String) {
        val current = _uiState.value.expandedClasses
        _uiState.value = _uiState.value.copy(
            expandedClasses = if (current.contains(classId)) {
                current - classId
            } else {
                current + classId
            }
        )
    }

    fun toggleUnit(unitId: String) {
        val current = _uiState.value.expandedUnits
        _uiState.value = _uiState.value.copy(
            expandedUnits = if (current.contains(unitId)) {
                current - unitId
            } else {
                current + unitId
            }
        )
    }

    fun expandAll() {
        val curriculum = _uiState.value.curriculum ?: return
        _uiState.value = _uiState.value.copy(
            expandedSubjects = curriculum.sections.map { it.id }.toSet(),
            expandedClasses = curriculum.sections.flatMap { it.classes }.map { it.id }.toSet(),
            expandedUnits = curriculum.sections.flatMap { it.classes }.flatMap { it.units }.map { it.id }.toSet()
        )
    }

    fun collapseAll() {
        _uiState.value = _uiState.value.copy(
            expandedSubjects = emptySet(),
            expandedClasses = emptySet(),
            expandedUnits = emptySet()
        )
    }

    fun toggleDownload(lessonId: String) {
        val current = _uiState.value.downloadingLessons
        _uiState.value = _uiState.value.copy(
            downloadingLessons = if (current.contains(lessonId)) {
                current - lessonId
            } else {
                current + lessonId
            }
        )
        // TODO: Enqueue WorkManager download when download infrastructure is ready
    }

    fun retry() {
        loadCurriculum()
    }
}
