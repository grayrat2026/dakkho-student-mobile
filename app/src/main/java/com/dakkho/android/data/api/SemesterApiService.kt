package com.dakkho.android.data.api

import com.dakkho.android.data.api.ApiResult
import com.dakkho.android.domain.model.SemesterDto
import com.dakkho.android.domain.model.SubjectDto2
import com.dakkho.android.domain.model.RoutineEntryDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * API service for semester, subject, and routine endpoints.
 *
 * Semesters are dynamic — each department defines how many it has.
 * 7 regular semesters + 8th = ইন্টার্নি (Internship).
 *
 * The backend worker already has:
 * - GET /api/subjects?technologyId=X
 * - GET /api/courses?semester=X&technology=X
 *
 * We also add:
 * - GET /api/technologies/{slug}/semesters
 * - GET /api/semesters/{id}/routine
 * - GET /api/semesters/{id}/syllabus
 */
interface SemesterApiService {

    /**
     * Get all semesters for a specific department/technology.
     * Returns a list of semester objects with subject counts and credit totals.
     */
    @GET("api/technologies/{slug}/semesters")
    suspend fun getSemestersForDepartment(
        @Path("slug") departmentSlug: String
    ): Response<ApiResult<List<SemesterDto>>>

    /**
     * Get subjects for a specific semester within a department.
     * Uses the existing subjects endpoint with filters.
     */
    @GET("api/subjects")
    suspend fun getSubjectsForSemester(
        @Query("technologyId") technologyId: String,
        @Query("semester") semester: Int,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50
    ): Response<ApiResult<Map<String, Any>>>

    /**
     * Get the weekly routine/schedule for a semester.
     */
    @GET("api/semesters/{semesterId}/routine")
    suspend fun getRoutineForSemester(
        @Path("semesterId") semesterId: String
    ): Response<ApiResult<List<RoutineEntryDto>>>

    /**
     * Get syllabus overview for a semester — expandable topic lists per subject.
     */
    @GET("api/semesters/{semesterId}/syllabus")
    suspend fun getSyllabusForSemester(
        @Path("semesterId") semesterId: String
    ): Response<ApiResult<Map<String, Any>>>

    /**
     * Get courses filtered by semester and technology.
     * Reuses existing courses endpoint.
     */
    @GET("api/courses")
    suspend fun getCoursesBySemesterAndTechnology(
        @Query("semester") semester: Int,
        @Query("technology") technology: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResult<Map<String, Any>>>
}
