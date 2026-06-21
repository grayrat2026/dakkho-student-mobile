package com.dakkho.android.domain.repository

import com.dakkho.android.domain.model.Course
import com.dakkho.android.domain.model.Instructor
import com.dakkho.android.domain.model.Technology
import kotlinx.coroutines.flow.Flow

/**
 * Repository for department/technology data.
 * All departments are dynamic — fetched from API, cached in Room.
 * No hardcoded defaults exist.
 */
interface DepartmentRepository {

    /** Get all active departments as a Flow for reactive UI updates */
    fun getAllDepartments(): Flow<List<Technology>>

    /** Get a single department by its URL slug */
    suspend fun getDepartmentBySlug(slug: String): Technology?

    /** Get courses filtered by technology/department */
    suspend fun getCoursesForDepartment(technology: String, page: Int = 1): List<Course>

    /** Get instructors filtered by technology/department */
    suspend fun getInstructorsForDepartment(technology: String): List<Instructor>

    /** Force refresh from API (pull-to-refresh) */
    suspend fun refreshDepartments()

    /** Search departments by name */
    fun searchDepartments(query: String): Flow<List<Technology>>
}
