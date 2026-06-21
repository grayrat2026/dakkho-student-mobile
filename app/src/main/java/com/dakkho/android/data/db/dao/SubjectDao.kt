package com.dakkho.android.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dakkho.android.data.db.entity.SubjectEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Subject data within semesters — offline cache.
 */
@Dao
interface SubjectDao {

    @Query("SELECT * FROM subjects WHERE semesterId = :semesterId ORDER BY sortOrder ASC, name ASC")
    fun getSubjectsForSemester(semesterId: String): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects WHERE departmentSlug = :departmentSlug AND semesterNumber = :semesterNumber ORDER BY sortOrder ASC, name ASC")
    fun getSubjectsForDepartmentSemester(departmentSlug: String, semesterNumber: Int): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects WHERE id = :id LIMIT 1")
    suspend fun getSubjectById(id: String): SubjectEntity?

    @Query("SELECT * FROM subjects WHERE courseId = :courseId LIMIT 1")
    suspend fun getSubjectByCourseId(courseId: String): SubjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(subject: SubjectEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(subjects: List<SubjectEntity>)

    @Query("DELETE FROM subjects WHERE semesterId = :semesterId")
    suspend fun deleteSubjectsForSemester(semesterId: String)

    @Query("DELETE FROM subjects WHERE departmentSlug = :departmentSlug AND semesterNumber = :semesterNumber")
    suspend fun deleteSubjectsForDepartmentSemester(departmentSlug: String, semesterNumber: Int)

    @Query("SELECT COUNT(*) FROM subjects WHERE semesterId = :semesterId")
    suspend fun countSubjectsForSemester(semesterId: String): Int
}
