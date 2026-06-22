package com.dakkho.android.data.api

import com.dakkho.android.data.api.ApiResult
import com.dakkho.android.domain.model.ActiveSessionDto
import com.dakkho.android.domain.model.ContentProtectionConfigDto
import com.dakkho.android.domain.model.DownloadSettingsDto
import com.dakkho.android.domain.model.NetworkDataConfigDto
import com.dakkho.android.domain.model.ThemeSettingsDto
import com.dakkho.android.domain.model.VideoQualitySettingsDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Phase 27: Settings Part 2 API Service #77-82
 * Covers: Theme, Download, Video Quality, Network, Content Protection, Active Sessions
 */
interface SettingsApiService {

    // ── #77: Theme Settings ──

    @GET("api/settings/theme")
    suspend fun getThemeSettings(): Response<ApiResult<ThemeSettingsDto>>

    @PUT("api/settings/theme")
    suspend fun updateThemeSettings(@Body settings: ThemeSettingsDto): Response<ApiResult<ThemeSettingsDto>>

    // ── #78: Download Settings ──

    @GET("api/settings/downloads")
    suspend fun getDownloadSettings(): Response<ApiResult<DownloadSettingsDto>>

    @PUT("api/settings/downloads")
    suspend fun updateDownloadSettings(@Body settings: DownloadSettingsDto): Response<ApiResult<DownloadSettingsDto>>

    // ── #79: Video Quality Settings ──

    @GET("api/settings/video-quality")
    suspend fun getVideoQualitySettings(): Response<ApiResult<VideoQualitySettingsDto>>

    @PUT("api/settings/video-quality")
    suspend fun updateVideoQualitySettings(@Body settings: VideoQualitySettingsDto): Response<ApiResult<VideoQualitySettingsDto>>

    // ── #80: Network & Data Settings ──

    @GET("api/settings/network")
    suspend fun getNetworkDataConfig(): Response<ApiResult<NetworkDataConfigDto>>

    @PUT("api/settings/network")
    suspend fun updateNetworkDataConfig(@Body config: NetworkDataConfigDto): Response<ApiResult<NetworkDataConfigDto>>

    // ── #81: Content Protection ──

    @GET("api/settings/content-protection")
    suspend fun getContentProtectionConfig(): Response<ApiResult<ContentProtectionConfigDto>>

    @PUT("api/settings/content-protection")
    suspend fun updateContentProtectionConfig(@Body config: ContentProtectionConfigDto): Response<ApiResult<ContentProtectionConfigDto>>

    // ── #82: Active Sessions ──

    @GET("api/settings/sessions")
    suspend fun getActiveSessions(): Response<ApiResult<List<ActiveSessionDto>>>

    @DELETE("api/settings/sessions/{sessionId}")
    suspend fun logoutSession(@Path("sessionId") sessionId: String): Response<ApiResult<Unit>>
}
