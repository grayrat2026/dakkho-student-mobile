package com.dakkho.android.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.data.db.dao.EnrollmentDao
import com.dakkho.android.data.db.EncryptedPrefsHelper
import com.dakkho.android.domain.model.User
import com.dakkho.android.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class ProfileStats(
    val coursesEnrolled: Int = 0,
    val hoursWatched: Int = 0,
    val streakDays: Int = 0
)

data class ProfileUiState(
    val user: User? = null,
    val stats: ProfileStats = ProfileStats(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val enrollmentDao: EnrollmentDao,
    private val encryptedPrefsHelper: EncryptedPrefsHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            authRepository.getProfile()
                .onSuccess { user ->
                    _uiState.update { it.copy(user = user, isLoading = false) }
                    loadStats(user.id)
                }
                .onFailure { e ->
                    Timber.e(e, "Failed to load profile")
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    private fun loadStats(userId: String) {
        viewModelScope.launch {
            try {
                val enrollments = enrollmentDao.getEnrollmentsForUser(userId)
                val courseCount = enrollments.size
                val hours = enrollments.size * 2 // placeholder calculation
                _uiState.update { it.copy(stats = ProfileStats(
                    coursesEnrolled = courseCount,
                    hoursWatched = hours,
                    streakDays = 0 // Will be calculated from watch history later
                ))}
            } catch (e: Exception) {
                Timber.e(e, "Failed to load profile stats")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}
