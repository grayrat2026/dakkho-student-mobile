package com.dakkho.android.data.api

import com.dakkho.android.domain.model.ApiResult
import com.dakkho.android.domain.model.TechnologyDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * API service for technology/department endpoints.
 * Departments are fully dynamic — whatever Admin/Instructor adds is what students see.
 * No hardcoded departments exist in the app.
 */
interface TechnologyApiService {

    @GET("api/technologies")
    suspend fun getTechnologies(): Response<ApiResult<List<TechnologyDto>>>

    @GET("api/technologies/{slug}")
    suspend fun getTechnologyBySlug(
        @Path("slug") slug: String
    ): Response<ApiResult<TechnologyDto>>

    @GET("api/courses")
    suspend fun getCoursesByTechnology(
        @Query("technology") technology: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResult<Map<String, Any>>>

    @GET("api/instructors")
    suspend fun getInstructorsByTechnology(
        @Query("technology") technology: String
    ): Response<ApiResult<List<Map<String, Any>>>>
}
