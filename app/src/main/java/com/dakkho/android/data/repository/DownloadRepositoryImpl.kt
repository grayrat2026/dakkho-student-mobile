package com.dakkho.android.data.repository

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.dakkho.android.data.db.dao.DownloadDao
import com.dakkho.android.data.db.entity.DownloadEntity
import com.dakkho.android.data.work.DownloadWorker
import com.dakkho.android.domain.model.DownloadItem
import com.dakkho.android.domain.repository.DownloadRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val downloadDao: DownloadDao,
    private val workManager: WorkManager
) : DownloadRepository {

    override fun getDownloadsFlow(): Flow<List<DownloadItem>> {
        return downloadDao.getAllFlow().map { entities ->
            entities.map { mapEntityToDomain(it) }
        }
    }

    override suspend fun getDownloads(): List<DownloadItem> {
        return downloadDao.getAll().map { mapEntityToDomain(it) }
    }

    override suspend fun getDownloadById(id: String): DownloadItem? {
        return downloadDao.getById(id)?.let { mapEntityToDomain(it) }
    }

    override suspend fun getDownloadsByCourseId(courseId: String): List<DownloadItem> {
        return downloadDao.getByCourseId(courseId).map { mapEntityToDomain(it) }
    }

    override suspend fun insertDownload(download: DownloadItem): Result<Unit> {
        return try {
            downloadDao.insert(mapDomainToEntity(download))
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Insert download error")
            Result.failure(e)
        }
    }

    override suspend fun deleteDownload(id: String): Result<Unit> {
        return try {
            val entity = downloadDao.getById(id)
            // Delete file from storage
            entity?.filePath?.let { path ->
                val file = File(path)
                if (file.exists()) {
                    val deleted = file.delete()
                    if (!deleted) {
                        Timber.w("Failed to delete file: $path")
                    }
                }
                // Also delete encrypted file if exists
                val encryptedFile = File("$path.enc")
                if (encryptedFile.exists()) {
                    encryptedFile.delete()
                }
            }
            // Cancel WorkManager request if active
            workManager.cancelUniqueWork("download_$id")
            // Remove from Room
            downloadDao.deleteById(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Delete download error")
            Result.failure(e)
        }
    }

    override suspend fun deleteAllDownloads(): Result<Unit> {
        return try {
            val allDownloads = downloadDao.getAll()
            allDownloads.forEach { entity ->
                entity.filePath?.let { path ->
                    val file = File(path)
                    if (file.exists()) file.delete()
                    val encryptedFile = File("$path.enc")
                    if (encryptedFile.exists()) encryptedFile.delete()
                }
            }
            workManager.cancelAllWork()
            downloadDao.deleteAll()
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Delete all downloads error")
            Result.failure(e)
        }
    }

    override suspend fun updateDownloadProgress(
        id: String,
        downloadedBytes: Long,
        status: String
    ): Result<Unit> {
        return try {
            val entity = downloadDao.getById(id) ?: return Result.failure(
                Exception("Download not found: $id")
            )
            downloadDao.insert(entity.copy(downloadedBytes = downloadedBytes, status = status))
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Update download progress error")
            Result.failure(e)
        }
    }

    override suspend fun updateDownloadStatus(
        id: String,
        status: String,
        filePath: String?
    ): Result<Unit> {
        return try {
            val entity = downloadDao.getById(id) ?: return Result.failure(
                Exception("Download not found: $id")
            )
            downloadDao.insert(
                entity.copy(
                    status = status,
                    filePath = filePath ?: entity.filePath,
                    completedAt = if (status == "completed") System.currentTimeMillis() else null
                )
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Update download status error")
            Result.failure(e)
        }
    }

    override fun getTotalStorageUsedFlow(): Flow<Long> {
        return downloadDao.getAllFlow().map { entities ->
            entities.sumOf { it.fileSizeBytes }
        }
    }

    override suspend fun getTotalStorageUsed(): Long {
        return downloadDao.getAll().sumOf { it.fileSizeBytes }
    }

    override suspend fun enqueueDownload(
        videoId: String,
        courseId: String,
        title: String,
        videoUrl: String,
        thumbnailUrl: String?,
        fileSizeBytes: Long
    ): Result<String> {
        return try {
            // Check if already downloaded
            val existing = downloadDao.getByVideoId(videoId)
            if (existing != null && existing.status == "completed") {
                return Result.failure(Exception("Video already downloaded"))
            }

            val downloadId = existing?.id ?: UUID.randomUUID().toString()

            // Insert or update the download entity
            val entity = existing?.copy(
                status = "pending",
                downloadedBytes = 0
            ) ?: DownloadEntity(
                id = downloadId,
                videoId = videoId,
                courseId = courseId,
                title = title,
                filePath = null,
                fileSizeBytes = fileSizeBytes,
                downloadedBytes = 0,
                status = "pending",
                thumbnailUrl = thumbnailUrl,
                completedAt = null
            )
            downloadDao.insert(entity)

            // Create WorkManager request
            val inputData = Data.Builder()
                .putString(DownloadWorker.KEY_DOWNLOAD_ID, downloadId)
                .putString(DownloadWorker.KEY_VIDEO_ID, videoId)
                .putString(DownloadWorker.KEY_COURSE_ID, courseId)
                .putString(DownloadWorker.KEY_TITLE, title)
                .putString(DownloadWorker.KEY_VIDEO_URL, videoUrl)
                .putLong(DownloadWorker.KEY_FILE_SIZE, fileSizeBytes)
                .putString(DownloadWorker.KEY_THUMBNAIL_URL, thumbnailUrl)
                .build()

            val workRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
                .setInputData(inputData)
                .build()

            workManager.enqueue(workRequest)

            Timber.d("Enqueued download: $downloadId for video: $videoId")
            Result.success(downloadId)
        } catch (e: Exception) {
            Timber.e(e, "Enqueue download error")
            Result.failure(e)
        }
    }

    override suspend fun cancelDownload(id: String): Result<Unit> {
        return try {
            workManager.cancelUniqueWork("download_$id")
            val entity = downloadDao.getById(id)
            if (entity != null) {
                downloadDao.insert(entity.copy(status = "failed"))
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Cancel download error")
            Result.failure(e)
        }
    }

    private fun mapEntityToDomain(entity: DownloadEntity): DownloadItem {
        return DownloadItem(
            id = entity.id,
            videoId = entity.videoId,
            courseId = entity.courseId,
            title = entity.title,
            filePath = entity.filePath,
            fileSizeBytes = entity.fileSizeBytes,
            downloadedBytes = entity.downloadedBytes,
            status = entity.status,
            thumbnailUrl = entity.thumbnailUrl,
            createdAt = entity.createdAt,
            completedAt = entity.completedAt
        )
    }

    private fun mapDomainToEntity(item: DownloadItem): DownloadEntity {
        return DownloadEntity(
            id = item.id,
            videoId = item.videoId,
            courseId = item.courseId,
            title = item.title,
            filePath = item.filePath,
            fileSizeBytes = item.fileSizeBytes,
            downloadedBytes = item.downloadedBytes,
            status = item.status,
            thumbnailUrl = item.thumbnailUrl,
            createdAt = item.createdAt,
            completedAt = item.completedAt
        )
    }
}
