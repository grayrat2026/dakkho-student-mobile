package com.dakkho.android.data.repository

import com.dakkho.android.data.api.TechnologyApiService
import com.dakkho.android.data.db.dao.DepartmentDao
import com.dakkho.android.data.db.entity.DepartmentEntity
import com.dakkho.android.domain.model.Course
import com.dakkho.android.domain.model.Instructor
import com.dakkho.android.domain.model.Technology
import com.dakkho.android.domain.repository.DepartmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DepartmentRepositoryImpl @Inject constructor(
    private val technologyApiService: TechnologyApiService,
    private val departmentDao: DepartmentDao
) : DepartmentRepository {

    override fun getAllDepartments(): Flow<List<Technology>> {
        return departmentDao.getAllDepartments().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getDepartmentBySlug(slug: String): Technology? {
        // Try Room first
        val cached = departmentDao.getDepartmentBySlug(slug)
        if (cached != null) return cached.toDomain()

        // Fallback: fetch from API and cache
        return try {
            val response = technologyApiService.getTechnologyBySlug(slug)
            if (response.isSuccessful) {
                response.body()?.data?.let { dto ->
                    val tech = Technology(
                        id = dto.id,
                        name = dto.name,
                        slug = dto.slug ?: slug,
                        shortCode = dto.shortCode ?: "",
                        description = dto.description,
                        iconUrl = dto.iconUrl,
                        bannerUrl = dto.bannerUrl,
                        courseCount = dto.courseCount ?: 0,
                        instructorCount = dto.instructorCount ?: 0,
                        studentCount = dto.studentCount ?: 0,
                        semesterCount = dto.semesterCount ?: 8,
                        isActive = dto.isActive ?: true
                    )
                    departmentDao.insert(tech.toEntity())
                    tech
                }
            } else null
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getCoursesForDepartment(technology: String, page: Int): List<Course> {
        return try {
            val response = technologyApiService.getCoursesByTechnology(technology, page)
            if (response.isSuccessful) {
                val body = response.body()?.data
                @Suppress("UNCHECKED_CAST")
                val coursesData = (body?.get("courses") as? List<Map<String, Any?>>) ?: emptyList()
                coursesData.mapNotNull { map ->
                    try {
                        Course(
                            id = map["id"] as? String ?: return@mapNotNull null,
                            title = map["title"] as? String ?: "",
                            description = map["description"] as? String,
                            instructorId = map["instructor_id"] as? String ?: "",
                            instructorName = map["instructor_name"] as? String,
                            technology = map["technology"] as? String,
                            price = (map["price"] as? Number)?.toDouble(),
                            discountedPrice = (map["discounted_price"] as? Number)?.toDouble(),
                            thumbnailUrl = map["thumbnail_url"] as? String,
                            rating = (map["rating"] as? Number)?.toFloat(),
                            enrollmentCount = (map["enrollment_count"] as? Number)?.toInt(),
                            durationHours = (map["duration_hours"] as? Number)?.toFloat(),
                            level = map["level"] as? String
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
            } else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getInstructorsForDepartment(technology: String): List<Instructor> {
        return try {
            val response = technologyApiService.getInstructorsByTechnology(technology)
            if (response.isSuccessful) {
                val body = response.body()?.data
                body?.mapNotNull { map ->
                    try {
                        Instructor(
                            id = map["id"] as? String ?: return@mapNotNull null,
                            name = map["name"] as? String ?: "",
                            avatarUrl = map["avatar_url"] as? String,
                            title = map["title"] as? String,
                            courseCount = (map["course_count"] as? Number)?.toInt() ?: 0,
                            studentCount = (map["student_count"] as? Number)?.toInt() ?: 0,
                            rating = (map["rating"] as? Number)?.toFloat() ?: 0f
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()
            } else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun refreshDepartments() {
        try {
            val response = technologyApiService.getTechnologies()
            if (response.isSuccessful) {
                val technologies = response.body()?.data ?: emptyList()
                val entities = technologies.map { dto ->
                    DepartmentEntity(
                        id = dto.id,
                        slug = dto.slug ?: dto.name.lowercase().replace(" ", "-"),
                        name = dto.name,
                        shortCode = dto.shortCode ?: dto.name.take(3).uppercase(),
                        description = dto.description,
                        iconUrl = dto.iconUrl,
                        bannerUrl = dto.bannerUrl,
                        courseCount = dto.courseCount ?: 0,
                        instructorCount = dto.instructorCount ?: 0,
                        studentCount = dto.studentCount ?: 0,
                        semesterCount = dto.semesterCount ?: 8,
                        isActive = dto.isActive ?: true
                    )
                }
                departmentDao.insertAll(entities)
            }
        } catch (e: Exception) {
            // Silently fail — Room cache is still available
        }
    }

    override fun searchDepartments(query: String): Flow<List<Technology>> {
        return departmentDao.getAllDepartments().map { entities ->
            entities.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.shortCode.contains(query, ignoreCase = true) ||
                it.description?.contains(query, ignoreCase = true) == true
            }.map { it.toDomain() }
        }
    }

    // ── Mappers ──

    private fun DepartmentEntity.toDomain() = Technology(
        id = id,
        name = name,
        slug = slug,
        shortCode = shortCode,
        description = description,
        iconUrl = iconUrl,
        bannerUrl = bannerUrl,
        courseCount = courseCount,
        instructorCount = instructorCount,
        studentCount = studentCount,
        semesterCount = semesterCount,
        isActive = isActive
    )

    private fun Technology.toEntity() = DepartmentEntity(
        id = id,
        slug = slug,
        name = name,
        shortCode = shortCode,
        description = description,
        iconUrl = iconUrl,
        bannerUrl = bannerUrl,
        courseCount = courseCount,
        instructorCount = instructorCount,
        studentCount = studentCount,
        semesterCount = semesterCount,
        isActive = isActive
    )
}
