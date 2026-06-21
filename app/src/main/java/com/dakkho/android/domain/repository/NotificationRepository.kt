package com.dakkho.android.domain.repository

import com.dakkho.android.domain.model.NotificationItem
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {

    fun getNotificationsFlow(): Flow<List<NotificationItem>>

    fun getUnreadCount(): Flow<Int>

    suspend fun getNotificationById(id: String): NotificationItem?

    suspend fun syncNotifications(page: Int = 1, pageSize: Int = 20): Result<List<NotificationItem>>

    suspend fun markAsRead(id: String): Result<Unit>

    suspend fun markAllAsRead(): Result<Unit>

    suspend fun deleteNotification(id: String): Result<Unit>
}
