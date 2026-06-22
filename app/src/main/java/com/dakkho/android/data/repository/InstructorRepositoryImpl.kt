package com.dakkho.android.data.repository

import com.dakkho.android.data.api.InstructorApiService
import com.dakkho.android.data.api.LiveClassApiService
import com.dakkho.android.domain.model.Course
import com.dakkho.android.domain.model.CourseDto
import com.dakkho.android.domain.model.Instructor
import com.dakkho.android.domain.model.InstructorDetail
import com.dakkho.android.domain.model.InstructorDetailDto
import com.dakkho.android.domain.model.InstructorDto
import com.dakkho.android.domain.repository.InstructorReviewsResult
import com.dakkho.android.domain.model.LiveClass
import com.dakkho.android.domain.model.LiveClassDto
import com.dakkho.android.domain.model.LiveClassStatus
import com.dakkho.android.domain.model.RatingBreakdown
import com.dakkho.android.domain.model.Review
import com.dakkho.android.domain.model.ReviewDto
import com.dakkho.android.domain.model.SocialLinks
import com.dakkho.android.domain.repository.InstructorRepository
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InstructorRepositoryImpl @Inject constructor(
    private val instructorApiService: InstructorApiService,
    private val liveClassApiService: LiveClassApiService
) : InstructorRepository {

    override suspend fun getInstructors(
        limit: Int,
        offset: Int,
        search: String
    ): Result<Pair<List<Instructor>, Int>> {
        return try {
            val response = instructorApiService.getInstructorsPaginated(limit, offset, search)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.error == null) {
                    val instructors = body.instructors.map { mapInstructorDtoToDomain(it) }
                    Result.success(Pair(instructors, body.total))
                } else {
                    Result.failure(Exception(body?.error ?: "Failed to load instructors"))
                }
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getInstructorDetail(instructorId: String): Result<InstructorDetail> {
        return try {
            val response = instructorApiService.getInstructorDetail(instructorId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.instructor != null && body.error == null) {
                    Result.success(mapInstructorDetailDtoToDomain(body.instructor))
                } else {
                    Result.failure(Exception(body?.error ?: "Instructor not found"))
                }
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getInstructorCourses(
        instructorId: String,
        limit: Int,
        offset: Int
    ): Result<Pair<List<Course>, Int>> {
        return try {
            val response = instructorApiService.getInstructorCourses(instructorId, limit, offset)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.error == null) {
                    val courses = body.courses.map { mapCourseDtoToDomain(it) }
                    Result.success(Pair(courses, body.total))
                } else {
                    Result.failure(Exception(body?.error ?: "Failed to load courses"))
                }
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getInstructorReviews(
        instructorId: String,
        limit: Int,
        offset: Int,
        rating: Int?
    ): Result<InstructorReviewsResult> {
        return try {
            val response = instructorApiService.getInstructorReviews(instructorId, limit, offset, rating)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.error == null) {
                    val reviews = body.reviews.map { dto: ReviewDto -> mapReviewDtoToDomain(dto) }
                    val breakdown = parseRatingBreakdown(body.rating_breakdown)
                    Result.success(
                        InstructorReviewsResult(
                            reviews = reviews,
                            total = body.total,
                            averageRating = body.average_rating,
                            ratingBreakdown = breakdown
                        )
                    )
                } else {
                    Result.failure(Exception(body?.error ?: "Failed to load reviews"))
                }
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getInstructorLiveClasses(
        instructorId: String,
        limit: Int,
        offset: Int
    ): Result<Pair<List<LiveClass>, Int>> {
        return try {
            val response = liveClassApiService.getLiveClasses(instructorId, limit, offset)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.error == null) {
                    val liveClasses = body.liveClasses.map { mapLiveClassDtoToDomain(it) }
                    Result.success(Pair(liveClasses, body.total))
                } else {
                    Result.failure(Exception(body?.error ?: "Failed to load live classes"))
                }
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── DTO → Domain Mappers ──

    private fun mapInstructorDtoToDomain(dto: InstructorDto): Instructor {
        return Instructor(
            id = dto.id,
            name = dto.name,
            avatarUrl = dto.avatarUrl,
            title = dto.specialization ?: dto.title,
            courseCount = dto.totalCourses ?: dto.courseCount ?: 0,
            studentCount = dto.totalStudents ?: dto.studentCount ?: 0,
            rating = dto.rating ?: 0f
        )
    }

    private fun mapInstructorDetailDtoToDomain(
        dto: InstructorDetailDto
    ): InstructorDetail {
        val socialLinks = parseSocialLinks(dto.socialLinks)
        val courses = dto.courses?.map { mapCourseDtoToDomain(it) } ?: emptyList()

        return InstructorDetail(
            id = dto.id,
            name = dto.name,
            avatarUrl = dto.avatarUrl,
            coverUrl = dto.coverUrl,
            title = dto.specialization ?: dto.title,
            bio = dto.bio,
            specialization = dto.specialization,
            email = dto.email,
            courseCount = dto.totalCourses ?: dto.courseCount ?: 0,
            studentCount = dto.totalStudents ?: dto.studentCount ?: 0,
            rating = dto.rating ?: 0f,
            socialLinks = socialLinks,
            isActive = dto.isActive ?: true,
            createdAt = dto.createdAt,
            courses = courses
        )
    }

    private fun mapCourseDtoToDomain(dto: CourseDto): Course {
        return Course(
            id = dto.id,
            title = dto.title,
            description = dto.description,
            instructorId = dto.instructorId,
            instructorName = dto.instructorName,
            technology = dto.technology,
            price = dto.price,
            discountedPrice = dto.discountedPrice,
            thumbnailUrl = dto.thumbnailUrl,
            isPublished = dto.isPublished ?: true,
            rating = dto.rating,
            enrollmentCount = dto.enrollmentCount,
            durationHours = dto.durationHours,
            level = dto.level,
            createdAt = dto.createdAt
        )
    }

    private fun mapReviewDtoToDomain(dto: ReviewDto): Review {
        return Review(
            id = dto.id,
            userId = dto.userId,
            courseId = dto.courseId,
            userName = dto.userName,
            userAvatar = dto.userAvatar,
            rating = dto.rating,
            title = dto.title,
            comment = dto.comment,
            createdAt = dto.createdAt
        )
    }

    private fun mapLiveClassDtoToDomain(dto: LiveClassDto): LiveClass {
        return LiveClass(
            id = dto.id,
            title = dto.title,
            description = dto.description,
            instructorId = dto.instructorId,
            instructorName = dto.instructorName,
            courseId = dto.courseId,
            courseName = dto.courseName,
            scheduledAt = dto.scheduledAt,
            startedAt = dto.startedAt,
            endedAt = dto.endedAt,
            durationMinutes = dto.durationMinutes ?: 60,
            meetingUrl = dto.meetingUrl,
            thumbnailUrl = dto.thumbnailUrl,
            status = when (dto.status?.lowercase()) {
                "live" -> LiveClassStatus.LIVE
                "ended" -> LiveClassStatus.ENDED
                "cancelled" -> LiveClassStatus.CANCELLED
                else -> LiveClassStatus.SCHEDULED
            },
            isRecorded = dto.isRecorded ?: false,
            recordingUrl = dto.recordingUrl
        )
    }

    /**
     * Parse social_links JSON string from D1.
     * Format: {"youtube":"...", "github":"...", "facebook":"...", "linkedin":"...", "website":"..."}
     */
    private fun parseSocialLinks(json: String?): SocialLinks {
        if (json.isNullOrBlank() || json == "{}") return SocialLinks()
        return try {
            val obj = JSONObject(json)
            SocialLinks(
                youtube = obj.optString("youtube")?.ifBlank { null },
                github = obj.optString("github")?.ifBlank { null },
                facebook = obj.optString("facebook")?.ifBlank { null },
                linkedin = obj.optString("linkedin")?.ifBlank { null },
                website = obj.optString("website")?.ifBlank { null }
            )
        } catch (e: Exception) {
            SocialLinks()
        }
    }

    /**
     * Parse rating breakdown from API response.
     * Expected format: {"5": 10, "4": 5, "3": 2, "2": 1, "1": 0}
     */
    private fun parseRatingBreakdown(breakdown: Map<String, Int>?): RatingBreakdown {
        if (breakdown == null) return RatingBreakdown()
        return RatingBreakdown(
            star5 = breakdown["5"] ?: breakdown["five"] ?: 0,
            star4 = breakdown["4"] ?: breakdown["four"] ?: 0,
            star3 = breakdown["3"] ?: breakdown["three"] ?: 0,
            star2 = breakdown["2"] ?: breakdown["two"] ?: 0,
            star1 = breakdown["1"] ?: breakdown["one"] ?: 0
        )
    }
}
