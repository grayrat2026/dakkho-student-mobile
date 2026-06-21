package com.dakkho.android.presentation.screens.accessibilitysettings

import androidx.lifecycle.ViewModel
import com.dakkho.android.data.db.EncryptedPrefsHelper
import com.dakkho.android.domain.model.AccessibilitySettings
import com.dakkho.android.domain.model.ContentScale
import com.dakkho.android.domain.model.TouchTargetSize
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class AccessibilityUiState(
    val settings: AccessibilitySettings = AccessibilitySettings(),
    val showContentScaleDialog: Boolean = false,
    val showTouchTargetDialog: Boolean = false
)

@HiltViewModel
class AccessibilitySettingsViewModel @Inject constructor(
    private val encryptedPrefsHelper: EncryptedPrefsHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccessibilityUiState())
    val uiState: StateFlow<AccessibilityUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        val settings = AccessibilitySettings(
            isHighContrastEnabled = encryptedPrefsHelper.getBoolean("a11y_high_contrast", false),
            isReduceMotionEnabled = encryptedPrefsHelper.getBoolean("a11y_reduce_motion", false),
            isScreenReaderOptimized = encryptedPrefsHelper.getBoolean("a11y_screen_reader", false),
            contentScale = ContentScale.entries.find {
                it.name == encryptedPrefsHelper.getString("a11y_content_scale", "DEFAULT")
            } ?: ContentScale.DEFAULT,
            isColorInversionEnabled = encryptedPrefsHelper.getBoolean("a11y_color_inversion", false),
            isLargePointerEnabled = encryptedPrefsHelper.getBoolean("a11y_large_pointer", false),
            isBoldTextEnabled = encryptedPrefsHelper.getBoolean("a11y_bold_text", false),
            isSpacingAdjustmentEnabled = encryptedPrefsHelper.getBoolean("a11y_spacing", false),
            minTouchTargetSize = TouchTargetSize.entries.find {
                it.name == encryptedPrefsHelper.getString("a11y_touch_target", "DEFAULT")
            } ?: TouchTargetSize.DEFAULT
        )
        _uiState.update { it.copy(settings = settings) }
    }

    fun setHighContrast(enabled: Boolean) {
        encryptedPrefsHelper.saveBoolean("a11y_high_contrast", enabled)
        _uiState.update { it.copy(settings = it.settings.copy(isHighContrastEnabled = enabled)) }
    }

    fun setReduceMotion(enabled: Boolean) {
        encryptedPrefsHelper.saveBoolean("a11y_reduce_motion", enabled)
        _uiState.update { it.copy(settings = it.settings.copy(isReduceMotionEnabled = enabled)) }
    }

    fun setScreenReaderOptimized(enabled: Boolean) {
        encryptedPrefsHelper.saveBoolean("a11y_screen_reader", enabled)
        _uiState.update { it.copy(settings = it.settings.copy(isScreenReaderOptimized = enabled)) }
    }

    fun setContentScale(scale: ContentScale) {
        encryptedPrefsHelper.saveString("a11y_content_scale", scale.name)
        _uiState.update {
            it.copy(settings = it.settings.copy(contentScale = scale), showContentScaleDialog = false)
        }
    }

    fun setColorInversion(enabled: Boolean) {
        encryptedPrefsHelper.saveBoolean("a11y_color_inversion", enabled)
        _uiState.update { it.copy(settings = it.settings.copy(isColorInversionEnabled = enabled)) }
    }

    fun setLargePointer(enabled: Boolean) {
        encryptedPrefsHelper.saveBoolean("a11y_large_pointer", enabled)
        _uiState.update { it.copy(settings = it.settings.copy(isLargePointerEnabled = enabled)) }
    }

    fun setBoldText(enabled: Boolean) {
        encryptedPrefsHelper.saveBoolean("a11y_bold_text", enabled)
        _uiState.update { it.copy(settings = it.settings.copy(isBoldTextEnabled = enabled)) }
    }

    fun setSpacingAdjustment(enabled: Boolean) {
        encryptedPrefsHelper.saveBoolean("a11y_spacing", enabled)
        _uiState.update { it.copy(settings = it.settings.copy(isSpacingAdjustmentEnabled = enabled)) }
    }

    fun setTouchTargetSize(size: TouchTargetSize) {
        encryptedPrefsHelper.saveString("a11y_touch_target", size.name)
        _uiState.update {
            it.copy(settings = it.settings.copy(minTouchTargetSize = size), showTouchTargetDialog = false)
        }
    }

    fun showContentScaleDialog() { _uiState.update { it.copy(showContentScaleDialog = true) } }
    fun dismissContentScaleDialog() { _uiState.update { it.copy(showContentScaleDialog = false) } }
    fun showTouchTargetDialog() { _uiState.update { it.copy(showTouchTargetDialog = true) } }
    fun dismissTouchTargetDialog() { _uiState.update { it.copy(showTouchTargetDialog = false) } }
}
