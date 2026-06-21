package com.dakkho.android.presentation.screens.downloadsettings

import android.content.Context
import android.os.Environment
import android.os.StatFs
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.data.api.SettingsApiService
import com.dakkho.android.data.db.EncryptedPrefsHelper
import com.dakkho.android.domain.model.DownloadQuality
import com.dakkho.android.domain.model.DownloadSettings
import com.dakkho.android.domain.model.DownloadSettingsDto
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject

data class DownloadSettingsUiState(
    val settings: DownloadSettings = DownloadSettings(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val showClearCacheDialog: Boolean = false,
    val showClearDownloadsDialog: Boolean = false
)

@HiltViewModel
class DownloadSettingsViewModel @Inject constructor(
    private val apiService: SettingsApiService,
    private val prefsHelper: EncryptedPrefsHelper,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(DownloadSettingsUiState())
    val uiState: StateFlow<DownloadSettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
        refreshStorageStats()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val localSettings = loadFromPrefs()
                _uiState.value = _uiState.value.copy(settings = localSettings, isLoading = false)
                val response = apiService.getDownloadSettings()
                if (response.isSuccessful) {
                    response.body()?.data?.let { dto ->
                        val apiSettings = dto.toDomain()
                        _uiState.value = _uiState.value.copy(settings = apiSettings)
                        saveToPrefs(apiSettings)
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to load download settings")
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun setDownloadQuality(quality: DownloadQuality) {
        val updated = _uiState.value.settings.copy(downloadQuality = quality)
        _uiState.value = _uiState.value.copy(settings = updated)
        saveToPrefs(updated)
        pushToApi(updated)
    }

    fun setAutoDownloadOnWifi(enabled: Boolean) {
        val updated = _uiState.value.settings.copy(isAutoDownloadOnWifi = enabled)
        _uiState.value = _uiState.value.copy(settings = updated)
        saveToPrefs(updated)
        pushToApi(updated)
    }

    fun setStoragePath(uri: String?, label: String?) {
        val updated = _uiState.value.settings.copy(storagePathUri = uri, storagePathLabel = label)
        _uiState.value = _uiState.value.copy(settings = updated)
        saveToPrefs(updated)
        pushToApi(updated)
    }

    fun showClearCacheDialog() {
        _uiState.value = _uiState.value.copy(showClearCacheDialog = true)
    }

    fun dismissClearCacheDialog() {
        _uiState.value = _uiState.value.copy(showClearCacheDialog = false)
    }

    fun showClearDownloadsDialog() {
        _uiState.value = _uiState.value.copy(showClearDownloadsDialog = true)
    }

    fun dismissClearDownloadsDialog() {
        _uiState.value = _uiState.value.copy(showClearDownloadsDialog = false)
    }

    fun clearCache() {
        viewModelScope.launch {
            try {
                val cacheDir = context.cacheDir
                cacheDir.deleteRecursively()
                refreshStorageStats()
            } catch (e: Exception) {
                Timber.e(e, "Failed to clear cache")
            }
            dismissClearCacheDialog()
        }
    }

    fun refreshStorageStats() {
        try {
            val stat = StatFs(Environment.getDataDirectory().path)
            val availableBytes = stat.availableBlocksLong * stat.blockSizeLong
            val cacheSize = calculateDirSize(context.cacheDir)
            val downloadsDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "dakkho")
            val downloadsSize = if (downloadsDir.exists()) calculateDirSize(downloadsDir) else 0L

            val updated = _uiState.value.settings.copy(
                availableStorageBytes = availableBytes,
                usedByCacheBytes = cacheSize,
                usedByDownloadsBytes = downloadsSize
            )
            _uiState.value = _uiState.value.copy(settings = updated)
        } catch (e: Exception) {
            Timber.e(e, "Failed to refresh storage stats")
        }
    }

    private fun calculateDirSize(dir: File): Long {
        if (!dir.exists()) return 0L
        return dir.walkTopDown().filter { it.isFile }.sumOf { it.length() }
    }

    private fun pushToApi(settings: DownloadSettings) {
        viewModelScope.launch {
            try {
                val dto = DownloadSettingsDto(
                    downloadQuality = settings.downloadQuality.value,
                    isAutoDownloadOnWifi = settings.isAutoDownloadOnWifi,
                    storagePathUri = settings.storagePathUri,
                    storagePathLabel = settings.storagePathLabel,
                    availableStorageBytes = settings.availableStorageBytes,
                    usedByDownloadsBytes = settings.usedByDownloadsBytes,
                    usedByCacheBytes = settings.usedByCacheBytes,
                    usedByOtherBytes = settings.usedByOtherBytes
                )
                apiService.updateDownloadSettings(dto)
            } catch (e: Exception) {
                Timber.e(e, "Failed to push download settings")
            }
        }
    }

    private fun saveToPrefs(settings: DownloadSettings) {
        prefsHelper.saveString("download_quality", settings.downloadQuality.value)
        prefsHelper.saveBoolean("auto_download_wifi", settings.isAutoDownloadOnWifi)
        settings.storagePathUri?.let { prefsHelper.saveString("storage_path_uri", it) }
        settings.storagePathLabel?.let { prefsHelper.saveString("storage_path_label", it) }
    }

    private fun loadFromPrefs(): DownloadSettings {
        val qualityValue = prefsHelper.getString("download_quality") ?: "720"
        val autoWifi = prefsHelper.getBoolean("auto_download_wifi", false)
        val pathUri = prefsHelper.getString("storage_path_uri")
        val pathLabel = prefsHelper.getString("storage_path_label")
        return DownloadSettings(
            downloadQuality = DownloadQuality.fromValue(qualityValue),
            isAutoDownloadOnWifi = autoWifi,
            storagePathUri = pathUri,
            storagePathLabel = pathLabel
        )
    }
}
