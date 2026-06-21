package com.dakkho.android.presentation.screens.instructorlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.domain.model.Instructor
import com.dakkho.android.domain.repository.InstructorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InstructorListUiState(
    val instructors: List<Instructor> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isRefreshing: Boolean = false,
    val searchQuery: String = "",
    val totalInstructors: Int = 0,
    val currentPage: Int = 0,
    val hasMore: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class InstructorListViewModel @Inject constructor(
    private val instructorRepository: InstructorRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InstructorListUiState())
    val uiState: StateFlow<InstructorListUiState> = _uiState.asStateFlow()

    private val pageSize = 20
    private val _searchFlow = MutableStateFlow("")

    init {
        // Observe search query changes with debounce
        viewModelScope.launch {
            _searchFlow
                .debounce(400)
                .distinctUntilChanged()
                .collect { query ->
                    _uiState.update { it.copy(searchQuery = query) }
                    loadInstructors(reset = true)
                }
        }

        // Initial load
        loadInstructors(reset = true)
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        _searchFlow.value = query
    }

    fun loadInstructors(reset: Boolean = false) {
        if (_uiState.value.isLoading || _uiState.value.isLoadingMore) return

        val state = _uiState.value
        val offset = if (reset) 0 else state.instructors.size

        viewModelScope.launch {
            if (reset) {
                _uiState.update { it.copy(isLoading = true, error = null) }
            } else {
                _uiState.update { it.copy(isLoadingMore = true) }
            }

            val result = instructorRepository.getInstructors(
                limit = pageSize,
                offset = offset,
                search = state.searchQuery
            )

            result.fold(
                onSuccess = { (instructors, total) ->
                    _uiState.update {
                        it.copy(
                            instructors = if (reset) instructors else it.instructors + instructors,
                            totalInstructors = total,
                            currentPage = if (reset) 0 else it.currentPage + 1,
                            hasMore = (if (reset) instructors.size else it.instructors.size + instructors.size) < total,
                            isLoading = false,
                            isLoadingMore = false,
                            isRefreshing = false,
                            error = null
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            isRefreshing = false,
                            error = error.message ?: "Failed to load instructors"
                        )
                    }
                }
            )
        }
    }

    fun refresh() {
        _uiState.update { it.copy(isRefreshing = true) }
        loadInstructors(reset = true)
    }

    fun loadMore() {
        val state = _uiState.value
        if (state.hasMore && !state.isLoading && !state.isLoadingMore) {
            loadInstructors(reset = false)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
