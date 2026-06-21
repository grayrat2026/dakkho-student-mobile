package com.dakkho.android.presentation.screens.videoquality

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.data.api.SettingsApiService
import com.dakkho.android.data.db.EncryptedPrefsHelper
import com.dakkho.android.domain.model.StreamingQuality
import com.dakkho.android.domain.model.VideoQualitySettings
import com.dakkho.android.domain.model.VideoQualitySettingsDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class VideoQualityUiState(
    val settings: VideoQualitySettings = VideoQualitySettings(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class VideoQualityViewModel @Inject constructor(
    private val apiService: SettingsApiService,
    private val prefsHelper: EncryptedPrefsHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(VideoQualityUiState())
    val uiState: StateFlow<VideoQualityUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val localSettings = loadFromPrefs()
                _uiState.value = _uiState.value.copy(settings = localSettings, isLoading = false)
                val response = apiService.getVideoQualitySettings()
                if (response.isSuccessful) {
                    response.body()?.data?.let { dto ->
                        val apiSettings = dto.toDomain()
                        _uiState.value = _uiState.value.copy(settings = apiSettings)
                        saveToPrefs(apiSettings)
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to load video quality settings")
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun setStreamingQuality(quality: StreamingQuality) {
        val updated = _uiState.value.settings.copy(streamingQuality = quality)
        _uiState.value = _uiState.value.copy(settings = updated)
        saveToPrefs(updated)
        pushToApi(updated)
    }

    fun setBandwidthSaver(enabled: Boolean) {
        val updated = _uiState.value.settings.copy(isBandwidthSaverEnabled = enabled)
        _uiState.value = _uiState.value.copy(settings = updated)
        saveToPrefs(updated)
        pushToApi(updated)
    }

    private fun pushToApi(settings: VideoQualitySettings) {
        viewModelScope.launch {
            try {
                val dto = VideoQualitySettingsDto(
                    streamingQuality = settings.streamingQuality.value,
                    isBandwidthSaverEnabled = settings.isBandwidthSaverEnabled
                )
                apiService.updateVideoQualitySettings(dto)
            } catch (e: Exception) {
                Timber.e(e, "Failed to push video quality settings")
            }
        }
    }

    private fun saveToPrefs(settings: VideoQualitySettings) {
        prefsHelper.saveString("streaming_quality", settings.streamingQuality.value)
        prefsHelper.saveBoolean("bandwidth_saver", settings.isBandwidthSaverEnabled)
    }

    private fun loadFromPrefs(): VideoQualitySettings {
        val qualityValue = prefsHelper.getString("streaming_quality") ?: "auto"
        val bandwidthSaver = prefsHelper.getBoolean("bandwidth_saver", false)
        return VideoQualitySettings(
            streamingQuality = StreamingQuality.fromValue(qualityValue),
            isBandwidthSaverEnabled = bandwidthSaver
        )
    }
}
