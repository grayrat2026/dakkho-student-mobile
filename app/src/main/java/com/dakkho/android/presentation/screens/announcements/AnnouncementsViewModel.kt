package com.dakkho.android.presentation.screens.announcements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.domain.model.Announcement
import com.dakkho.android.domain.repository.AnnouncementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class AnnouncementsUiState(
    val isLoading: Boolean = true,
    val announcements: List<Announcement> = emptyList(),
    val selectedAnnouncement: Announcement? = null,
    val page: Int = 1,
    val hasMore: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class AnnouncementsViewModel @Inject constructor(
    private val announcementRepository: AnnouncementRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnnouncementsUiState())
    val uiState: StateFlow<AnnouncementsUiState> = _uiState.asStateFlow()

    private var courseId: String = ""

    fun initialize(courseId: String) {
        this.courseId = courseId
        loadAnnouncements()
    }

    fun loadAnnouncements() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            announcementRepository.getAnnouncements(courseId, page = 1)
                .onSuccess { announcements ->
                    _uiState.value = _uiState.value.copy(
                        announcements = announcements,
                        isLoading = false,
                        page = 1,
                        hasMore = announcements.size >= 20
                    )
                }
                .onFailure { error ->
                    Timber.e(error, "Load announcements failed")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load announcements"
                    )
                }
        }
    }

    fun loadMoreAnnouncements() {
        if (_uiState.value.isLoading || !_uiState.value.hasMore) return
        val nextPage = _uiState.value.page + 1
        viewModelScope.launch {
            announcementRepository.getAnnouncements(courseId, page = nextPage)
                .onSuccess { newAnnouncements ->
                    _uiState.value = _uiState.value.copy(
                        announcements = _uiState.value.announcements + newAnnouncements,
                        page = nextPage,
                        hasMore = newAnnouncements.size >= 20
                    )
                }
                .onFailure { error ->
                    Timber.e(error, "Load more announcements failed")
                }
        }
    }

    fun openAnnouncement(announcement: Announcement) {
        _uiState.value = _uiState.value.copy(
            selectedAnnouncement = announcement,
            announcements = _uiState.value.announcements.map {
                if (it.id == announcement.id) it.copy(isRead = true) else it
            }
        )
    }

    fun closeAnnouncement() {
        _uiState.value = _uiState.value.copy(selectedAnnouncement = null)
    }

    fun refresh() {
        loadAnnouncements()
    }
}
