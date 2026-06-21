package com.dakkho.android.data.repository

import com.dakkho.android.data.api.InstructorApiService
import com.dakkho.android.domain.model.Course
import com.dakkho.android.domain.model.CourseDto
import com.dakkho.android.domain.model.Instructor
import com.dakkho.android.domain.model.InstructorDetail
import com.dakkho.android.domain.model.InstructorDetailDto
import com.dakkho.android.domain.model.InstructorDto
import com.dakkho.android.domain.model.SocialLinks
import com.dakkho.android.domain.repository.InstructorRepository
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InstructorRepositoryImpl @Inject constructor(
    private val instructorApiService: InstructorApiService
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
}
