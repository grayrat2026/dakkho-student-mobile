package com.dakkho.android.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dakkho.android.data.db.entity.WatchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchHistoryDao {

    @Query("SELECT * FROM watch_history WHERE user_id = :userId ORDER BY last_watched_at DESC")
    suspend fun getWatchHistory(userId: String): List<WatchHistoryEntity>

    @Query("SELECT * FROM watch_history WHERE user_id = :userId ORDER BY last_watched_at DESC")
    fun getWatchHistoryFlow(userId: String): Flow<List<WatchHistoryEntity>>

    @Query("SELECT * FROM watch_history WHERE video_id = :videoId AND user_id = :userId")
    suspend fun getByVideoId(videoId: String, userId: String): WatchHistoryEntity?

    @Query("SELECT * FROM watch_history WHERE course_id = :courseId AND user_id = :userId ORDER BY last_watched_at DESC")
    suspend fun getByCourseId(courseId: String, userId: String): List<WatchHistoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: WatchHistoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(historyItems: List<WatchHistoryEntity>)

    @Query("DELETE FROM watch_history WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM watch_history WHERE user_id = :userId")
    suspend fun deleteAllForUser(userId: String)
}
