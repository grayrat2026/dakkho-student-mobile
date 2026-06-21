package com.dakkho.android.domain.repository

import com.dakkho.android.domain.model.DownloadItem
import kotlinx.coroutines.flow.Flow

interface DownloadRepository {

    fun getDownloadsFlow(): Flow<List<DownloadItem>>

    suspend fun getDownloads(): List<DownloadItem>

    suspend fun getDownloadById(id: String): DownloadItem?

    suspend fun getDownloadsByCourseId(courseId: String): List<DownloadItem>

    suspend fun insertDownload(download: DownloadItem): Result<Unit>

    suspend fun deleteDownload(id: String): Result<Unit>

    suspend fun deleteAllDownloads(): Result<Unit>

    suspend fun updateDownloadProgress(id: String, downloadedBytes: Long, status: String): Result<Unit>

    suspend fun updateDownloadStatus(id: String, status: String, filePath: String? = null): Result<Unit>

    fun getTotalStorageUsedFlow(): Flow<Long>

    suspend fun getTotalStorageUsed(): Long

    suspend fun enqueueDownload(
        videoId: String,
        courseId: String,
        title: String,
        videoUrl: String,
        thumbnailUrl: String?,
        fileSizeBytes: Long
    ): Result<String>

    suspend fun cancelDownload(id: String): Result<Unit>
}
