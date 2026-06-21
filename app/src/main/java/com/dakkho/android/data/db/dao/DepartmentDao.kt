package com.dakkho.android.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dakkho.android.data.db.entity.DepartmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DepartmentDao {

    @Query("SELECT * FROM departments WHERE isActive = 1 ORDER BY name ASC")
    fun getAllDepartments(): Flow<List<DepartmentEntity>>

    @Query("SELECT * FROM departments WHERE slug = :slug AND isActive = 1 LIMIT 1")
    suspend fun getDepartmentBySlug(slug: String): DepartmentEntity?

    @Query("SELECT * FROM departments WHERE id = :id AND isActive = 1 LIMIT 1")
    suspend fun getDepartmentById(id: String): DepartmentEntity?

    @Query("SELECT * FROM departments WHERE isActive = 1 ORDER BY courseCount DESC")
    fun getDepartmentsByCourseCount(): Flow<List<DepartmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(departments: List<DepartmentEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(department: DepartmentEntity)

    @Update
    suspend fun update(department: DepartmentEntity)

    @Delete
    suspend fun delete(department: DepartmentEntity)

    @Query("DELETE FROM departments")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM departments WHERE isActive = 1")
    suspend fun getActiveDepartmentCount(): Int
}
