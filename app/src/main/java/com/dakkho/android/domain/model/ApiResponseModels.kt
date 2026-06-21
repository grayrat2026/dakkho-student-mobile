package com.dakkho.android.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// ── Auth Request/Response Models ──

@JsonClass(generateAdapter = true)
data class SignupRequest(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String,
    @Json(name = "full_name") val fullName: String,
    @Json(name = "institute_id") val instituteId: String?,
    @Json(name = "technology") val technology: String?,
    @Json(name = "phone") val phone: String?
)

@JsonClass(generateAdapter = true)
data class LoginRequest(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String
)

@JsonClass(generateAdapter = true)
data class VerifyOtpRequest(
    @Json(name = "email") val email: String,
    @Json(name = "otp") val otp: String
)

@JsonClass(generateAdapter = true)
data class ForgotPasswordRequest(
    @Json(name = "email") val email: String
)

@JsonClass(generateAdapter = true)
data class ResetPasswordRequest(
    @Json(name = "token") val token: String,
    @Json(name = "password") val password: String
)

@JsonClass(generateAdapter = true)
data class UpdateProfileRequest(
    @Json(name = "full_name") val fullName: String?,
    @Json(name = "phone") val phone: String?,
    @Json(name = "avatar_url") val avatarUrl: String?,
    @Json(name = "institute_id") val instituteId: String?,
    @Json(name = "technology") val technology: String?
)

@JsonClass(generateAdapter = true)
data class AuthResponse(
    @Json(name = "user") val user: UserDto,
    @Json(name = "token") val token: String,
    @Json(name = "refresh_token") val refreshToken: String?
)

@JsonClass(generateAdapter = true)
data class UserDto(
    @Json(name = "id") val id: String,
    @Json(name = "email") val email: String,
    @Json(name = "full_name") val fullName: String,
    @Json(name = "institute_id") val instituteId: String?,
    @Json(name = "technology") val technology: String?,
    @Json(name = "avatar_url") val avatarUrl: String?,
    @Json(name = "role") val role: String,
    @Json(name = "phone") val phone: String?,
    @Json(name = "is_verified") val isVerified: Boolean?,
    @Json(name = "created_at") val createdAt: String?
)

// ── Course Models ──

@JsonClass(generateAdapter = true)
data class CourseDto(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String?,
    @Json(name = "instructor_id") val instructorId: String,
    @Json(name = "instructor_name") val instructorName: String?,
    @Json(name = "technology") val technology: String?,
    @Json(name = "price") val price: Double?,
    @Json(name = "discounted_price") val discountedPrice: Double?,
    @Json(name = "thumbnail_url") val thumbnailUrl: String?,
    @Json(name = "is_published") val isPublished: Boolean?,
    @Json(name = "rating") val rating: Float?,
    @Json(name = "enrollment_count") val enrollmentCount: Int?,
    @Json(name = "duration_hours") val durationHours: Float?,
    @Json(name = "level") val level: String?,
    @Json(name = "created_at") val createdAt: String?
)

@JsonClass(generateAdapter = true)
data class CourseDetailDto(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String?,
    @Json(name = "instructor_id") val instructorId: String,
    @Json(name = "instructor_name") val instructorName: String?,
    @Json(name = "instructor_avatar") val instructorAvatar: String?,
    @Json(name = "technology") val technology: String?,
    @Json(name = "price") val price: Double?,
    @Json(name = "discounted_price") val discountedPrice: Double?,
    @Json(name = "thumbnail_url") val thumbnailUrl: String?,
    @Json(name = "is_published") val isPublished: Boolean?,
    @Json(name = "rating") val rating: Float?,
    @Json(name = "enrollment_count") val enrollmentCount: Int?,
    @Json(name = "duration_hours") val durationHours: Float?,
    @Json(name = "level") val level: String?,
    @Json(name = "what_you_learn") val whatYouLearn: List<String>?,
    @Json(name = "requirements") val requirements: List<String>?,
    @Json(name = "created_at") val createdAt: String?
)

@JsonClass(generateAdapter = true)
data class CurriculumDto(
    @Json(name = "sections") val sections: List<SectionDto>?
)

@JsonClass(generateAdapter = true)
data class SectionDto(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String,
    @Json(name = "order") val order: Int?,
    @Json(name = "lessons") val lessons: List<LessonDto>?
)

