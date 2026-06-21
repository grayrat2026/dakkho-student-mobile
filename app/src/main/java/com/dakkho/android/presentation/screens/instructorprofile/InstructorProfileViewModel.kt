package com.dakkho.android.presentation.screens.instructorprofile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.domain.model.Course
import com.dakkho.android.domain.model.InstructorDetail
import com.dakkho.android.domain.repository.InstructorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InstructorProfileUiState(
    val instructor: InstructorDetail? = null,
    val courses: List<Course> = emptyList(),
    val isLoading: Boolean = true,
    val isLoadingCourses: Boolean = false,
    val isRefreshing: Boolean = false,
    val selectedTab: Int = 0,
    val coursesTotal: Int = 0,
    val coursesPage: Int = 0,
    val hasMoreCourses: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class InstructorProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val instructorRepository: InstructorRepository
) : ViewModel() {

    private val instructorId: String = savedStateHandle["instructorId"] ?: ""

    private val _uiState = MutableStateFlow(InstructorProfileUiState())
    val uiState: StateFlow<InstructorProfileUiState> = _uiState.asStateFlow()

    private val pageSize = 20

    init {
        loadInstructor()
    }

    fun loadInstructor() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val result = instructorRepository.getInstructorDetail(instructorId)
            result.fold(
                onSuccess = { detail ->
                    _uiState.update {
                        it.copy(
                            instructor = detail,
                            courses = detail.courses,
                            coursesTotal = detail.courseCount,
                            hasMoreCourses = detail.courses.size < detail.courseCount,
                            isLoading = false,
                            isRefreshing = false,
                            error = null
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = error.message ?: "Failed to load instructor profile"
                        )
                    }
                }
            )
        }
    }

    fun loadMoreCourses() {
        val state = _uiState.value
        if (!state.hasMoreCourses || state.isLoadingCourses || state.instructor == null) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingCourses = true) }

            val offset = state.courses.size
            val result = instructorRepository.getInstructorCourses(
                instructorId = instructorId,
                limit = pageSize,
                offset = offset
            )

            result.fold(
                onSuccess = { (newCourses, total) ->
                    _uiState.update {
                        it.copy(
                            courses = it.courses + newCourses,
                            coursesTotal = total,
                            coursesPage = it.coursesPage + 1,
                            hasMoreCourses = it.courses.size + newCourses.size < total,
                            isLoadingCourses = false
                        )
                    }
                },
                onFailure = { _ ->
                    _uiState.update { it.copy(isLoadingCourses = false) }
                }
            )
        }
    }

    fun selectTab(tab: Int) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun refresh() {
        _uiState.update { it.copy(isRefreshing = true) }
        loadInstructor()
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
