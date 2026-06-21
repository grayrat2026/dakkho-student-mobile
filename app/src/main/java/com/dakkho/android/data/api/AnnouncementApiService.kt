package com.dakkho.android.data.api

import com.dakkho.android.domain.model.AnnouncementDto
import com.dakkho.android.domain.model.AnnouncementListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AnnouncementApiService {

    @GET("api/courses/{courseId}/announcements")
    suspend fun getAnnouncements(
        @Path("courseId") courseId: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<AnnouncementListResponse>

    @GET("api/announcements/{id}")
    suspend fun getAnnouncementDetail(
        @Path("id") announcementId: String
    ): Response<AnnouncementDto>
}
