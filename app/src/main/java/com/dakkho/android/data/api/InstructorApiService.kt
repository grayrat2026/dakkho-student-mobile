package com.dakkho.android.data.api

import com.dakkho.android.domain.model.ApiResult
import com.dakkho.android.domain.model.AchievementDto
import com.dakkho.android.domain.model.CreateTicketRequest
import com.dakkho.android.domain.model.InstituteDto
import com.dakkho.android.domain.model.InstructorDetailDto
import com.dakkho.android.domain.model.InstructorDto
import com.dakkho.android.domain.model.NotificationDto
import com.dakkho.android.domain.model.PaginatedResponse
import com.dakkho.android.domain.model.SupportTicketDto
import com.dakkho.android.domain.model.TechnologyDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface InstructorApiService {

    @GET("api/instructors")
    suspend fun getInstructors(): Response<ApiResult<List<InstructorDto>>>

    @GET("api/instructors/{id}")
    suspend fun getInstructorDetail(@Path("id") id: String): Response<ApiResult<InstructorDetailDto>>

    @GET("api/institutes")
    suspend fun getInstitutes(): Response<ApiResult<List<InstituteDto>>>

    @GET("api/technologies")
    suspend fun getTechnologies(): Response<ApiResult<List<TechnologyDto>>>

    @GET("api/notifications")
    suspend fun getNotifications(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResult<PaginatedResponse<NotificationDto>>>

    @GET("api/achievements")
    suspend fun getAchievements(): Response<ApiResult<List<AchievementDto>>>

    @GET("api/support/tickets")
    suspend fun getSupportTickets(): Response<ApiResult<List<SupportTicketDto>>>

    @POST("api/support/tickets")
    suspend fun createSupportTicket(@Body request: CreateTicketRequest): Response<ApiResult<SupportTicketDto>>
}
