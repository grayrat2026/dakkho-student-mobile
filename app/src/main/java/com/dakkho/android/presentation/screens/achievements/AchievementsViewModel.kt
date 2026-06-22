package com.dakkho.android.presentation.screens.achievements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.domain.model.Achievement
import com.dakkho.android.domain.repository.AchievementData
import com.dakkho.android.domain.model.AchievementMilestone
import com.dakkho.android.domain.model.StreakData
import com.dakkho.android.domain.repository.AchievementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AchievementsUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val achievements: List<Achievement> = emptyList(),
    val totalXp: Int = 0,
    val unlockedCount: Int = 0,
    val totalCount: Int = 0,
    val milestones: List<AchievementMilestone> = emptyList(),
    val streak: StreakData = StreakData(),
    val selectedCategory: String? = null,
    val categories: List<String> = emptyList(),
    val newlyUnlockedBadge: Achievement? = null,
    val error: String? = null
)

@HiltViewModel
class AchievementsViewModel @Inject constructor(
    private val achievementRepository: AchievementRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AchievementsUiState())
    val uiState: StateFlow<AchievementsUiState> = _uiState.asStateFlow()

    init {
        loadAchievements()
    }

    fun loadAchievements() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = achievementRepository.getAchievements()
            if (result.isSuccess) {
                val data = result.getOrNull()!!
                val categories = data.achievements.map { it.category }.distinct()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    achievements = data.achievements,
                    totalXp = data.totalXp,
                    unlockedCount = data.unlockedCount,
                    totalCount = data.totalCount,
                    milestones = data.milestones,
                    streak = data.streak,
                    categories = categories
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "Failed to load achievements"
                )
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true)
            val result = achievementRepository.getAchievements()
            if (result.isSuccess) {
                val data = result.getOrNull()!!
                val previousUnlocked = _uiState.value.unlockedCount
                val categories = data.achievements.map { it.category }.distinct()

                // Check for newly unlocked badge
                val newlyUnlocked = if (data.unlockedCount > previousUnlocked) {
                    data.achievements.firstOrNull { it.isRecentlyUnlocked }
                } else null

                _uiState.value = _uiState.value.copy(
                    isRefreshing = false,
                    achievements = data.achievements,
                    totalXp = data.totalXp,
                    unlockedCount = data.unlockedCount,
                    totalCount = data.totalCount,
                    milestones = data.milestones,
                    streak = data.streak,
                    categories = categories,
                    newlyUnlockedBadge = newlyUnlocked
                )
            } else {
                _uiState.value = _uiState.value.copy(isRefreshing = false)
            }
        }
    }

    fun selectCategory(category: String?) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun clearNewlyUnlockedBadge() {
        _uiState.value = _uiState.value.copy(newlyUnlockedBadge = null)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /** Get filtered achievements based on selected category */
    fun getFilteredAchievements(): List<Achievement> {
        val category = _uiState.value.selectedCategory
        return if (category == null) {
            _uiState.value.achievements
        } else {
            _uiState.value.achievements.filter { it.category == category }
        }
    }
}
