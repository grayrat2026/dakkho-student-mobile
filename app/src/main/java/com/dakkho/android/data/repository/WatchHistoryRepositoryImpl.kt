package com.dakkho.android.data.repository

import com.dakkho.android.data.api.WatchHistoryApiService
import com.dakkho.android.data.db.EncryptedPrefsHelper
import com.dakkho.android.data.db.dao.VideoBookmarkDao
import com.dakkho.android.data.db.dao.WatchHistoryDao
import com.dakkho.android.data.db.entity.VideoBookmarkEntity
import com.dakkho.android.data.db.entity.WatchHistoryEntity
import com.dakkho.android.domain.model.VideoBookmark
import com.dakkho.android.domain.model.WatchHistoryDto
import com.dakkho.android.domain.model.WatchHistoryItem
import com.dakkho.android.domain.repository.WatchHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WatchHistoryRepositoryImpl @Inject constructor(
    private val watchHistoryApiService: WatchHistoryApiService,
    private val watchHistoryDao: WatchHistoryDao,
    private val videoBookmarkDao: VideoBookmarkDao,
    private val encryptedPrefsHelper: EncryptedPrefsHelper
) : WatchHistoryRepository {

    private fun getCurrentUserId(): String {
        return encryptedPrefsHelper.getUserId() ?: ""
    }

    override fun getWatchHistoryFlow(): Flow<List<WatchHistoryItem>> {
        val userId = getCurrentUserId()
        return watchHistoryDao.getWatchHistoryFlow(userId).map { entities ->
            entities.map { mapEntityToDomain(it) }
        }
    }

    override suspend fun getWatchHistory(): List<WatchHistoryItem> {
        val userId = getCurrentUserId()
        return watchHistoryDao.getWatchHistory(userId).map { mapEntityToDomain(it) }
    }

    override suspend fun getWatchHistoryByCourse(courseId: String): List<WatchHistoryItem> {
        val userId = getCurrentUserId()
        return watchHistoryDao.getByCourseId(courseId, userId).map { mapEntityToDomain(it) }
    }

    override suspend fun getWatchHistoryById(id: String): WatchHistoryItem? {
        // We need to query by id — use flow and get first, or add a new DAO query
        // For simplicity, load all and find
        val userId = getCurrentUserId()
        return watchHistoryDao.getWatchHistory(userId).find { it.id == id }?.let { mapEntityToDomain(it) }
    }

    override suspend fun syncWatchHistory(): Result<List<WatchHistoryItem>> {
        return try {
            val response = watchHistoryApiService.getWatchHistory()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    val dtos = body.data
                    // Cache to Room
                    val userId = getCurrentUserId()
                    watchHistoryDao.insertAll(dtos.map { mapDtoToEntity(it, userId) })
                    Result.success(dtos.map { mapDtoToDomain(it) })
                } else {
                    getCachedWatchHistory()
                }
            } else {
                Timber.e("Sync watch history API error: ${response.code()}")
                getCachedWatchHistory()
            }
        } catch (e: Exception) {
            Timber.e(e, "Sync watch history error, falling back to cache")
            getCachedWatchHistory()
        }
    }

    override suspend fun deleteWatchHistory(id: String): Result<Unit> {
        return try {
            val response = watchHistoryApiService.deleteWatchHistory(id)
            if (response.isSuccessful) {
                watchHistoryDao.deleteById(id)
                Result.success(Unit)
            } else {
                // Still delete locally even if API fails
                watchHistoryDao.deleteById(id)
                Timber.e("Delete watch history API error: ${response.code()}")
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Timber.e(e, "Delete watch history error")
            watchHistoryDao.deleteById(id)
            Result.success(Unit)
        }
    }

    override suspend fun clearAllWatchHistory(): Result<Unit> {
        return try {
            val response = watchHistoryApiService.clearAllWatchHistory()
            if (response.isSuccessful) {
                val userId = getCurrentUserId()
                watchHistoryDao.deleteAllForUser(userId)
                Result.success(Unit)
            } else {
                val userId = getCurrentUserId()
                watchHistoryDao.deleteAllForUser(userId)
                Timber.e("Clear all watch history API error: ${response.code()}")
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Timber.e(e, "Clear all watch history error")
            val userId = getCurrentUserId()
            watchHistoryDao.deleteAllForUser(userId)
            Result.success(Unit)
        }
    }

    private suspend fun getCachedWatchHistory(): Result<List<WatchHistoryItem>> {
        val userId = getCurrentUserId()
        val cached = watchHistoryDao.getWatchHistory(userId)
        return if (cached.isNotEmpty()) {
            Result.success(cached.map { mapEntityToDomain(it) })
        } else {
            Result.failure(Exception("No cached watch history available"))
        }
    }

    private fun mapEntityToDomain(entity: WatchHistoryEntity): WatchHistoryItem {
        return WatchHistoryItem(
            id = entity.id,
            videoId = entity.videoId,
            courseId = entity.courseId,
            videoTitle = entity.videoTitle,
            courseTitle = entity.courseTitle,
            thumbnailUrl = entity.thumbnailUrl,
            progressSeconds = entity.progressSeconds,
            totalSeconds = entity.totalSeconds,
            completed = entity.completed,
            lastWatchedAt = entity.lastWatchedAt
        )
    }

    private fun mapDtoToEntity(dto: WatchHistoryDto, userId: String): WatchHistoryEntity {
        return WatchHistoryEntity(
            id = dto.id,
            userId = userId,
            videoId = dto.videoId,
            courseId = dto.courseId,
            videoTitle = dto.videoTitle ?: "",
            courseTitle = dto.courseTitle ?: "",
            thumbnailUrl = dto.thumbnailUrl,
            progressSeconds = dto.progressSeconds ?: 0,
            totalSeconds = dto.totalSeconds ?: 0,
            completed = dto.completed ?: false,
            lastWatchedAt = dto.lastWatchedAt
        )
    }

    private fun mapDtoToDomain(dto: WatchHistoryDto): WatchHistoryItem {
        return WatchHistoryItem(
            id = dto.id,
            videoId = dto.videoId,
            courseId = dto.courseId,
            videoTitle = dto.videoTitle ?: "",
            courseTitle = dto.courseTitle ?: "",
            thumbnailUrl = dto.thumbnailUrl,
            progressSeconds = dto.progressSeconds ?: 0,
            totalSeconds = dto.totalSeconds ?: 0,
            completed = dto.completed ?: false,
            lastWatchedAt = dto.lastWatchedAt
        )
    }

    // ── Watch Progress ──

    override suspend fun updateWatchProgress(videoId: String, courseId: String, progressSeconds: Int, totalSeconds: Int) {
        try {
            val userId = getCurrentUserId()
            val existing = watchHistoryDao.getWatchHistory(userId).find { it.videoId == videoId }
            if (existing != null) {
                watchHistoryDao.insert(existing.copy(
                    progressSeconds = progressSeconds,
                    totalSeconds = totalSeconds,
                    completed = progressSeconds >= totalSeconds && totalSeconds > 0
                ))
            } else {
                watchHistoryDao.insert(WatchHistoryEntity(
                    id = "${userId}_$videoId",
                    userId = userId,
                    videoId = videoId,
                    courseId = courseId,
                    videoTitle = "",
                    courseTitle = "",
                    progressSeconds = progressSeconds,
                    totalSeconds = totalSeconds,
                    completed = false
                ))
            }
        } catch (e: Exception) {
            Timber.e(e, "Update watch progress error")
        }
    }

    // ── Video Bookmarks ──

    override suspend fun getVideoBookmarks(videoId: String, userId: String): List<VideoBookmark> {
        return videoBookmarkDao.getBookmarksForVideo(videoId, userId).map { mapBookmarkEntityToDomain(it) }
    }

    override fun getVideoBookmarksFlow(videoId: String, userId: String): Flow<List<VideoBookmark>> {
        return videoBookmarkDao.getBookmarksForVideoFlow(videoId, userId).map { entities ->
            entities.map { mapBookmarkEntityToDomain(it) }
        }
    }

    override suspend fun addVideoBookmark(bookmark: VideoBookmarkEntity): Long {
        return videoBookmarkDao.insert(bookmark)
    }

    override suspend fun deleteVideoBookmark(id: Long) {
        videoBookmarkDao.deleteById(id)
    }

    private fun mapBookmarkEntityToDomain(entity: VideoBookmarkEntity): VideoBookmark {
        return VideoBookmark(
            id = entity.id,
            videoId = entity.videoId,
            courseId = entity.courseId,
            userId = entity.userId,
            positionMs = entity.positionMs,
            note = entity.note,
            videoTitle = entity.videoTitle,
            courseTitle = entity.courseTitle,
            thumbnailUrl = entity.thumbnailUrl,
            createdAt = entity.createdAt
        )
    }
}
