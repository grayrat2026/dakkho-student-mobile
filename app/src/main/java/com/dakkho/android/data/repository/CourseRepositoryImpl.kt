package com.dakkho.android.data.repository

import com.dakkho.android.data.api.CourseApiService
import com.dakkho.android.data.db.Converters
import com.dakkho.android.data.db.dao.CourseDao
import com.dakkho.android.data.db.dao.CourseDetailDao
import com.dakkho.android.data.db.entity.CourseDetailEntity
import com.dakkho.android.data.db.entity.CourseEntity
import com.dakkho.android.domain.model.Course
import com.dakkho.android.domain.model.CourseDetail
import com.dakkho.android.domain.model.CoursePackage
import com.dakkho.android.domain.model.Curriculum
import com.dakkho.android.domain.model.Lesson
import com.dakkho.android.domain.model.LessonResources
import com.dakkho.android.domain.model.QuizItem
import com.dakkho.android.domain.model.ResourceFile
import com.dakkho.android.domain.model.Review
import com.dakkho.android.domain.model.Section
import com.dakkho.android.domain.model.Subject
import com.dakkho.android.domain.model.SubjectClass
import com.dakkho.android.domain.model.Unit
import com.dakkho.android.domain.repository.CourseRepository
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CourseRepositoryImpl @Inject constructor(
    private val courseApiService: CourseApiService,
    private val courseDao: CourseDao,
    private val courseDetailDao: CourseDetailDao
) : CourseRepository {

    private val converters = Converters()

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
                    val detail = mapCourseDetailDtoToDomain(body.data)
                    // Cache course detail for offline access
                    cacheCourseDetail(detail)
                    Result.success(detail)
                } else {
                    // Try offline cache
                    getCachedCourseDetail(courseId)
                }
            } else {
                getCachedCourseDetail(courseId)
            }
        } catch (e: Exception) {
            Timber.e(e, "Get course detail error, falling back to cache")
            getCachedCourseDetail(courseId)
        }
    }

    override suspend fun getCourseCurriculum(courseId: String): Result<Curriculum> {
        return try {
            val response = courseApiService.getCourseCurriculum(courseId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    val dto = body.data
                    val curriculum = if (dto.subjects != null && dto.subjects.isNotEmpty()) {
                        // New hierarchy: Subject → Class → Unit → Lesson
                        Curriculum(
                            sections = dto.subjects.map { subjectDto ->
                                Subject(
                                    id = subjectDto.id,
                                    title = subjectDto.title,
                                    order = subjectDto.order ?: 0,
                                    classes = subjectDto.classes?.map { classDto ->
                                        SubjectClass(
                                            id = classDto.id,
                                            title = classDto.title,
                                            order = classDto.order ?: 0,
                                            units = classDto.units?.map { unitDto ->
                                                Unit(
                                                    id = unitDto.id,
                                                    title = unitDto.title,
                                                    order = unitDto.order ?: 0,
                                                    lessons = unitDto.lessons?.map { mapLessonDtoToDomain(it) } ?: emptyList()
                                                )
                                            } ?: emptyList()
                                        )
                                    } ?: emptyList()
                                )
                            }
                        )
                    } else {
                        // Backward compat: Section → Lesson (mapped as Subject with single Class/Unit)
                        Curriculum(
                            sections = dto.sections?.map { sectionDto ->
                                Subject(
                                    id = sectionDto.id,
                                    title = sectionDto.title,
                                    order = sectionDto.order ?: 0,
                                    classes = listOf(
                                        SubjectClass(
                                            id = "${sectionDto.id}_class",
                                            title = sectionDto.title,
                                            order = 0,
                                            units = listOf(
                                                Unit(
                                                    id = "${sectionDto.id}_unit",
                                                    title = sectionDto.title,
                                                    order = 0,
                                                    lessons = sectionDto.lessons?.map { mapLessonDtoToDomain(it) } ?: emptyList()
                                                )
                                            )
                                        )
                                    )
                                )
                            } ?: emptyList()
                        )
                    }
                    Result.success(curriculum)
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

    override suspend fun getCourseReviews(courseId: String, page: Int, limit: Int): Result<List<Review>> {
        return try {
            val response = courseApiService.getCourseReviews(courseId, page, limit)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    Result.success(
                        body.data.items.map { dto ->
                            Review(
                                id = dto.id,
                                userId = dto.userId,
                                courseId = dto.courseId,
                                userName = dto.userName,
                                userAvatar = dto.userAvatar,
                                rating = dto.rating,
                                comment = dto.comment,
                                createdAt = dto.createdAt
                            )
                        }
                    )
                } else {
                    Result.success(emptyList())
                }
            } else {
                Result.success(emptyList())
            }
        } catch (e: Exception) {
            Timber.e(e, "Get course reviews error")
            Result.failure(e)
        }
    }

    override suspend fun getCoursePackages(courseId: String): Result<List<CoursePackage>> {
        return try {
            val response = courseApiService.getCoursePackages(courseId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    Result.success(
                        body.data.map { dto ->
                            CoursePackage(
                                id = dto.id,
                                name = dto.name,
                                description = dto.description,
                                price = dto.price,
                                isFree = dto.isFree ?: false,
                                features = dto.features ?: emptyList(),
                                courseIds = dto.courseIds ?: emptyList(),
                                isActive = dto.isActive ?: true
                            )
                        }
                    )
                } else {
                    Result.success(emptyList())
                }
            } else {
                Result.success(emptyList())
            }
        } catch (e: Exception) {
            Timber.e(e, "Get course packages error")
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

    // ── Cache Helpers ──

    private suspend fun cacheCourseDetail(detail: CourseDetail) {
        try {
            val entity = CourseDetailEntity(
                id = detail.id,
                courseId = detail.id,
                title = detail.title,
                description = detail.description,
                instructorId = detail.instructorId,
                instructorName = detail.instructorName,
                instructorAvatar = detail.instructorAvatar,
                technology = detail.technology,
                price = detail.price,
                discountedPrice = detail.discountedPrice,
                thumbnailUrl = detail.thumbnailUrl,
                isPublished = detail.isPublished,
                rating = detail.rating,
                reviewCount = detail.reviewCount,
                enrollmentCount = detail.enrollmentCount,
                durationHours = detail.durationHours,
                level = detail.level,
                whatYouLearn = converters.fromStringList(detail.whatYouLearn),
                requirements = converters.fromStringList(detail.requirements),
                targetAudience = converters.fromStringList(detail.targetAudience),
                createdAt = detail.createdAt
            )
            courseDetailDao.insert(entity)
        } catch (e: Exception) {
            Timber.e(e, "Cache course detail error")
        }
    }

    private suspend fun getCachedCourseDetail(courseId: String): Result<CourseDetail> {
        return try {
            val cached = courseDetailDao.getCourseDetail(courseId)
            if (cached != null) {
                Result.success(mapCourseDetailEntityToDomain(cached))
            } else {
                Result.failure(Exception("Course detail not found (offline)"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Course detail not available offline"))
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

    // ── Mapping Functions ──

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
            reviewCount = dto.reviewCount,
            enrollmentCount = dto.enrollmentCount,
            durationHours = dto.durationHours,
            level = dto.level,
            whatYouLearn = dto.whatYouLearn ?: emptyList(),
            requirements = dto.requirements ?: emptyList(),
            targetAudience = dto.targetAudience ?: emptyList(),
            createdAt = dto.createdAt
        )
    }

    private fun mapCourseDetailEntityToDomain(entity: CourseDetailEntity): CourseDetail {
        return CourseDetail(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            instructorId = entity.instructorId,
            instructorName = entity.instructorName,
            instructorAvatar = entity.instructorAvatar,
            technology = entity.technology,
            price = entity.price,
            discountedPrice = entity.discountedPrice,
            thumbnailUrl = entity.thumbnailUrl,
            isPublished = entity.isPublished,
            rating = entity.rating,
            reviewCount = entity.reviewCount,
            enrollmentCount = entity.enrollmentCount,
            durationHours = entity.durationHours,
            level = entity.level,
            whatYouLearn = converters.toStringList(entity.whatYouLearn) ?: emptyList(),
            requirements = converters.toStringList(entity.requirements) ?: emptyList(),
            targetAudience = converters.toStringList(entity.targetAudience) ?: emptyList(),
            createdAt = entity.createdAt
        )
    }

    private fun mapLessonDtoToDomain(dto: com.dakkho.android.domain.model.LessonDto): Lesson {
        return Lesson(
            id = dto.id,
            title = dto.title,
            type = dto.type,
            durationSeconds = dto.durationSeconds,
            isFree = dto.isFree ?: false,
            order = dto.order ?: 0,
            videoUrl = dto.videoUrl,
            label = dto.label,
            resources = dto.resources?.let { res ->
                LessonResources(
                    lectureSheets = res.lectureSheets?.map { mapResourceFileDtoToDomain(it) } ?: emptyList(),
                    pdfs = res.pdfs?.map { mapResourceFileDtoToDomain(it) } ?: emptyList(),
                    notes = res.notes?.map { mapResourceFileDtoToDomain(it) } ?: emptyList(),
                    quizzes = res.quizzes?.map { mapQuizItemDtoToDomain(it) } ?: emptyList(),
                    hasTimestamps = res.hasTimestamps ?: false,
                    hasQA = res.hasQA ?: false
                )
            },
            progress = dto.progress ?: 0f,
            isCompleted = dto.isCompleted ?: false
        )
    }

    private fun mapResourceFileDtoToDomain(dto: com.dakkho.android.domain.model.ResourceFileDto): ResourceFile {
        return ResourceFile(
            id = dto.id,
            title = dto.title,
            fileUrl = dto.fileUrl,
            fileSize = dto.fileSize,
            fileType = dto.fileType
        )
    }

    private fun mapQuizItemDtoToDomain(dto: com.dakkho.android.domain.model.QuizItemDto): QuizItem {
        return QuizItem(
            id = dto.id,
            title = dto.title,
            type = dto.type ?: "mcq",
            questionCount = dto.questionCount ?: 0,
            durationMinutes = dto.durationMinutes,
            isCompleted = dto.isCompleted ?: false,
            score = dto.score
        )
    }
}
