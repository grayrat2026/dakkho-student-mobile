package com.dakkho.android.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dakkho.android.data.db.entity.CourseNoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseNoteDao {

    @Query("SELECT * FROM course_notes WHERE course_id = :courseId AND user_id = :userId ORDER BY created_at DESC")
    suspend fun getNotesForCourse(courseId: String, userId: String): List<CourseNoteEntity>

    @Query("SELECT * FROM course_notes WHERE course_id = :courseId AND user_id = :userId ORDER BY created_at DESC")
    fun getNotesForCourseFlow(courseId: String, userId: String): Flow<List<CourseNoteEntity>>

    @Query("SELECT * FROM course_notes WHERE video_id = :videoId AND user_id = :userId ORDER BY position_ms ASC")
    suspend fun getNotesForVideo(videoId: String, userId: String): List<CourseNoteEntity>

    @Query("SELECT * FROM course_notes WHERE video_id = :videoId AND user_id = :userId ORDER BY position_ms ASC")
    fun getNotesForVideoFlow(videoId: String, userId: String): Flow<List<CourseNoteEntity>>

    @Query("SELECT * FROM course_notes WHERE id = :id")
    suspend fun getNoteById(id: Long): CourseNoteEntity?

    @Query("SELECT COUNT(*) FROM course_notes WHERE course_id = :courseId AND user_id = :userId")
    suspend fun getNoteCountForCourse(courseId: String, userId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: CourseNoteEntity): Long

    @Update
    suspend fun update(note: CourseNoteEntity)

    @Delete
    suspend fun delete(note: CourseNoteEntity)

    @Query("DELETE FROM course_notes WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM course_notes WHERE video_id = :videoId AND user_id = :userId")
    suspend fun deleteAllForVideo(videoId: String, userId: String)

    @Query("DELETE FROM course_notes WHERE course_id = :courseId AND user_id = :userId")
    suspend fun deleteAllForCourse(courseId: String, userId: String)
}
