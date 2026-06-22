package com.dakkho.android.data.api

import com.dakkho.android.data.api.ApiResult
import com.dakkho.android.domain.model.CommunityCommentDto
import com.dakkho.android.domain.model.CommunityPostDto
import com.dakkho.android.domain.model.CreateGroupRequest
import com.dakkho.android.domain.model.CreatePostRequest
import com.dakkho.android.domain.model.LeaderboardEntryDto
import com.dakkho.android.domain.model.PeerUserDto
import com.dakkho.android.domain.model.RoadmapFeatureDto
import com.dakkho.android.domain.model.StudyGroupDto
import com.dakkho.android.domain.model.SubmitFeedbackRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Phase 29: Social API Service #96-101
 * Covers: Leaderboard, Study Groups, Peer Connections, Community, Feedback, Roadmap
 */
interface SocialApiService {

    // ── #96: Leaderboard ──

    @GET("api/social/leaderboard")
    suspend fun getLeaderboard(
        @Query("period") period: String = "weekly"
    ): Response<ApiResult<List<LeaderboardEntryDto>>>

    @GET("api/social/leaderboard/me")
    suspend fun getMyRank(
        @Query("period") period: String = "weekly"
    ): Response<ApiResult<LeaderboardEntryDto>>

    // ── #97-98: Study Groups ──

    @GET("api/social/groups")
    suspend fun getStudyGroups(
        @Query("subject") subject: String? = null
    ): Response<ApiResult<List<StudyGroupDto>>>

    @POST("api/social/groups")
    suspend fun createGroup(@Body request: CreateGroupRequest): Response<ApiResult<StudyGroupDto>>

    @POST("api/social/groups/{id}/join")
    suspend fun joinGroup(@Path("id") groupId: String): Response<ApiResult<Unit>>

    @POST("api/social/groups/{id}/leave")
    suspend fun leaveGroup(@Path("id") groupId: String): Response<ApiResult<Unit>>

    // ── #99-100: Peer Connections ──

    @GET("api/social/peers")
    suspend fun getPeers(
        @Query("technology") technology: String? = null
    ): Response<ApiResult<List<PeerUserDto>>>

    @GET("api/social/peers/suggestions")
    suspend fun getPeerSuggestions(): Response<ApiResult<List<PeerUserDto>>>

    @PUT("api/social/peers/{id}/follow")
    suspend fun followPeer(@Path("id") peerId: String): Response<ApiResult<Unit>>

    @PUT("api/social/peers/{id}/unfollow")
    suspend fun unfollowPeer(@Path("id") peerId: String): Response<ApiResult<Unit>>

    // ── #101: Community ──

    @GET("api/social/community/posts")
    suspend fun getCommunityPosts(
        @Query("category") category: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResult<List<CommunityPostDto>>>

    @POST("api/social/community/posts")
    suspend fun createPost(@Body request: CreatePostRequest): Response<ApiResult<CommunityPostDto>>

    @PUT("api/social/community/posts/{id}/upvote")
    suspend fun upvotePost(@Path("id") postId: String): Response<ApiResult<Unit>>

    @GET("api/social/community/posts/{id}/comments")
    suspend fun getPostComments(@Path("id") postId: String): Response<ApiResult<List<CommunityCommentDto>>>

    @POST("api/social/community/posts/{id}/comments")
    suspend fun addComment(
        @Path("id") postId: String,
        @Body comment: Map<String, String>
    ): Response<ApiResult<CommunityCommentDto>>

    // ── Feedback & Roadmap ──

    @POST("api/social/feedback")
    suspend fun submitFeedback(@Body request: SubmitFeedbackRequest): Response<ApiResult<Unit>>

    @GET("api/social/roadmap")
    suspend fun getRoadmap(): Response<ApiResult<List<RoadmapFeatureDto>>>

    @PUT("api/social/roadmap/{id}/upvote")
    suspend fun upvoteFeature(@Path("id") featureId: String): Response<ApiResult<Unit>>
}
