package com.dakkho.android.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dakkho.android.data.db.entity.VideoBookmarkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoBookmarkDao {

    @Query("SELECT * FROM video_bookmarks WHERE video_id = :videoId AND user_id = :userId ORDER BY position_ms ASC")
    suspend fun getBookmarksForVideo(videoId: String, userId: String): List<VideoBookmarkEntity>

    @Query("SELECT * FROM video_bookmarks WHERE video_id = :videoId AND user_id = :userId ORDER BY position_ms ASC")
    fun getBookmarksForVideoFlow(videoId: String, userId: String): Flow<List<VideoBookmarkEntity>>

    @Query("SELECT * FROM video_bookmarks WHERE course_id = :courseId AND user_id = :userId ORDER BY created_at DESC")
    suspend fun getBookmarksForCourse(courseId: String, userId: String): List<VideoBookmarkEntity>

    @Query("SELECT * FROM video_bookmarks WHERE user_id = :userId ORDER BY created_at DESC")
    suspend fun getAllBookmarks(userId: String): List<VideoBookmarkEntity>

    @Query("SELECT * FROM video_bookmarks WHERE user_id = :userId ORDER BY created_at DESC")
    fun getAllBookmarksFlow(userId: String): Flow<List<VideoBookmarkEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM video_bookmarks WHERE video_id = :videoId AND position_ms = :positionMs AND user_id = :userId)")
    suspend fun isBookmarkedAtPosition(videoId: String, positionMs: Long, userId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bookmark: VideoBookmarkEntity): Long

    @Delete
    suspend fun delete(bookmark: VideoBookmarkEntity)

    @Query("DELETE FROM video_bookmarks WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM video_bookmarks WHERE video_id = :videoId AND user_id = :userId")
    suspend fun deleteAllForVideo(videoId: String, userId: String)
}
