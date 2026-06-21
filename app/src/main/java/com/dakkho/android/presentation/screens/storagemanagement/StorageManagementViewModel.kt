package com.dakkho.android.presentation.screens.storagemanagement

import android.content.Context
import android.os.StatFs
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.data.db.EncryptedPrefsHelper
import com.dakkho.android.domain.model.StorageBreakdown
import com.dakkho.android.domain.model.StorageCategory
import com.dakkho.android.domain.model.StorageInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class StorageManagementUiState(
    val storageInfo: StorageInfo = StorageInfo(),
    val breakdown: List<StorageBreakdown> = emptyList(),
    val isClearingCache: Boolean = false,
    val cacheCleared: Boolean = false,
    val isClearingDownloads: Boolean = false,
    val showClearCacheConfirm: Boolean = false,
    val showClearDownloadsConfirm: Boolean = false
)

@HiltViewModel
class StorageManagementViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val encryptedPrefsHelper: EncryptedPrefsHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(StorageManagementUiState())
    val uiState: StateFlow<StorageManagementUiState> = _uiState.asStateFlow()

    init {
        calculateStorage()
    }

    fun calculateStorage() {
        viewModelScope.launch(Dispatchers.IO) {
            val internalStorage = context.filesDir.absoluteFile
            val cacheDir = context.cacheDir

            val stat = StatFs(internalStorage.path)
            val totalBytes = stat.totalBytes
            val freeBytes = stat.availableBytes
            val usedBytes = totalBytes - freeBytes

            val cacheSize = calculateDirectorySize(cacheDir)
            val downloadSize = calculateDirectorySize(File(context.filesDir, "downloads"))
            val imageSize = calculateDirectorySize(File(context.filesDir, "image_cache"))
            val dbSize = calculateDirectorySize(File(context.filesDir, "../databases"))
            val otherSize = calculateAppSize(internalStorage) -
                    cacheSize - downloadSize - imageSize - dbSize

            val storageInfo = StorageInfo(
                totalStorageBytes = totalBytes,
                usedStorageBytes = usedBytes,
                freeStorageBytes = freeBytes,
                cacheSizeBytes = cacheSize,
                downloadSizeBytes = downloadSize,
                imageDataSizeBytes = imageSize,
                databaseSizeBytes = dbSize,
                otherDataSizeBytes = if (otherSize > 0) otherSize else 0L
            )

            val breakdown = listOf(
                StorageBreakdown(StorageCategory.CACHE, cacheSize, StorageCategory.CACHE.color),
                StorageBreakdown(StorageCategory.DOWNLOADS, downloadSize, StorageCategory.DOWNLOADS.color),
                StorageBreakdown(StorageCategory.IMAGES, imageSize, StorageCategory.IMAGES.color),
                StorageBreakdown(StorageCategory.DATABASE, dbSize, StorageCategory.DATABASE.color),
                StorageBreakdown(StorageCategory.OTHER, if (otherSize > 0) otherSize else 0L, StorageCategory.OTHER.color)
            )

            _uiState.update { it.copy(storageInfo = storageInfo, breakdown = breakdown) }
        }
    }

    private fun calculateDirectorySize(directory: File): Long {
        if (!directory.exists()) return 0L
        var size = 0L
        val files = directory.listFiles() ?: return 0L
        for (file in files) {
            size += if (file.isDirectory) calculateDirectorySize(file) else file.length()
        }
        return size
    }

    private fun calculateAppSize(baseDir: File): Long {
        var size = 0L
        val files = baseDir.listFiles() ?: return 0L
        for (file in files) {
            size += if (file.isDirectory) calculateDirectorySize(file) else file.length()
        }
        return size
    }

    fun showClearCacheConfirm() { _uiState.update { it.copy(showClearCacheConfirm = true) } }
    fun dismissClearCacheConfirm() { _uiState.update { it.copy(showClearCacheConfirm = false) } }

    fun clearCache() {
        _uiState.update { it.copy(isClearingCache = true, showClearCacheConfirm = false) }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                deleteDirectoryContents(context.cacheDir)
                deleteDirectoryContents(File(context.filesDir, "image_cache"))
                _uiState.update { it.copy(isClearingCache = false, cacheCleared = true) }
                calculateStorage()
            } catch (e: Exception) {
                _uiState.update { it.copy(isClearingCache = false) }
            }
        }
    }

    fun showClearDownloadsConfirm() { _uiState.update { it.copy(showClearDownloadsConfirm = true) } }
    fun dismissClearDownloadsConfirm() { _uiState.update { it.copy(showClearDownloadsConfirm = false) } }

    fun clearDownloads() {
        _uiState.update { it.copy(isClearingDownloads = true, showClearDownloadsConfirm = false) }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                deleteDirectoryContents(File(context.filesDir, "downloads"))
                _uiState.update { it.copy(isClearingDownloads = false) }
                calculateStorage()
            } catch (e: Exception) {
                _uiState.update { it.copy(isClearingDownloads = false) }
            }
        }
    }

    private fun deleteDirectoryContents(directory: File) {
        if (!directory.exists()) return
        val files = directory.listFiles() ?: return
        for (file in files) {
            if (file.isDirectory) {
                deleteDirectoryContents(file)
                file.delete()
            } else {
                file.delete()
            }
        }
    }
}
