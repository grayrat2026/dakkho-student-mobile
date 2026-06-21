package com.dakkho.android.presentation.screens.downloads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.domain.model.DownloadItem
import com.dakkho.android.domain.repository.DownloadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DownloadsUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val showDeleteDialog: Boolean = false,
    val deletingItemId: String? = null,
    val totalStorageUsed: Long = 0L,
    val totalStorageAvailable: Long = 0L
)

@HiltViewModel
class DownloadsViewModel @Inject constructor(
    private val downloadRepository: DownloadRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DownloadsUiState())
    val uiState: StateFlow<DownloadsUiState> = _uiState.asStateFlow()

    /** Room-backed reactive flow for instant local updates */
    val downloadItems: StateFlow<List<DownloadItem>> = downloadRepository
        .getDownloadsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Storage usage reactive flow */
    val storageUsed: StateFlow<Long> = downloadRepository
        .getTotalStorageUsedFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0L)

    init {
        loadDownloads()
    }

    fun loadDownloads() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val totalUsed = downloadRepository.getTotalStorageUsed()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        totalStorageUsed = totalUsed,
                        totalStorageAvailable = estimateTotalStorage()
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load downloads"
                    )
                }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            try {
                val totalUsed = downloadRepository.getTotalStorageUsed()
                _uiState.update {
                    it.copy(
                        isRefreshing = false,
                        totalStorageUsed = totalUsed,
                        totalStorageAvailable = estimateTotalStorage()
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isRefreshing = false) }
            }
        }
    }

    fun showDeleteDialog(id: String) {
        _uiState.update {
            it.copy(showDeleteDialog = true, deletingItemId = id)
        }
    }

    fun dismissDeleteDialog() {
        _uiState.update {
            it.copy(showDeleteDialog = false, deletingItemId = null)
        }
    }

    fun confirmDelete() {
        val id = _uiState.value.deletingItemId ?: return
        viewModelScope.launch {
            val result = downloadRepository.deleteDownload(id)
            if (result.isFailure) {
                _uiState.update {
                    it.copy(
                        error = result.exceptionOrNull()?.message ?: "Failed to delete download",
                        showDeleteDialog = false,
                        deletingItemId = null
                    )
                }
            } else {
                _uiState.update {
                    it.copy(showDeleteDialog = false, deletingItemId = null)
                }
            }
        }
    }

    fun deleteDownload(id: String) {
        viewModelScope.launch {
            val result = downloadRepository.deleteDownload(id)
            if (result.isFailure) {
                _uiState.update {
                    it.copy(error = result.exceptionOrNull()?.message ?: "Failed to delete")
                }
            }
        }
    }

    fun cancelDownload(id: String) {
        viewModelScope.launch {
            downloadRepository.cancelDownload(id)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun estimateTotalStorage(): Long {
        // Default estimate: 8 GB available for downloads
        return 8L * 1024L * 1024L * 1024L
    }

    /** Storage usage as a percentage (0-100) */
    val storagePercent: StateFlow<Int> = combine(
        storageUsed,
        _uiState
    ) { used, state ->
        val total = state.totalStorageAvailable
        if (total > 0) ((used.toFloat() / total) * 100).coerceIn(0f, 100f).toInt() else 0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)
}
