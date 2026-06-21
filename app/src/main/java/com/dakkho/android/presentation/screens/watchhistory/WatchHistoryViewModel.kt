package com.dakkho.android.presentation.screens.watchhistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.domain.model.WatchHistoryItem
import com.dakkho.android.domain.repository.WatchHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WatchHistoryUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val showClearAllDialog: Boolean = false,
    val deletingItemId: String? = null
)

@HiltViewModel
class WatchHistoryViewModel @Inject constructor(
    private val watchHistoryRepository: WatchHistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WatchHistoryUiState())
    val uiState: StateFlow<WatchHistoryUiState> = _uiState.asStateFlow()

    /** Room-backed reactive flow for instant local updates */
    val watchHistoryItems: StateFlow<List<WatchHistoryItem>> = watchHistoryRepository
        .getWatchHistoryFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        syncWatchHistory()
    }

    fun syncWatchHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = watchHistoryRepository.syncWatchHistory()
            if (result.isFailure) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Failed to load watch history"
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            watchHistoryRepository.syncWatchHistory()
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    fun deleteWatchHistory(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(deletingItemId = id) }
            val result = watchHistoryRepository.deleteWatchHistory(id)
            if (result.isFailure) {
                _uiState.update {
                    it.copy(
                        error = result.exceptionOrNull()?.message ?: "Failed to delete",
                        deletingItemId = null
                    )
                }
            } else {
                _uiState.update { it.copy(deletingItemId = null) }
            }
        }
    }

    fun showClearAllDialog() {
        _uiState.update { it.copy(showClearAllDialog = true) }
    }

    fun dismissClearAllDialog() {
        _uiState.update { it.copy(showClearAllDialog = false) }
    }

    fun clearAllWatchHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(showClearAllDialog = false, isLoading = true) }
            val result = watchHistoryRepository.clearAllWatchHistory()
            if (result.isFailure) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Failed to clear history"
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
