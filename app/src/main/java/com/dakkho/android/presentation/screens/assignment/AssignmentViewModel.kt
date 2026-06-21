package com.dakkho.android.presentation.screens.assignment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.domain.model.AssignmentItem
import com.dakkho.android.domain.repository.AssignmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AssignmentUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val assignments: List<AssignmentItem> = emptyList(),
    val isUploading: Boolean = false,
    val uploadProgress: Float = 0f,
    val uploadingAssignmentId: String? = null,
    val uploadSuccess: Boolean = false
)

@HiltViewModel
class AssignmentViewModel @Inject constructor(
    private val assignmentRepository: AssignmentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AssignmentUiState())
    val uiState: StateFlow<AssignmentUiState> = _uiState.asStateFlow()

    private var currentCourseId: String = ""

    fun setCourseId(courseId: String) {
        if (currentCourseId != courseId) {
            currentCourseId = courseId
            loadAssignments()
        }
    }

    fun loadAssignments() {
        if (currentCourseId.isEmpty()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = assignmentRepository.syncAssignments(currentCourseId)
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        assignments = result.getOrDefault(emptyList())
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Failed to load assignments"
                    )
                }
            }
        }
    }

    fun refresh() {
        if (currentCourseId.isEmpty()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            val result = assignmentRepository.syncAssignments(currentCourseId)
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        isRefreshing = false,
                        assignments = result.getOrDefault(emptyList())
                    )
                }
            } else {
                _uiState.update { it.copy(isRefreshing = false) }
            }
        }
    }

    fun submitAssignmentWithFile(assignmentId: String, filePath: String) {
        if (currentCourseId.isEmpty()) return
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isUploading = true,
                    uploadProgress = 0f,
                    uploadingAssignmentId = assignmentId,
                    uploadSuccess = false
                )
            }
            // Simulate progress updates
            val progressJob = viewModelScope.launch {
                var progress = 0f
                while (progress < 0.9f) {
                    kotlinx.coroutines.delay(200)
                    progress += 0.1f
                    _uiState.update { it.copy(uploadProgress = progress) }
                }
            }

            val result = assignmentRepository.submitAssignment(
                courseId = currentCourseId,
                assignmentId = assignmentId,
                filePath = filePath
            )

            progressJob.cancel()

            if (result.isSuccess) {
                val updatedItem = result.getOrNull()
                _uiState.update {
                    val updatedList = it.assignments.map { assignment ->
                        if (assignment.id == assignmentId && updatedItem != null) {
                            updatedItem
                        } else assignment
                    }
                    it.copy(
                        isUploading = false,
                        uploadProgress = 1f,
                        uploadingAssignmentId = null,
                        uploadSuccess = true,
                        assignments = updatedList
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isUploading = false,
                        uploadProgress = 0f,
                        uploadingAssignmentId = null,
                        error = result.exceptionOrNull()?.message ?: "Upload failed"
                    )
                }
            }
        }
    }

    fun clearUploadSuccess() {
        _uiState.update { it.copy(uploadSuccess = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
