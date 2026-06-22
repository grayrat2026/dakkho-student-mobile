package com.dakkho.android.data.api

import com.dakkho.android.data.api.ApiResult
import com.dakkho.android.domain.model.WatchHistoryDto
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface WatchHistoryApiService {

    @GET("api/watch-history")
    suspend fun getWatchHistory(
        @QueryMap params: Map<String, String> = emptyMap()
    ): Response<ApiResult<List<WatchHistoryDto>>>

    @GET("api/watch-history/{id}")
    suspend fun getWatchHistoryById(
        @Path("id") id: String
    ): Response<ApiResult<WatchHistoryDto>>

    @DELETE("api/watch-history/{id}")
    suspend fun deleteWatchHistory(
        @Path("id") id: String
    ): Response<ApiResult<Unit>>

    @DELETE("api/watch-history")
    suspend fun clearAllWatchHistory(): Response<ApiResult<Unit>>
}
