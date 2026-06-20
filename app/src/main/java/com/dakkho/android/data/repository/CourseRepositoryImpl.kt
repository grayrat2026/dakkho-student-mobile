package com.dakkho.android.data.repository

import com.dakkho.android.data.api.CourseApiService
import com.dakkho.android.data.db.dao.CourseDao
import com.dakkho.android.data.db.entity.CourseEntity
import com.dakkho.android.domain.model.Course
import com.dakkho.android.domain.model.CourseDetail
import com.dakkho.android.domain.model.Curriculum
import com.dakkho.android.domain.model.Lesson
import com.dakkho.android.domain.model.Section
import com.dakkho.android.domain.repository.CourseRepository
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CourseRepositoryImpl @Inject constructor(
    private val courseApiService: CourseApiService,
    private val courseDao: CourseDao
) : CourseRepository {

    override suspend fun getCourses(params: Map<String, String>): Result<List<Course>> {
        return try {
            val response = courseApiService.getCourses(params)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    val courses = body.data.items.map { mapCourseDtoToDomain(it) }
                    // Cache courses
                    courseDao.insertAll(body.data.items.map { mapCourseDtoToEntity(it) })
                    Result.success(courses)
                } else {
                    getCachedCourses()
                }
            } else {
                getCachedCourses()
            }
        } catch (e: Exception) {
            Timber.e(e, "Get courses error, falling back to cache")
            return getCachedCourses()
        }
    }

    override suspend fun getCourseDetail(courseId: String): Result<CourseDetail> {
        return try {
            val response = courseApiService.getCourseDetail(courseId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    Result.success(mapCourseDetailDtoToDomain(body.data))
                } else {
                    Result.failure(Exception(body?.message ?: "Course detail not found"))
                }
            } else {
                Result.failure(Exception("Course detail failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Get course detail error")
            Result.failure(e)
        }
    }

    override suspend fun getCourseCurriculum(courseId: String): Result<Curriculum> {
        return try {
            val response = courseApiService.getCourseCurriculum(courseId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    val curriculum = body.data
                    Result.success(
                        Curriculum(
                            sections = curriculum.sections?.map { section ->
                                Section(
                                    id = section.id,
                                    title = section.title,
                                    order = section.order ?: 0,
                                    lessons = section.lessons?.map { lesson ->
                                        Lesson(
                                            id = lesson.id,
                                            title = lesson.title,
                                            type = lesson.type,
                                            durationSeconds = lesson.durationSeconds,
                                            isFree = lesson.isFree ?: false,
                                            order = lesson.order ?: 0,
                                            videoUrl = lesson.videoUrl
                                        )
                                    } ?: emptyList()
                                )
                            } ?: emptyList()
                        )
                    )
                } else {
                    Result.failure(Exception(body?.message ?: "Curriculum not found"))
                }
            } else {
                Result.failure(Exception("Curriculum failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Get course curriculum error")
            Result.failure(e)
        }
    }

    override suspend fun searchCourses(query: String): Result<List<Course>> {
        return try {
            val response = courseApiService.getCourses(mapOf("search" to query))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    Result.success(body.data.items.map { mapCourseDtoToDomain(it) })
                } else {
                    // Fallback to local search
                    val localResults = courseDao.searchCourses(query)
                    Result.success(localResults.map { mapCourseEntityToDomain(it) })
                }
            } else {
                val localResults = courseDao.searchCourses(query)
                Result.success(localResults.map { mapCourseEntityToDomain(it) })
            }
        } catch (e: Exception) {
            Timber.e(e, "Search courses error, falling back to local search")
            val localResults = courseDao.searchCourses(query)
            Result.success(localResults.map { mapCourseEntityToDomain(it) })
        }
    }

    override suspend fun getCoursesByTechnology(technology: String): Result<List<Course>> {
        return try {
            val response = courseApiService.getCourses(mapOf("technology" to technology))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    Result.success(body.data.items.map { mapCourseDtoToDomain(it) })
                } else {
                    getCachedCoursesByTechnology(technology)
                }
            } else {
                getCachedCoursesByTechnology(technology)
            }
        } catch (e: Exception) {
            Timber.e(e, "Get courses by technology error, falling back to cache")
            return getCachedCoursesByTechnology(technology)
        }
    }

    private suspend fun getCachedCourses(): Result<List<Course>> {
        val cached = courseDao.getCourses()
        return if (cached.isNotEmpty()) {
            Result.success(cached.map { mapCourseEntityToDomain(it) })
        } else {
            Result.failure(Exception("No cached courses available"))
        }
    }

    private suspend fun getCachedCoursesByTechnology(technology: String): Result<List<Course>> {
        val cached = courseDao.getCoursesByTechnology(technology)
        return if (cached.isNotEmpty()) {
            Result.success(cached.map { mapCourseEntityToDomain(it) })
        } else {
            Result.failure(Exception("No cached courses available for technology: $technology"))
        }
    }

    private fun mapCourseDtoToDomain(dto: com.dakkho.android.domain.model.CourseDto): Course {
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

    private fun mapCourseDtoToEntity(dto: com.dakkho.android.domain.model.CourseDto): CourseEntity {
        return CourseEntity(
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

    private fun mapCourseEntityToDomain(entity: CourseEntity): Course {
        return Course(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            instructorId = entity.instructorId,
            instructorName = entity.instructorName,
            technology = entity.technology,
            price = entity.price,
            discountedPrice = entity.discountedPrice,
            thumbnailUrl = entity.thumbnailUrl,
            isPublished = entity.isPublished,
            rating = entity.rating,
            enrollmentCount = entity.enrollmentCount,
            durationHours = entity.durationHours,
            level = entity.level,
            createdAt = entity.createdAt
        )
    }

    private fun mapCourseDetailDtoToDomain(dto: com.dakkho.android.domain.model.CourseDetailDto): CourseDetail {
        return CourseDetail(
            id = dto.id,
            title = dto.title,
            description = dto.description,
            instructorId = dto.instructorId,
            instructorName = dto.instructorName,
            instructorAvatar = dto.instructorAvatar,
            technology = dto.technology,
            price = dto.price,
            discountedPrice = dto.discountedPrice,
            thumbnailUrl = dto.thumbnailUrl,
            isPublished = dto.isPublished ?: true,
            rating = dto.rating,
            enrollmentCount = dto.enrollmentCount,
            durationHours = dto.durationHours,
            level = dto.level,
            whatYouLearn = dto.whatYouLearn ?: emptyList(),
            requirements = dto.requirements ?: emptyList(),
            createdAt = dto.createdAt
        )
    }
}
