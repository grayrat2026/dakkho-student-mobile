package com.dakkho.android.data.api

import com.dakkho.android.domain.model.ApiResult
import com.dakkho.android.domain.model.CourseDetailDto
import com.dakkho.android.domain.model.CourseDto
import com.dakkho.android.domain.model.CurriculumDto
import com.dakkho.android.domain.model.PaginatedResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface CourseApiService {

    @GET("api/courses")
    suspend fun getCourses(@QueryMap params: Map<String, String>): Response<ApiResult<PaginatedResponse<CourseDto>>>

    @GET("api/courses/{id}")
    suspend fun getCourseDetail(@Path("id") id: String): Response<ApiResult<CourseDetailDto>>

    @GET("api/courses/{id}/curriculum")
    suspend fun getCourseCurriculum(@Path("id") id: String): Response<ApiResult<CurriculumDto>>
}
