package com.dakkho.android.data.api

import com.dakkho.android.domain.model.LiveClassDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface StudentLiveClassApiService {

    @GET("api/live-classes")
    suspend fun getLiveClasses(
        @Query("status") status: String? = null,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): Response<LiveClassListResponse>

    @GET("api/live-classes/featured")
    suspend fun getFeaturedLiveClasses(): Response<LiveClassListResponse>

    @GET("api/live-classes/{id}")
    suspend fun getLiveClassDetail(
        @Path("id") id: String
    ): Response<LiveClassDetailResponse>

    @POST("api/live-classes/{id}/join")
    suspend fun joinLiveClass(
        @Path("id") id: String
    ): Response<LiveClassJoinResponse>

    @POST("api/live-classes/{id}/reminder")
    suspend fun toggleReminder(
        @Path("id") id: String
    ): Response<ReminderToggleResponse>
}

data class LiveClassJoinResponse(
    val token: String? = null,
    val livekitUrl: String? = null,
    val roomName: String? = null,
    val meetingUrl: String? = null,
    val error: String? = null
)

data class ReminderToggleResponse(
    val reminderSet: Boolean = false,
    val error: String? = null
)
