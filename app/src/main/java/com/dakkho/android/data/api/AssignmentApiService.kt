package com.dakkho.android.data.api

import com.dakkho.android.data.api.ApiResult
import com.dakkho.android.domain.model.AssignmentDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface AssignmentApiService {

    @GET("api/courses/{courseId}/assignments")
    suspend fun getAssignments(
        @Path("courseId") courseId: String,
        @QueryMap params: Map<String, String> = emptyMap()
    ): Response<ApiResult<List<AssignmentDto>>>

    @GET("api/courses/{courseId}/assignments/{assignmentId}")
    suspend fun getAssignmentById(
        @Path("courseId") courseId: String,
        @Path("assignmentId") assignmentId: String
    ): Response<ApiResult<AssignmentDto>>

    @Multipart
    @POST("api/courses/{courseId}/assignments/{assignmentId}/submit")
    suspend fun submitAssignment(
        @Path("courseId") courseId: String,
        @Path("assignmentId") assignmentId: String,
        @Part file: MultipartBody.Part
    ): Response<ApiResult<AssignmentDto>>
}
