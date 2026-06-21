package com.dakkho.android.data.repository

import com.dakkho.android.data.api.NotificationApiService
import com.dakkho.android.data.db.dao.NotificationDao
import com.dakkho.android.data.db.entity.NotificationEntity
import com.dakkho.android.domain.model.NotificationDto
import com.dakkho.android.domain.model.NotificationItem
import com.dakkho.android.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val notificationApiService: NotificationApiService,
    private val notificationDao: NotificationDao
) : NotificationRepository {

    override fun getNotificationsFlow(): Flow<List<NotificationItem>> {
        return notificationDao.getAllFlow().map { entities ->
            entities.map { mapEntityToDomain(it) }
        }
    }

    override fun getUnreadCount(): Flow<Int> {
        return notificationDao.getUnreadCount()
    }

    override suspend fun getNotificationById(id: String): NotificationItem? {
        return notificationDao.getById(id)?.let { mapEntityToDomain(it) }
    }

    override suspend fun syncNotifications(page: Int, pageSize: Int): Result<List<NotificationItem>> {
        return try {
            val response = notificationApiService.getNotifications(
                mapOf("page" to page.toString(), "pageSize" to pageSize.toString())
            )
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    val dtos = body.data.items
                    // Cache to Room
                    notificationDao.insertAll(dtos.map { mapDtoToEntity(it) })
                    Result.success(dtos.map { mapDtoToDomain(it) })
                } else {
                    // Fallback to local cache
                    getCachedNotifications()
                }
            } else {
                Timber.e("Sync notifications API error: ${response.code()}")
                getCachedNotifications()
            }
        } catch (e: Exception) {
            Timber.e(e, "Sync notifications error, falling back to cache")
            getCachedNotifications()
        }
    }

    override suspend fun markAsRead(id: String): Result<Unit> {
        return try {
            val response = notificationApiService.markAsRead(id)
            if (response.isSuccessful) {
                notificationDao.markRead(id)
                Result.success(Unit)
            } else {
                // Still update locally even if API fails
                notificationDao.markRead(id)
                Timber.e("Mark as read API error: ${response.code()}")
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Timber.e(e, "Mark as read error")
            // Still update locally
            notificationDao.markRead(id)
            Result.success(Unit)
        }
    }

    override suspend fun markAllAsRead(): Result<Unit> {
        return try {
            val response = notificationApiService.markAllAsRead()
            if (response.isSuccessful) {
                notificationDao.markAllRead()
                Result.success(Unit)
            } else {
                // Still update locally even if API fails
                notificationDao.markAllRead()
                Timber.e("Mark all as read API error: ${response.code()}")
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Timber.e(e, "Mark all as read error")
            // Still update locally
            notificationDao.markAllRead()
            Result.success(Unit)
        }
    }

    override suspend fun deleteNotification(id: String): Result<Unit> {
        return try {
            val response = notificationApiService.deleteNotification(id)
            if (response.isSuccessful) {
                notificationDao.deleteById(id)
                Result.success(Unit)
            } else {
                // Still delete locally even if API fails
                notificationDao.deleteById(id)
                Timber.e("Delete notification API error: ${response.code()}")
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Timber.e(e, "Delete notification error")
            // Still delete locally
            notificationDao.deleteById(id)
            Result.success(Unit)
        }
    }

    private suspend fun getCachedNotifications(): Result<List<NotificationItem>> {
        val cached = notificationDao.getAll()
        return if (cached.isNotEmpty()) {
            Result.success(cached.map { mapEntityToDomain(it) })
        } else {
            Result.failure(Exception("No cached notifications available"))
        }
    }

    private fun mapEntityToDomain(entity: NotificationEntity): NotificationItem {
        return NotificationItem(
            id = entity.id,
            title = entity.title,
            body = entity.body,
            type = entity.type,
            isRead = entity.isRead,
            actionUrl = entity.actionUrl,
            createdAt = entity.createdAt
        )
    }

    private fun mapDtoToEntity(dto: NotificationDto): NotificationEntity {
        return NotificationEntity(
            id = dto.id,
            title = dto.title,
            body = dto.body,
            type = dto.type,
            isRead = dto.isRead,
            actionUrl = dto.actionUrl,
            createdAt = dto.createdAt
        )
    }

    private fun mapDtoToDomain(dto: NotificationDto): NotificationItem {
        return NotificationItem(
            id = dto.id,
            title = dto.title,
            body = dto.body,
            type = dto.type,
            isRead = dto.isRead,
            actionUrl = dto.actionUrl,
            createdAt = dto.createdAt
        )
    }
}
