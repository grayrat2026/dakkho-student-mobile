package com.dakkho.android.presentation.screens.department

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.domain.model.Course
import com.dakkho.android.domain.model.Instructor
import com.dakkho.android.domain.model.Technology
import com.dakkho.android.domain.repository.DepartmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for a single Department page.
 * Parameterized by department slug — loads courses, instructors for that department.
 * All data is dynamic from the API. No hardcoded departments.
 */
@HiltViewModel
class DepartmentViewModel @Inject constructor(
    private val departmentRepository: DepartmentRepository
) : ViewModel() {

    private val _department = MutableStateFlow<Technology?>(null)
    val department: StateFlow<Technology?> = _department.asStateFlow()

    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> = _courses.asStateFlow()

    private val _instructors = MutableStateFlow<List<Instructor>>(emptyList())
    val instructors: StateFlow<List<Instructor>> = _instructors.asStateFlow()

    private val _selectedSemester = MutableStateFlow(0) // 0 = All Semesters
    val selectedSemester: StateFlow<Int> = _selectedSemester.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var currentSlug: String = ""

    /**
     * Initialize the ViewModel with a department slug.
     * Called once when navigating to the department page.
     */
    fun initialize(slug: String) {
        if (currentSlug == slug && _department.value != null) return
        currentSlug = slug
        loadDepartment(slug)
    }

    fun loadDepartment(slug: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Load department info
                val dept = departmentRepository.getDepartmentBySlug(slug)
                _department.value = dept

                if (dept != null) {
                    // Load courses and instructors for this department
                    val techFilter = dept.shortCode.ifBlank { dept.slug }
                    val courses = departmentRepository.getCoursesForDepartment(techFilter)
                    _courses.value = courses

                    val instructors = departmentRepository.getInstructorsForDepartment(techFilter)
                    _instructors.value = instructors
                } else {
                    _error.value = "Department not found"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load department"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                departmentRepository.refreshDepartments()
                loadDepartment(currentSlug)
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun selectSemester(semester: Int) {
        _selectedSemester.value = semester
        // Filter courses by semester if needed
        // For now, semester tabs are visual — actual filtering depends on course data
    }

    fun retry() {
        if (currentSlug.isNotBlank()) {
            loadDepartment(currentSlug)
        }
    }
}
