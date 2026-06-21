package com.dakkho.android.presentation.screens.semester

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.domain.model.RoutineEntry
import com.dakkho.android.domain.model.Semester
import com.dakkho.android.domain.model.SemesterProgress
import com.dakkho.android.domain.model.Subject
import com.dakkho.android.domain.repository.SemesterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for a single Semester page within a Department.
 * Parameterized by departmentSlug + semesterNumber.
 *
 * Bangladesh Diploma system: 7 regular semesters + 8th = ইন্টার্নি (Internship).
 * When semesterNumber = 8, the UI shows "ইন্টার্নি" instead of "Semester 8".
 */
@HiltViewModel
class SemesterViewModel @Inject constructor(
    private val semesterRepository: SemesterRepository
) : ViewModel() {

    private val _semester = MutableStateFlow<Semester?>(null)
    val semester: StateFlow<Semester?> = _semester.asStateFlow()

    private val _subjects = MutableStateFlow<List<Subject>>(emptyList())
    val subjects: StateFlow<List<Subject>> = _subjects.asStateFlow()

    private val _routine = MutableStateFlow<List<RoutineEntry>>(emptyList())
    val routine: StateFlow<List<RoutineEntry>> = _routine.asStateFlow()

    private val _progress = MutableStateFlow<SemesterProgress?>(null)
    val progress: StateFlow<SemesterProgress?> = _progress.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Selected tab: "subjects", "routine", "syllabus"
    private val _selectedTab = MutableStateFlow("subjects")
    val selectedTab: StateFlow<String> = _selectedTab.asStateFlow()

    // Expanded subject for syllabus view
    private val _expandedSubjectId = MutableStateFlow<String?>(null)
    val expandedSubjectId: StateFlow<String?> = _expandedSubjectId.asStateFlow()

    private var currentDeptSlug: String = ""
    private var currentSemesterNumber: Int = 0

    /**
     * Whether this semester is the internship semester (8th semester = ইন্টার্নি)
     */
    val isInternship: Boolean get() = currentSemesterNumber == Semester.INTERNSHIP_SEMESTER

    /**
     * Display name for this semester
     */
    val semesterDisplayName: String get() = Semester.semesterName(currentSemesterNumber)

    /**
     * Initialize the ViewModel with department slug and semester number.
     * Called once when navigating to the semester page.
     */
    fun initialize(departmentSlug: String, semesterNumber: Int) {
        if (currentDeptSlug == departmentSlug && currentSemesterNumber == semesterNumber && _semester.value != null) return
        currentDeptSlug = departmentSlug
        currentSemesterNumber = semesterNumber
        loadSemester(departmentSlug, semesterNumber)
    }

    fun loadSemester(departmentSlug: String, semesterNumber: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Load semester info
                val semester = semesterRepository.getSemester(departmentSlug, semesterNumber)
                _semester.value = semester ?: Semester(
                    id = "${departmentSlug}_$semesterNumber",
                    departmentSlug = departmentSlug,
                    number = semesterNumber,
                    name = Semester.semesterName(semesterNumber)
                )

                // Load subjects (reactive Flow)
                semesterRepository.getSubjectsForSemester(departmentSlug, semesterNumber)
                    .collect { subjectsList ->
                        _subjects.value = subjectsList
                    }

                // Load routine (reactive Flow)
                semesterRepository.getRoutineForSemester(departmentSlug, semesterNumber)
                    .collect { routineList ->
                        _routine.value = routineList
                    }

                // Load semester progress
                val progressData = semesterRepository.getSemesterProgress(departmentSlug)
                _progress.value = progressData
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load semester"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                semesterRepository.refreshSemesterData(currentDeptSlug, currentSemesterNumber)
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun selectTab(tab: String) {
        _selectedTab.value = tab
    }

    fun toggleSubjectExpansion(subjectId: String) {
        _expandedSubjectId.value = if (_expandedSubjectId.value == subjectId) null else subjectId
    }

    fun retry() {
        if (currentDeptSlug.isNotBlank() && currentSemesterNumber > 0) {
            loadSemester(currentDeptSlug, currentSemesterNumber)
        }
    }
}
