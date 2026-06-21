package com.dakkho.android.presentation.screens.instructorcourses

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.domain.model.Course
import com.dakkho.android.domain.repository.InstructorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InstructorCoursesUiState(
    val instructorName: String = "",
    val courses: List<Course> = emptyList(),
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val total: Int = 0,
    val hasMore: Boolean = true
)

@HiltViewModel
class InstructorCoursesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val instructorRepository: InstructorRepository
) : ViewModel() {

    private val instructorId: String = savedStateHandle["instructorId"] ?: ""
    private val instructorName: String = savedStateHandle["instructorName"] ?: ""

    private val _uiState = MutableStateFlow(InstructorCoursesUiState(instructorName = instructorName))
    val uiState: StateFlow<InstructorCoursesUiState> = _uiState.asStateFlow()

    private val pageSize = 20

    init {
        loadCourses()
    }

    fun loadCourses() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val result = instructorRepository.getInstructorCourses(instructorId, pageSize, 0)
            result.fold(
                onSuccess = { (courses, total) ->
                    _uiState.update {
                        it.copy(
                            courses = courses,
                            total = total,
                            hasMore = courses.size < total,
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
                            error = error.message ?: "Failed to load courses"
                        )
                    }
                }
            )
        }
    }

    fun loadMore() {
        val state = _uiState.value
        if (!state.hasMore || state.isLoadingMore) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }

            val offset = state.courses.size
            val result = instructorRepository.getInstructorCourses(instructorId, pageSize, offset)
            result.fold(
                onSuccess = { (newCourses, total) ->
                    _uiState.update {
                        it.copy(
                            courses = it.courses + newCourses,
                            total = total,
                            hasMore = it.courses.size + newCourses.size < total,
                            isLoadingMore = false
                        )
                    }
                },
                onFailure = { _ ->
                    _uiState.update { it.copy(isLoadingMore = false) }
                }
            )
        }
    }

    fun refresh() {
        _uiState.update { it.copy(isRefreshing = true) }
        loadCourses()
    }
}
