package com.dakkho.android.data.api

import com.dakkho.android.domain.model.LiveClassDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface LiveClassApiService {

    @GET("api/live-classes")
    suspend fun getLiveClasses(
        @Query("instructor_id") instructorId: String,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0,
        @Query("status") status: String? = null
    ): Response<LiveClassListResponse>

    @GET("api/live-classes/{id}")
    suspend fun getLiveClassDetail(
        @Path("id") id: String
    ): Response<LiveClassDetailResponse>
}

data class LiveClassListResponse(
    val liveClasses: List<LiveClassDto> = emptyList(),
    val total: Int = 0,
    val error: String? = null
)

data class LiveClassDetailResponse(
    val liveClass: LiveClassDto? = null,
    val error: String? = null
)
