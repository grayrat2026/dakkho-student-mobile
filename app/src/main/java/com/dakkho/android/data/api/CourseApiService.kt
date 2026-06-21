package com.dakkho.android.data.api

import com.dakkho.android.domain.model.ApiResult
import com.dakkho.android.domain.model.CourseDetailDto
import com.dakkho.android.domain.model.CourseDto
import com.dakkho.android.domain.model.CoursePackageDto
import com.dakkho.android.domain.model.CurriculumDto
import com.dakkho.android.domain.model.PaginatedResponse
import com.dakkho.android.domain.model.ReviewDto
import com.dakkho.android.domain.model.SubmitReviewRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface CourseApiService {

    @GET("api/courses")
    suspend fun getCourses(@QueryMap params: Map<String, String>): Response<ApiResult<PaginatedResponse<CourseDto>>>

    @GET("api/courses/{id}")
    suspend fun getCourseDetail(@Path("id") id: String): Response<ApiResult<CourseDetailDto>>

    @GET("api/courses/{id}/curriculum")
    suspend fun getCourseCurriculum(@Path("id") id: String): Response<ApiResult<CurriculumDto>>

    @GET("api/courses/{id}/reviews")
    suspend fun getCourseReviews(
        @Path("id") courseId: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("rating") rating: Int? = null
    ): Response<ApiResult<PaginatedResponse<ReviewDto>>>

    @POST("api/courses/{id}/reviews")
    suspend fun submitCourseReview(
        @Path("id") courseId: String,
        @Body request: SubmitReviewRequest
    ): Response<ApiResult<ReviewDto>>

    @GET("api/course-packages")
    suspend fun getCoursePackages(
        @Query("course_id") courseId: String
    ): Response<ApiResult<List<CoursePackageDto>>>
}
