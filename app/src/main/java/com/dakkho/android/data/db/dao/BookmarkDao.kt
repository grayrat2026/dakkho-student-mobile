package com.dakkho.android.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dakkho.android.data.db.entity.BookmarkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {

    @Query("SELECT * FROM bookmarks WHERE user_id = :userId ORDER BY created_at DESC")
    suspend fun getByUserId(userId: String): List<BookmarkEntity>

    @Query("SELECT * FROM bookmarks WHERE user_id = :userId ORDER BY created_at DESC")
    fun getByUserIdFlow(userId: String): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE course_id = :courseId AND user_id = :userId")
    suspend fun getByCourseId(courseId: String, userId: String): BookmarkEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE course_id = :courseId AND user_id = :userId)")
    suspend fun isBookmarked(courseId: String, userId: String): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE course_id = :courseId AND user_id = :userId)")
    fun isBookmarkedFlow(courseId: String, userId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bookmark: BookmarkEntity)

    @Delete
    suspend fun delete(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE course_id = :courseId AND user_id = :userId")
    suspend fun deleteByCourseId(courseId: String, userId: String)

    @Query("DELETE FROM bookmarks WHERE user_id = :userId")
    suspend fun deleteAllForUser(userId: String)
}
