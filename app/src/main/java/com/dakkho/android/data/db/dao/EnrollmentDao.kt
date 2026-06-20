package com.dakkho.android.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dakkho.android.data.db.entity.EnrollmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EnrollmentDao {

    @Query("SELECT * FROM enrollments WHERE user_id = :userId ORDER BY enrolled_at DESC")
    suspend fun getEnrollmentsForUser(userId: String): List<EnrollmentEntity>

    @Query("SELECT * FROM enrollments WHERE user_id = :userId ORDER BY enrolled_at DESC")
    fun getEnrollmentsForUserFlow(userId: String): Flow<List<EnrollmentEntity>>

    @Query("SELECT * FROM enrollments WHERE user_id = :userId AND course_id = :courseId")
    suspend fun getEnrollment(userId: String, courseId: String): EnrollmentEntity?

    @Query("SELECT * FROM enrollments WHERE user_id = :userId AND course_id = :courseId")
    fun getEnrollmentFlow(userId: String, courseId: String): Flow<EnrollmentEntity?>

    @Query("SELECT EXISTS(SELECT 1 FROM enrollments WHERE user_id = :userId AND course_id = :courseId)")
    suspend fun isEnrolled(userId: String, courseId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(enrollment: EnrollmentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(enrollments: List<EnrollmentEntity>)

    @Query("DELETE FROM enrollments WHERE id = :enrollmentId")
    suspend fun deleteById(enrollmentId: String)

    @Query("DELETE FROM enrollments WHERE user_id = :userId")
    suspend fun deleteAllForUser(userId: String)
}
