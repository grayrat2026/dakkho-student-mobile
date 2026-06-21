package com.dakkho.android.domain.repository

import com.dakkho.android.domain.model.WatchHistoryItem
import kotlinx.coroutines.flow.Flow

interface WatchHistoryRepository {

    fun getWatchHistoryFlow(): Flow<List<WatchHistoryItem>>

    suspend fun getWatchHistory(): List<WatchHistoryItem>

    suspend fun getWatchHistoryByCourse(courseId: String): List<WatchHistoryItem>

    suspend fun getWatchHistoryById(id: String): WatchHistoryItem?

    suspend fun syncWatchHistory(): Result<List<WatchHistoryItem>>

    suspend fun deleteWatchHistory(id: String): Result<Unit>

    suspend fun clearAllWatchHistory(): Result<Unit>
}
