package com.dakkho.android.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dakkho.android.data.db.entity.CourseDetailEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDetailDao {

    @Query("SELECT * FROM course_details WHERE course_id = :courseId")
    suspend fun getCourseDetail(courseId: String): CourseDetailEntity?

    @Query("SELECT * FROM course_details WHERE course_id = :courseId")
    fun getCourseDetailFlow(courseId: String): Flow<CourseDetailEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(courseDetail: CourseDetailEntity)

    @Query("DELETE FROM course_details WHERE course_id = :courseId")
    suspend fun deleteByCourseId(courseId: String)

    @Query("DELETE FROM course_details")
    suspend fun deleteAll()
}
