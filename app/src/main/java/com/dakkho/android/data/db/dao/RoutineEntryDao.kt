package com.dakkho.android.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dakkho.android.data.db.entity.RoutineEntryEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Routine entries — weekly timetable.
 */
@Dao
interface RoutineEntryDao {

    @Query("SELECT * FROM routine_entries WHERE departmentSlug = :departmentSlug AND semesterNumber = :semesterNumber ORDER BY dayOfWeek ASC, startTime ASC")
    fun getRoutineForSemester(departmentSlug: String, semesterNumber: Int): Flow<List<RoutineEntryEntity>>

    @Query("SELECT * FROM routine_entries WHERE departmentSlug = :departmentSlug AND semesterNumber = :semesterNumber AND dayOfWeek = :dayOfWeek ORDER BY startTime ASC")
    fun getRoutineForDay(departmentSlug: String, semesterNumber: Int, dayOfWeek: Int): Flow<List<RoutineEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: RoutineEntryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<RoutineEntryEntity>)

    @Query("DELETE FROM routine_entries WHERE departmentSlug = :departmentSlug AND semesterNumber = :semesterNumber")
    suspend fun deleteRoutineForSemester(departmentSlug: String, semesterNumber: Int)

    @Query("SELECT COUNT(*) FROM routine_entries WHERE departmentSlug = :departmentSlug AND semesterNumber = :semesterNumber")
    suspend fun countRoutineEntries(departmentSlug: String, semesterNumber: Int): Int
}
