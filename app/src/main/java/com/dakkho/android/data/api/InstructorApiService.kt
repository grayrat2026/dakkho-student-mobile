package com.dakkho.android.data.api

import com.dakkho.android.domain.model.AchievementDto
import com.dakkho.android.domain.model.CourseDto
import com.dakkho.android.domain.model.CreateTicketRequest
import com.dakkho.android.domain.model.InstituteDto
import com.dakkho.android.domain.model.InstructorDetailDto
import com.dakkho.android.domain.model.InstructorDto
import com.dakkho.android.domain.model.NotificationDto
import com.dakkho.android.data.api.PaginatedResponse
import com.dakkho.android.domain.model.SupportTicketDto
import com.dakkho.android.domain.model.TechnologyDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface InstructorApiService {

    // ── Instructor List & Detail ──

    @GET("api/instructors")
    suspend fun getInstructors(): Response<InstructorListResponse>

    @GET("api/instructors")
    suspend fun getInstructorsPaginated(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("search") search: String = ""
    ): Response<InstructorListResponse>

    @GET("api/instructors/{id}")
    suspend fun getInstructorDetail(@Path("id") id: String): Response<InstructorDetailResponse>

    @GET("api/instructors/{id}/courses")
    suspend fun getInstructorCourses(
        @Path("id") instructorId: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): Response<InstructorCoursesResponse>

    @GET("api/instructors/{id}/reviews")
    suspend fun getInstructorReviews(
        @Path("id") instructorId: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("rating") rating: Int? = null
    ): Response<InstructorReviewsResponse>

    // ── Institutes & Technologies ──

    @GET("api/institutes")
    suspend fun getInstitutes(): Response<InstituteListResponse>

    @GET("api/technologies")
    suspend fun getTechnologies(): Response<TechnologyListResponse>

    // ── Notifications ──

    @GET("api/notifications")
    suspend fun getNotifications(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<NotificationPaginatedResponse>

    // ── Achievements & Support ──

    @GET("api/achievements")
    suspend fun getAchievements(): Response<AchievementListResponse>

    @GET("api/support/tickets")
    suspend fun getSupportTickets(): Response<SupportTicketListResponse>

    @POST("api/support/tickets")
    suspend fun createSupportTicket(@Body request: CreateTicketRequest): Response<SupportTicketCreateResponse>
}

// ── Response wrappers matching D1 backend format ──

data class InstructorListResponse(
    val instructors: List<InstructorDto> = emptyList(),
    val total: Int = 0,
    val error: String? = null
)

data class InstructorDetailResponse(
    val instructor: InstructorDetailDto? = null,
    val error: String? = null
)

data class InstructorCoursesResponse(
    val courses: List<CourseDto> = emptyList(),
    val total: Int = 0,
    val error: String? = null
)

data class InstructorReviewsResponse(
    val reviews: List<ReviewDto> = emptyList(),
    val total: Int = 0,
    val average_rating: Float = 0f,
    val rating_breakdown: Map<String, Int>? = null,
    val error: String? = null
)

data class InstituteListResponse(
    val institutes: List<InstituteDto> = emptyList(),
    val error: String? = null
)

data class TechnologyListResponse(
    val technologies: List<TechnologyDto> = emptyList(),
    val error: String? = null
)

data class NotificationPaginatedResponse(
    val notifications: List<NotificationDto> = emptyList(),
    val total: Int = 0,
    val error: String? = null
)

data class AchievementListResponse(
    val achievements: List<AchievementDto> = emptyList(),
    val error: String? = null
)

data class SupportTicketListResponse(
    val tickets: List<SupportTicketDto> = emptyList(),
    val error: String? = null
)

data class SupportTicketCreateResponse(
    val ticket: SupportTicketDto? = null,
    val error: String? = null
)
