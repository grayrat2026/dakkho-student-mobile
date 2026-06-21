package com.dakkho.android.presentation.screens.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.data.api.SocialApiService
import com.dakkho.android.domain.model.LeaderboardEntry
import com.dakkho.android.domain.model.LeaderboardPeriod
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val socialApiService: SocialApiService
) : ViewModel() {

    private val _entries = MutableStateFlow<List<LeaderboardEntry>>(emptyList())
    val entries: StateFlow<List<LeaderboardEntry>> = _entries.asStateFlow()

    private val _currentPeriod = MutableStateFlow(LeaderboardPeriod.WEEKLY)
    val currentPeriod: StateFlow<LeaderboardPeriod> = _currentPeriod.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _myRank = MutableStateFlow<LeaderboardEntry?>(null)
    val myRank: StateFlow<LeaderboardEntry?> = _myRank.asStateFlow()

    init {
        loadLeaderboard()
    }

    fun loadLeaderboard(period: LeaderboardPeriod = _currentPeriod.value) {
        viewModelScope.launch {
            _isLoading.value = true
            _currentPeriod.value = period
            try {
                val response = socialApiService.getLeaderboard(period.value)
                if (response.isSuccessful) {
                    val data = response.body()?.data
                    _entries.value = data?.map { it.toDomain() } ?: emptyList()
                }
                val myRankResponse = socialApiService.getMyRank(period.value)
                if (myRankResponse.isSuccessful) {
                    _myRank.value = myRankResponse.body()?.data?.toDomain()
                }
            } catch (_: Exception) {
                // Fallback: empty list
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onPeriodChanged(period: LeaderboardPeriod) {
        loadLeaderboard(period)
    }
}
