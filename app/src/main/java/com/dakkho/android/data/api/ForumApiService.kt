package com.dakkho.android.data.api

import com.dakkho.android.domain.model.CreateForumCommentRequest
import com.dakkho.android.domain.model.CreateForumThreadRequest
import com.dakkho.android.domain.model.ForumThreadDetailResponse
import com.dakkho.android.domain.model.ForumThreadListResponse
import com.dakkho.android.domain.model.ApiResult
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ForumApiService {

    @GET("api/support/discussions")
    suspend fun getForumThreads(
        @Query("category") category: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ForumThreadListResponse>

    @GET("api/support/discussions/{id}")
    suspend fun getForumThreadDetail(
        @Path("id") threadId: String
    ): Response<ForumThreadDetailResponse>

    @POST("api/support/discussions")
    suspend fun createForumThread(
        @Body request: CreateForumThreadRequest
    ): Response<ApiResult<ForumThreadDetailResponse>>

    @POST("api/support/discussions/{id}/comments")
    suspend fun createComment(
        @Path("id") threadId: String,
        @Body request: CreateForumCommentRequest
    ): Response<ApiResult<ForumThreadDetailResponse>>

    @PUT("api/support/discussions/{id}/upvote")
    suspend fun toggleUpvote(
        @Path("id") threadId: String
    ): Response<Map<String, Any>>

    @DELETE("api/support/discussions/{id}")
    suspend fun deleteForumThread(
        @Path("id") threadId: String
    ): Response<ApiResult<Unit>>
}
