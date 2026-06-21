package com.dakkho.android.data.repository

import com.dakkho.android.data.api.SemesterApiService
import com.dakkho.android.data.db.dao.RoutineEntryDao
import com.dakkho.android.data.db.dao.SemesterDao
import com.dakkho.android.data.db.dao.SubjectDao
import com.dakkho.android.data.db.entity.RoutineEntryEntity
import com.dakkho.android.data.db.entity.SemesterEntity
import com.dakkho.android.data.db.entity.SubjectEntity
import com.dakkho.android.domain.model.RoutineEntry
import com.dakkho.android.domain.model.Semester
import com.dakkho.android.domain.model.SemesterProgress
import com.dakkho.android.domain.model.Subject
import com.dakkho.android.domain.repository.SemesterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of SemesterRepository.
 * Fetches data from API, caches in Room, returns as reactive Flows.
 *
 * Bangladesh Diploma system: 7 regular semesters + 8th = ইন্টার্নি (Internship).
 */
@Singleton
class SemesterRepositoryImpl @Inject constructor(
    private val semesterApiService: SemesterApiService,
    private val semesterDao: SemesterDao,
    private val subjectDao: SubjectDao,
    private val routineEntryDao: RoutineEntryDao
) : SemesterRepository {

    override fun getSemestersForDepartment(departmentSlug: String): Flow<List<Semester>> {
        return semesterDao.getSemestersForDepartment(departmentSlug).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getSemester(departmentSlug: String, semesterNumber: Int): Semester? {
        // Try Room first
        val cached = semesterDao.getSemester(departmentSlug, semesterNumber)
        if (cached != null) return cached.toDomain()

        // Fallback: fetch from API and cache
        return try {
            refreshSemesterData(departmentSlug, semesterNumber)
            semesterDao.getSemester(departmentSlug, semesterNumber)?.toDomain()
        } catch (e: Exception) {
            null
        }
    }

    override fun getSubjectsForSemester(departmentSlug: String, semesterNumber: Int): Flow<List<Subject>> {
        return subjectDao.getSubjectsForDepartmentSemester(departmentSlug, semesterNumber).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getRoutineForSemester(departmentSlug: String, semesterNumber: Int): Flow<List<RoutineEntry>> {
        return routineEntryDao.getRoutineForSemester(departmentSlug, semesterNumber).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getSemesterProgress(departmentSlug: String): SemesterProgress {
        // TODO: Integrate with student enrollment data to calculate actual progress
        // For now, return a default based on semesters available
        val semesterCount = semesterDao.countSemestersForDepartment(departmentSlug)
        val totalSemesters = if (semesterCount > 0) semesterCount else 8

        return SemesterProgress(
            currentSemester = 1,
            totalSemesters = totalSemesters,
            progressPercent = 1f / totalSemesters,
            isInternshipSemester = false
        )
    }

    override suspend fun refreshSemesterData(departmentSlug: String, semesterNumber: Int) {
        try {
            // Fetch semesters for this department
            val semestersResponse = semesterApiService.getSemestersForDepartment(departmentSlug)
            if (semestersResponse.isSuccessful) {
                val semesters = semestersResponse.body()?.data ?: emptyList()
                val semesterEntities = semesters.map { dto ->
                    SemesterEntity(
                        id = dto.id,
                        departmentSlug = dto.departmentSlug ?: departmentSlug,
                        number = dto.number,
                        name = dto.name ?: Semester.semesterName(dto.number),
                        subjectCount = dto.subjectCount ?: 0,
                        totalCredits = dto.totalCredits ?: 0,
                        isActive = dto.isActive ?: true
                    )
                }
                semesterDao.insertAll(semesterEntities)
            }

            // Fetch subjects for this specific semester
            val subjectsResponse = semesterApiService.getSubjectsForSemester(
                technologyId = departmentSlug,
                semester = semesterNumber
            )
            if (subjectsResponse.isSuccessful) {
                val body = subjectsResponse.body()?.data
                @Suppress("UNCHECKED_CAST")
                val subjectDocs = (body?.get("documents") as? List<Map<String, Any?>>) ?: emptyList()

                val subjectEntities = subjectDocs.mapNotNull { map ->
                    try {
                        SubjectEntity(
                            id = map["id"] as? String ?: return@mapNotNull null,
                            semesterId = "${departmentSlug}_$semesterNumber",
                            departmentSlug = departmentSlug,
                            semesterNumber = semesterNumber,
                            name = map["name"] as? String ?: "",
                            code = map["code"] as? String ?: map["slug"] as? String ?: "",
                            creditHours = (map["credit_hours"] as? Number)?.toInt() ?: 0,
                            instructorName = map["instructor_name"] as? String,
                            instructorId = map["instructor_id"] as? String,
                            courseId = map["course_id"] as? String,
                            description = map["description"] as? String,
                            syllabusTopics = "[]", // Will be populated from syllabus endpoint
                            sortOrder = (map["sort_order"] as? Number)?.toInt() ?: 0,
                            color = map["color"] as? String,
                            isActive = (map["is_active"] as? Number)?.toInt() == 1
                        )
                    } catch (e: Exception) {
                        null
                    }
                }

                if (subjectEntities.isNotEmpty()) {
                    subjectDao.deleteSubjectsForDepartmentSemester(departmentSlug, semesterNumber)
                    subjectDao.insertAll(subjectEntities)
                }
            }

            // Fetch routine for this semester
            val semesterId = "${departmentSlug}_$semesterNumber"
            val routineResponse = semesterApiService.getRoutineForSemester(semesterId)
            if (routineResponse.isSuccessful) {
                val routines = routineResponse.body()?.data ?: emptyList()
                val routineEntities = routines.map { dto ->
                    RoutineEntryEntity(
                        id = dto.id,
                        subjectId = dto.subjectId,
                        subjectName = dto.subjectName ?: "",
                        subjectCode = dto.subjectCode ?: "",
                        departmentSlug = departmentSlug,
                        semesterNumber = semesterNumber,
                        dayOfWeek = dto.dayOfWeek ?: 1,
                        startTime = dto.startTime ?: "09:00",
                        endTime = dto.endTime ?: "10:30",
                        roomNumber = dto.roomNumber,
                        instructorName = dto.instructorName,
                        color = dto.color
                    )
                }
                if (routineEntities.isNotEmpty()) {
                    routineEntryDao.deleteRoutineForSemester(departmentSlug, semesterNumber)
                    routineEntryDao.insertAll(routineEntities)
                }
            }
        } catch (e: Exception) {
            // Silently fail — Room cache is still available
        }
    }

    override suspend fun refreshAllSemesters(departmentSlug: String) {
        try {
            // Fetch all semesters for this department
            val response = semesterApiService.getSemestersForDepartment(departmentSlug)
            if (response.isSuccessful) {
                val semesters = response.body()?.data ?: emptyList()
                val entities = semesters.map { dto ->
                    SemesterEntity(
                        id = dto.id,
                        departmentSlug = dto.departmentSlug ?: departmentSlug,
                        number = dto.number,
                        name = dto.name ?: Semester.semesterName(dto.number),
                        subjectCount = dto.subjectCount ?: 0,
                        totalCredits = dto.totalCredits ?: 0,
                        isActive = dto.isActive ?: true
                    )
                }
                if (entities.isNotEmpty()) {
                    semesterDao.deleteSemestersForDepartment(departmentSlug)
                    semesterDao.insertAll(entities)
                }

                // Also refresh subjects for each semester
                for (semester in semesters) {
                    try {
                        refreshSemesterData(departmentSlug, semester.number)
                    } catch (_: Exception) {
                        // Continue with other semesters even if one fails
                    }
                }
            }
        } catch (e: Exception) {
            // Silently fail — Room cache is still available
        }
    }

    // ── Mappers ──

    private fun SemesterEntity.toDomain() = Semester(
        id = id,
        departmentSlug = departmentSlug,
        number = number,
        name = name,
        subjectCount = subjectCount,
        totalCredits = totalCredits,
        isActive = isActive
    )

    private fun SubjectEntity.toDomain(): Subject {
        val topics = try {
            val arr = JSONArray(syllabusTopics)
            (0 until arr.length()).map { arr.getString(it) }
        } catch (_: Exception) {
            emptyList()
        }

        return Subject(
            id = id,
            semesterId = semesterId,
            departmentSlug = departmentSlug,
            semesterNumber = semesterNumber,
            name = name,
            code = code,
            creditHours = creditHours,
            instructorName = instructorName,
            instructorId = instructorId,
            courseId = courseId,
            description = description,
            syllabusTopics = topics,
            sortOrder = sortOrder,
            color = color,
            isActive = isActive
        )
    }

    private fun RoutineEntryEntity.toDomain() = RoutineEntry(
        id = id,
        subjectId = subjectId,
        subjectName = subjectName,
        subjectCode = subjectCode,
        dayOfWeek = dayOfWeek,
        startTime = startTime,
        endTime = endTime,
        roomNumber = roomNumber,
        instructorName = instructorName,
        color = color
    )
}
