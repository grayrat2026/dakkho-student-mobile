package com.dakkho.android.presentation.screens.datasaver

import androidx.lifecycle.ViewModel
import com.dakkho.android.data.db.EncryptedPrefsHelper
import com.dakkho.android.domain.model.DataSaverConfig
import com.dakkho.android.domain.model.ImageDataQuality
import com.dakkho.android.domain.model.MobileVideoQuality
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class DataSaverUiState(
    val config: DataSaverConfig = DataSaverConfig(),
    val showMobileQualityDialog: Boolean = false,
    val showImageQualityDialog: Boolean = false,
    val showDataLimitDialog: Boolean = false
)

@HiltViewModel
class DataSaverViewModel @Inject constructor(
    private val encryptedPrefsHelper: EncryptedPrefsHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(DataSaverUiState())
    val uiState: StateFlow<DataSaverUiState> = _uiState.asStateFlow()

    init {
        loadConfig()
    }

    private fun loadConfig() {
        val config = DataSaverConfig(
            isDataSaverEnabled = encryptedPrefsHelper.getBoolean("data_saver", false),
            mobileVideoQuality = MobileVideoQuality.entries.find {
                it.value == encryptedPrefsHelper.getString("mobile_quality", "360")
            } ?: MobileVideoQuality.LOW,
            isAutoplayOnMobileEnabled = encryptedPrefsHelper.getBoolean("autoplay_mobile", false),
            isImageCompressionEnabled = encryptedPrefsHelper.getBoolean("image_compression", true),
            isBackgroundDataEnabled = encryptedPrefsHelper.getBoolean("background_data", true),
            isDownloadOnMobileEnabled = encryptedPrefsHelper.getBoolean("download_mobile", false),
            imageDataQuality = ImageDataQuality.entries.find {
                it.name == encryptedPrefsHelper.getString("image_quality", "MEDIUM")
            } ?: ImageDataQuality.MEDIUM,
            monthlyDataLimitMB = encryptedPrefsHelper.getString("data_limit_mb", "0")?.toIntOrNull() ?: 0,
            dataUsedThisMonthMB = encryptedPrefsHelper.getString("data_used_mb", "0")?.toFloatOrNull() ?: 0f
        )
        _uiState.update { it.copy(config = config) }
    }

    fun setDataSaverEnabled(enabled: Boolean) {
        encryptedPrefsHelper.saveBoolean("data_saver", enabled)
        _uiState.update { it.copy(config = it.config.copy(isDataSaverEnabled = enabled)) }
    }

    fun setMobileVideoQuality(quality: MobileVideoQuality) {
        encryptedPrefsHelper.saveString("mobile_quality", quality.value)
        _uiState.update {
            it.copy(config = it.config.copy(mobileVideoQuality = quality), showMobileQualityDialog = false)
        }
    }

    fun setAutoplayOnMobile(enabled: Boolean) {
        encryptedPrefsHelper.saveBoolean("autoplay_mobile", enabled)
        _uiState.update { it.copy(config = it.config.copy(isAutoplayOnMobileEnabled = enabled)) }
    }

    fun setImageCompression(enabled: Boolean) {
        encryptedPrefsHelper.saveBoolean("image_compression", enabled)
        _uiState.update { it.copy(config = it.config.copy(isImageCompressionEnabled = enabled)) }
    }

    fun setBackgroundData(enabled: Boolean) {
        encryptedPrefsHelper.saveBoolean("background_data", enabled)
        _uiState.update { it.copy(config = it.config.copy(isBackgroundDataEnabled = enabled)) }
    }

    fun setDownloadOnMobile(enabled: Boolean) {
        encryptedPrefsHelper.saveBoolean("download_mobile", enabled)
        _uiState.update { it.copy(config = it.config.copy(isDownloadOnMobileEnabled = enabled)) }
    }

    fun setImageDataQuality(quality: ImageDataQuality) {
        encryptedPrefsHelper.saveString("image_quality", quality.name)
        _uiState.update {
            it.copy(config = it.config.copy(imageDataQuality = quality), showImageQualityDialog = false)
        }
    }

    fun setMonthlyDataLimit(mb: Int) {
        encryptedPrefsHelper.saveString("data_limit_mb", mb.toString())
        _uiState.update {
            it.copy(config = it.config.copy(monthlyDataLimitMB = mb), showDataLimitDialog = false)
        }
    }

    fun showMobileQualityDialog() { _uiState.update { it.copy(showMobileQualityDialog = true) } }
    fun dismissMobileQualityDialog() { _uiState.update { it.copy(showMobileQualityDialog = false) } }
    fun showImageQualityDialog() { _uiState.update { it.copy(showImageQualityDialog = true) } }
    fun dismissImageQualityDialog() { _uiState.update { it.copy(showImageQualityDialog = false) } }
    fun showDataLimitDialog() { _uiState.update { it.copy(showDataLimitDialog = true) } }
    fun dismissDataLimitDialog() { _uiState.update { it.copy(showDataLimitDialog = false) } }
}
