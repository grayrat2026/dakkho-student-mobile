package com.dakkho.android.data.api

import com.dakkho.android.domain.model.ApiResult
import com.dakkho.android.domain.model.CreateDiscussionRequest
import com.dakkho.android.domain.model.CreateReplyRequest
import com.dakkho.android.domain.model.DiscussionDetailResponse
import com.dakkho.android.domain.model.DiscussionListResponse
import com.dakkho.android.domain.model.LikeToggleResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface DiscussionApiService {

    @GET("student/discussions")
    suspend fun getDiscussions(
        @Query("courseId") courseId: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<DiscussionListResponse>

    @GET("student/discussions/{id}")
    suspend fun getDiscussionDetail(
        @Path("id") threadId: String
    ): Response<DiscussionDetailResponse>

    @POST("student/discussions")
    suspend fun createDiscussion(
        @Body request: CreateDiscussionRequest
    ): Response<ApiResult<DiscussionDetailResponse>>

    @POST("student/discussions/{id}/reply")
    suspend fun createReply(
        @Path("id") threadId: String,
        @Body request: CreateReplyRequest
    ): Response<ApiResult<DiscussionDetailResponse>>

    @PUT("student/discussions/{id}/like")
    suspend fun toggleLike(
        @Path("id") threadId: String
    ): Response<LikeToggleResponse>

    @DELETE("student/discussions/{id}")
    suspend fun deleteDiscussion(
        @Path("id") threadId: String
    ): Response<ApiResult<Unit>>
}
