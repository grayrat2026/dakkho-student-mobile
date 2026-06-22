package com.dakkho.android.data.api

import com.dakkho.android.data.api.ApiResult
import com.dakkho.android.domain.model.NotificationDto
import com.dakkho.android.data.api.PaginatedResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface NotificationApiService {

    @GET("api/notifications")
    suspend fun getNotifications(
        @QueryMap params: Map<String, String>
    ): Response<ApiResult<PaginatedResponse<NotificationDto>>>

    @GET("api/notifications/{id}")
    suspend fun getNotificationDetail(
        @Path("id") id: String
    ): Response<ApiResult<NotificationDto>>

    @PATCH("api/notifications/{id}/read")
    suspend fun markAsRead(
        @Path("id") id: String
    ): Response<ApiResult<Unit>>

    @PATCH("api/notifications/read-all")
    suspend fun markAllAsRead(): Response<ApiResult<Unit>>

    @PATCH("api/notifications/{id}")
    suspend fun deleteNotification(
        @Path("id") id: String
    ): Response<ApiResult<Unit>>
}
