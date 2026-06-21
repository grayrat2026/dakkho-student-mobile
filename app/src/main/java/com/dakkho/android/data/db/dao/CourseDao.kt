package com.dakkho.android.data.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dakkho.android.data.db.entity.CourseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {

    @Query("SELECT * FROM courses ORDER BY cached_at DESC")
    suspend fun getCourses(): List<CourseEntity>

    @Query("SELECT * FROM courses ORDER BY cached_at DESC")
    fun getCoursesFlow(): Flow<List<CourseEntity>>

    @Query("SELECT * FROM courses WHERE id = :courseId")
    suspend fun getCourseById(courseId: String): CourseEntity?

    @Query("SELECT * FROM courses WHERE id = :courseId")
    fun getCourseByIdFlow(courseId: String): Flow<CourseEntity?>

    @Query("SELECT * FROM courses WHERE technology = :technology ORDER BY rating DESC")
    suspend fun getCoursesByTechnology(technology: String): List<CourseEntity>

    @Query("SELECT * FROM courses WHERE instructor_id = :instructorId ORDER BY rating DESC")
    suspend fun getCoursesByInstructor(instructorId: String): List<CourseEntity>

    @Query(
        """
        SELECT * FROM courses 
        WHERE title LIKE '%' || :query || '%' 
           OR description LIKE '%' || :query || '%'
           OR technology LIKE '%' || :query || '%'
        ORDER BY rating DESC
        """
    )
    suspend fun searchCourses(query: String): List<CourseEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(course: CourseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(courses: List<CourseEntity>)

    @Query("DELETE FROM courses WHERE id = :courseId")
    suspend fun deleteById(courseId: String)

    @Query("DELETE FROM courses")
    suspend fun deleteAll()

    @Query("SELECT * FROM courses ORDER BY cached_at DESC LIMIT :limit OFFSET :offset")
    suspend fun getCoursesPaged(limit: Int, offset: Int): List<CourseEntity>

    @Query("SELECT COUNT(*) FROM courses")
    suspend fun getCoursesCount(): Int

    @Query("SELECT * FROM courses ORDER BY cached_at DESC")
    fun getCoursesPagingSource(): PagingSource<Int, CourseEntity>
}
