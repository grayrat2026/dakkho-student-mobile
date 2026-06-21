package com.dakkho.android.presentation.screens.instructorcontact

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.domain.model.InstructorDetail
import com.dakkho.android.domain.repository.InstructorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InstructorContactUiState(
    val instructorId: String = "",
    val instructorName: String = "",
    val instructor: InstructorDetail? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class InstructorContactViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val instructorRepository: InstructorRepository
) : ViewModel() {

    private val instructorId: String = savedStateHandle["instructorId"] ?: ""
    private val instructorName: String = savedStateHandle["instructorName"] ?: ""

    private val _uiState = MutableStateFlow(
        InstructorContactUiState(
            instructorId = instructorId,
            instructorName = instructorName
        )
    )
    val uiState: StateFlow<InstructorContactUiState> = _uiState.asStateFlow()

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
                            isLoading = false,
                            error = null
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load instructor"
                        )
                    }
                }
            )
        }
    }
}
