package com.dakkho.android.domain.repository

import com.dakkho.android.domain.model.RoutineEntry
import com.dakkho.android.domain.model.Semester
import com.dakkho.android.domain.model.SemesterProgress
import com.dakkho.android.domain.model.Subject
import kotlinx.coroutines.flow.Flow

/**
 * Repository for semester, subject, and routine data.
 * All data is dynamic from API, cached in Room for offline access.
 *
 * Bangladesh Diploma system: 7 regular semesters + 8th = ইন্টার্নি (Internship).
 */
interface SemesterRepository {

    /** Get all semesters for a department as a reactive Flow */
    fun getSemestersForDepartment(departmentSlug: String): Flow<List<Semester>>

    /** Get a single semester by department + semester number */
    suspend fun getSemester(departmentSlug: String, semesterNumber: Int): Semester?

    /** Get subjects for a specific semester */
    fun getSubjectsForSemester(departmentSlug: String, semesterNumber: Int): Flow<List<Subject>>

    /** Get the weekly routine/schedule for a semester */
    fun getRoutineForSemester(departmentSlug: String, semesterNumber: Int): Flow<List<RoutineEntry>>

    /** Get the semester progress for the current student */
    suspend fun getSemesterProgress(departmentSlug: String): SemesterProgress

    /** Force refresh from API (pull-to-refresh) */
    suspend fun refreshSemesterData(departmentSlug: String, semesterNumber: Int)

    /** Force refresh all semesters for a department */
    suspend fun refreshAllSemesters(departmentSlug: String)
}
