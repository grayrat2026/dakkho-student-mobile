package com.dakkho.android.presentation.screens.learningstats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.domain.model.LearningStats
import com.dakkho.android.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class LearningStatsUiState(
    val stats: LearningStats = LearningStats(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class LearningStatsViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LearningStatsUiState())
    val uiState: StateFlow<LearningStatsUiState> = _uiState.asStateFlow()

    init {
        loadStats()
    }

    fun loadStats() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            profileRepository.getLearningStats()
                .onSuccess { stats ->
                    _uiState.update { it.copy(stats = stats, isLoading = false) }
                }
                .onFailure { e ->
                    Timber.e(e, "Failed to load learning stats")
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }
}
