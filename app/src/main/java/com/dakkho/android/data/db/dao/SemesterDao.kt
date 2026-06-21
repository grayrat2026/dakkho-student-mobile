package com.dakkho.android.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dakkho.android.data.db.entity.SemesterEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Semester data — offline cache.
 */
@Dao
interface SemesterDao {

    @Query("SELECT * FROM semesters WHERE departmentSlug = :departmentSlug ORDER BY number ASC")
    fun getSemestersForDepartment(departmentSlug: String): Flow<List<SemesterEntity>>

    @Query("SELECT * FROM semesters WHERE departmentSlug = :departmentSlug AND number = :semesterNumber LIMIT 1")
    suspend fun getSemester(departmentSlug: String, semesterNumber: Int): SemesterEntity?

    @Query("SELECT * FROM semesters WHERE id = :id LIMIT 1")
    suspend fun getSemesterById(id: String): SemesterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(semester: SemesterEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(semesters: List<SemesterEntity>)

    @Query("DELETE FROM semesters WHERE departmentSlug = :departmentSlug")
    suspend fun deleteSemestersForDepartment(departmentSlug: String)

    @Query("SELECT COUNT(*) FROM semesters WHERE departmentSlug = :departmentSlug")
    suspend fun countSemestersForDepartment(departmentSlug: String): Int
}
