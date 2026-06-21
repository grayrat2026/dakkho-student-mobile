package com.dakkho.android.presentation.screens.livesessions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.domain.model.LiveClass
import com.dakkho.android.domain.model.LiveClassStatus
import com.dakkho.android.domain.repository.LiveClassRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LiveSessionsUiState(
    val isLoading: Boolean = false,
    val upcomingSessions: List<LiveClass> = emptyList(),
    val activeSessions: List<LiveClass> = emptyList(),
    val recordedSessions: List<LiveClass> = emptyList(),
    val featuredSessions: List<LiveClass> = emptyList(),
    val selectedTab: Int = 0,
    val error: String? = null,
    val joiningClassId: String? = null,
    val joinResult: com.dakkho.android.domain.repository.LiveClassJoinResult? = null,
    val joinError: String? = null,
    val reminderTogglingId: String? = null
)

@HiltViewModel
class LiveSessionsViewModel @Inject constructor(
    private val liveClassRepository: LiveClassRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LiveSessionsUiState())
    val uiState: StateFlow<LiveSessionsUiState> = _uiState.asStateFlow()

    init {
        loadAllSessions()
    }

    fun loadAllSessions() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val upcomingResult = liveClassRepository.getLiveClasses(status = "scheduled")
                val activeResult = liveClassRepository.getLiveClasses(status = "live")
                val recordedResult = liveClassRepository.getLiveClasses(status = "ended")

                val upcoming = upcomingResult.getOrDefault(emptyList())
                val active = activeResult.getOrDefault(emptyList())
                val recorded = recordedResult.getOrDefault(emptyList())

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    upcomingSessions = upcoming,
                    activeSessions = active,
                    recordedSessions = recorded
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load sessions"
                )
            }
        }
    }

    fun loadFeaturedSessions() {
        viewModelScope.launch {
            val result = liveClassRepository.getFeaturedLiveClasses()
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    featuredSessions = result.getOrDefault(emptyList())
                )
            }
        }
    }

    fun selectTab(tab: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }

    fun joinLiveClass(liveClassId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(joiningClassId = liveClassId, joinError = null, joinResult = null)
            val result = liveClassRepository.joinLiveClass(liveClassId)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    joiningClassId = null,
                    joinResult = result.getOrNull()
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    joiningClassId = null,
                    joinError = result.exceptionOrNull()?.message ?: "Failed to join"
                )
            }
        }
    }

    fun clearJoinResult() {
        _uiState.value = _uiState.value.copy(joinResult = null, joinError = null)
    }

    fun toggleReminder(liveClassId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(reminderTogglingId = liveClassId)
            val result = liveClassRepository.toggleReminder(liveClassId)
            _uiState.value = _uiState.value.copy(reminderTogglingId = null)
            if (result.isFailure) {
                _uiState.value = _uiState.value.copy(
                    error = result.exceptionOrNull()?.message ?: "Failed to toggle reminder"
                )
            }
        }
    }

    fun refresh() {
        loadAllSessions()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null, joinError = null)
    }
}