@JsonClass(generateAdapter = true)
data class LessonDto(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String,
    @Json(name = "type") val type: String?,
    @Json(name = "duration_seconds") val durationSeconds: Int?,
    @Json(name = "is_free") val isFree: Boolean?,
    @Json(name = "order") val order: Int?,
    @Json(name = "video_url") val videoUrl: String?
)

// ── Enrollment Models ──

@JsonClass(generateAdapter = true)
data class EnrollmentStatusDto(
    @Json(name = "is_enrolled") val isEnrolled: Boolean,
    @Json(name = "enrollment_id") val enrollmentId: String?,
    @Json(name = "progress") val progress: Float?,
    @Json(name = "enrolled_at") val enrolledAt: String?
)

@JsonClass(generateAdapter = true)
data class PackageDto(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "description") val description: String?,
    @Json(name = "price") val price: Double?,
    @Json(name = "course_ids") val courseIds: List<String>?,
    @Json(name = "is_active") val isActive: Boolean?,
    @Json(name = "created_at") val createdAt: String?
)

@JsonClass(generateAdapter = true)
data class WatchHistoryDto(
    @Json(name = "id") val id: String,
    @Json(name = "video_id") val videoId: String,
    @Json(name = "course_id") val courseId: String,
    @Json(name = "video_title") val videoTitle: String?,
    @Json(name = "course_title") val courseTitle: String?,
    @Json(name = "thumbnail_url") val thumbnailUrl: String?,
    @Json(name = "progress_seconds") val progressSeconds: Int?,
    @Json(name = "total_seconds") val totalSeconds: Int?,
    @Json(name = "completed") val completed: Boolean?,
    @Json(name = "last_watched_at") val lastWatchedAt: String?
)

@JsonClass(generateAdapter = true)
data class StreamSessionDto(
    @Json(name = "session_id") val sessionId: String,
    @Json(name = "stream_url") val streamUrl: String,
    @Json(name = "drm_license_url") val drmLicenseUrl: String?,
    @Json(name = "expires_at") val expiresAt: String?
)

@JsonClass(generateAdapter = true)
data class PaymentDto(
    @Json(name = "order_id") val orderId: String,
    @Json(name = "payment_url") val paymentUrl: String?,
    @Json(name = "amount") val amount: Double,
    @Json(name = "currency") val currency: String?
)

@JsonClass(generateAdapter = true)
data class PaymentStatusDto(
    @Json(name = "order_id") val orderId: String,
    @Json(name = "status") val status: String,
    @Json(name = "transaction_id") val transactionId: String?,
    @Json(name = "paid_at") val paidAt: String?
)

// ── Instructor Models ──

@JsonClass(generateAdapter = true)
data class InstructorDto(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "avatar_url") val avatarUrl: String?,
    @Json(name = "title") val title: String?,
    @Json(name = "course_count") val courseCount: Int?,
    @Json(name = "student_count") val studentCount: Int?,
    @Json(name = "rating") val rating: Float?
)

@JsonClass(generateAdapter = true)
data class InstructorDetailDto(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "avatar_url") val avatarUrl: String?,
    @Json(name = "title") val title: String?,
    @Json(name = "bio") val bio: String?,
    @Json(name = "course_count") val courseCount: Int?,
    @Json(name = "student_count") val studentCount: Int?,
    @Json(name = "rating") val rating: Float?,
    @Json(name = "courses") val courses: List<CourseDto>?
)

@JsonClass(generateAdapter = true)
data class InstituteDto(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "logo_url") val logoUrl: String?,
    @Json(name = "description") val description: String?
)

@JsonClass(generateAdapter = true)
data class TechnologyDto(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "icon_url") val iconUrl: String?,
    @Json(name = "course_count") val courseCount: Int?
)

@JsonClass(generateAdapter = true)
data class AchievementDto(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String?,
    @Json(name = "icon_url") val iconUrl: String?,
    @Json(name = "earned_at") val earnedAt: String?
)

@JsonClass(generateAdapter = true)
data class SupportTicketDto(
    @Json(name = "id") val id: String,
    @Json(name = "subject") val subject: String,
    @Json(name = "status") val status: String?,
    @Json(name = "created_at") val createdAt: String?,
    @Json(name = "updated_at") val updatedAt: String?
)

@JsonClass(generateAdapter = true)
data class CreateTicketRequest(
    @Json(name = "subject") val subject: String,
    @Json(name = "message") val message: String,
    @Json(name = "category") val category: String?
)
