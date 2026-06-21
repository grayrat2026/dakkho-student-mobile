package com.dakkho.android.presentation.screens.notificationpreferences

import androidx.lifecycle.ViewModel
import com.dakkho.android.data.db.EncryptedPrefsHelper
import com.dakkho.android.domain.model.NotificationPreferences
import com.dakkho.android.domain.model.NotificationSound
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class NotificationPreferencesUiState(
    val preferences: NotificationPreferences = NotificationPreferences(),
    val showSoundDialog: Boolean = false,
    val showQuietHoursDialog: Boolean = false
)

@HiltViewModel
class NotificationPreferencesViewModel @Inject constructor(
    private val encryptedPrefsHelper: EncryptedPrefsHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationPreferencesUiState())
    val uiState: StateFlow<NotificationPreferencesUiState> = _uiState.asStateFlow()

    init {
        loadPreferences()
    }

    private fun loadPreferences() {
        val prefs = NotificationPreferences(
            isPushEnabled = encryptedPrefsHelper.getBoolean("notif_push", true),
            isEmailEnabled = encryptedPrefsHelper.getBoolean("notif_email", true),
            isCourseUpdatesEnabled = encryptedPrefsHelper.getBoolean("notif_course_updates", true),
            isLiveClassReminderEnabled = encryptedPrefsHelper.getBoolean("notif_live_reminder", true),
            isAssignmentReminderEnabled = encryptedPrefsHelper.getBoolean("notif_assignment", true),
            isDiscussionReplyEnabled = encryptedPrefsHelper.getBoolean("notif_discussion", true),
            isPromotionalEnabled = encryptedPrefsHelper.getBoolean("notif_promo", false),
            isAchievementNotificationEnabled = encryptedPrefsHelper.getBoolean("notif_achievement", true),
            quietHoursEnabled = encryptedPrefsHelper.getBoolean("notif_quiet_hours", false),
            quietHoursStart = encryptedPrefsHelper.getString("notif_quiet_start", "22:00") ?: "22:00",
            quietHoursEnd = encryptedPrefsHelper.getString("notif_quiet_end", "07:00") ?: "07:00",
            notificationSound = NotificationSound.entries.find {
                it.value == encryptedPrefsHelper.getString("notif_sound", "default")
            } ?: NotificationSound.DEFAULT,
            isVibrationEnabled = encryptedPrefsHelper.getBoolean("notif_vibration", true),
            isLedEnabled = encryptedPrefsHelper.getBoolean("notif_led", true)
        )
        _uiState.update { it.copy(preferences = prefs) }
    }

    fun setPushEnabled(enabled: Boolean) {
        encryptedPrefsHelper.saveBoolean("notif_push", enabled)
        _uiState.update { it.copy(preferences = it.preferences.copy(isPushEnabled = enabled)) }
    }

    fun setEmailEnabled(enabled: Boolean) {
        encryptedPrefsHelper.saveBoolean("notif_email", enabled)
        _uiState.update { it.copy(preferences = it.preferences.copy(isEmailEnabled = enabled)) }
    }

    fun setCourseUpdatesEnabled(enabled: Boolean) {
        encryptedPrefsHelper.saveBoolean("notif_course_updates", enabled)
        _uiState.update { it.copy(preferences = it.preferences.copy(isCourseUpdatesEnabled = enabled)) }
    }

    fun setLiveClassReminderEnabled(enabled: Boolean) {
        encryptedPrefsHelper.saveBoolean("notif_live_reminder", enabled)
        _uiState.update { it.copy(preferences = it.preferences.copy(isLiveClassReminderEnabled = enabled)) }
    }

    fun setAssignmentReminderEnabled(enabled: Boolean) {
        encryptedPrefsHelper.saveBoolean("notif_assignment", enabled)
        _uiState.update { it.copy(preferences = it.preferences.copy(isAssignmentReminderEnabled = enabled)) }
    }

    fun setDiscussionReplyEnabled(enabled: Boolean) {
        encryptedPrefsHelper.saveBoolean("notif_discussion", enabled)
        _uiState.update { it.copy(preferences = it.preferences.copy(isDiscussionReplyEnabled = enabled)) }
    }

    fun setPromotionalEnabled(enabled: Boolean) {
        encryptedPrefsHelper.saveBoolean("notif_promo", enabled)
        _uiState.update { it.copy(preferences = it.preferences.copy(isPromotionalEnabled = enabled)) }
    }

    fun setAchievementNotificationEnabled(enabled: Boolean) {
        encryptedPrefsHelper.saveBoolean("notif_achievement", enabled)
        _uiState.update { it.copy(preferences = it.preferences.copy(isAchievementNotificationEnabled = enabled)) }
    }

    fun setQuietHoursEnabled(enabled: Boolean) {
        encryptedPrefsHelper.saveBoolean("notif_quiet_hours", enabled)
        _uiState.update { it.copy(preferences = it.preferences.copy(quietHoursEnabled = enabled)) }
    }

    fun setQuietHoursStart(time: String) {
        encryptedPrefsHelper.saveString("notif_quiet_start", time)
        _uiState.update { it.copy(preferences = it.preferences.copy(quietHoursStart = time)) }
    }

    fun setQuietHoursEnd(time: String) {
        encryptedPrefsHelper.saveString("notif_quiet_end", time)
        _uiState.update { it.copy(preferences = it.preferences.copy(quietHoursEnd = time)) }
    }

    fun setNotificationSound(sound: NotificationSound) {
        encryptedPrefsHelper.saveString("notif_sound", sound.value)
        _uiState.update {
            it.copy(
                preferences = it.preferences.copy(notificationSound = sound),
                showSoundDialog = false
            )
        }
    }

    fun setVibrationEnabled(enabled: Boolean) {
        encryptedPrefsHelper.saveBoolean("notif_vibration", enabled)
        _uiState.update { it.copy(preferences = it.preferences.copy(isVibrationEnabled = enabled)) }
    }

    fun setLedEnabled(enabled: Boolean) {
        encryptedPrefsHelper.saveBoolean("notif_led", enabled)
        _uiState.update { it.copy(preferences = it.preferences.copy(isLedEnabled = enabled)) }
    }

    fun showSoundDialog() { _uiState.update { it.copy(showSoundDialog = true) } }
    fun dismissSoundDialog() { _uiState.update { it.copy(showSoundDialog = false) } }
    fun showQuietHoursDialog() { _uiState.update { it.copy(showQuietHoursDialog = true) } }
    fun dismissQuietHoursDialog() { _uiState.update { it.copy(showQuietHoursDialog = false) } }
}
