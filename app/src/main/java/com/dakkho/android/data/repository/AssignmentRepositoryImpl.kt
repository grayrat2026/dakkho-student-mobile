package com.dakkho.android.data.repository

import com.dakkho.android.data.api.AssignmentApiService
import com.dakkho.android.domain.model.AssignmentDto
import com.dakkho.android.domain.model.AssignmentItem
import com.dakkho.android.domain.model.AssignmentStatus
import com.dakkho.android.domain.repository.AssignmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssignmentRepositoryImpl @Inject constructor(
    private val assignmentApiService: AssignmentApiService
) : AssignmentRepository {

    /** In-memory cache for assignments per course, keyed by courseId */
    private val assignmentsCache = MutableStateFlow<Map<String, List<AssignmentItem>>>(emptyMap())

    override fun getAssignmentsFlow(courseId: String): Flow<List<AssignmentItem>> {
        return assignmentsCache.map { cache ->
            cache[courseId] ?: emptyList()
        }
    }

    override suspend fun getAssignments(courseId: String): List<AssignmentItem> {
        return assignmentsCache.value[courseId] ?: emptyList()
    }

    override suspend fun getAssignmentById(courseId: String, assignmentId: String): AssignmentItem? {
        return assignmentsCache.value[courseId]?.find { it.id == assignmentId }
    }

    override suspend fun syncAssignments(courseId: String): Result<List<AssignmentItem>> {
        return try {
            val response = assignmentApiService.getAssignments(courseId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    val items = body.data.map { mapDtoToDomain(it) }
                    // Update cache
                    assignmentsCache.value = assignmentsCache.value.toMutableMap().apply {
                        this[courseId] = items
                    }
                    Result.success(items)
                } else {
                    getCachedAssignments(courseId)
                }
            } else {
                Timber.e("Sync assignments API error: ${response.code()}")
                getCachedAssignments(courseId)
            }
        } catch (e: Exception) {
            Timber.e(e, "Sync assignments error, falling back to cache")
            getCachedAssignments(courseId)
        }
    }

    override suspend fun submitAssignment(
        courseId: String,
        assignmentId: String,
        filePath: String
    ): Result<AssignmentItem> {
        return try {
            val file = File(filePath)
            if (!file.exists()) {
                return Result.failure(Exception("File not found: $filePath"))
            }

            val requestBody = file.asRequestBody("application/octet-stream".toMediaTypeOrNull())
            val multipartBody = MultipartBody.Part.createFormData(
                name = "file",
                filename = file.name,
                body = requestBody
            )

            val response = assignmentApiService.submitAssignment(courseId, assignmentId, multipartBody)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    val updatedItem = mapDtoToDomain(body.data)
                    // Update cache with new status
                    val currentList = assignmentsCache.value[courseId]?.toMutableList() ?: mutableListOf()
                    val index = currentList.indexOfFirst { it.id == assignmentId }
                    if (index >= 0) {
                        currentList[index] = updatedItem
                    }
                    assignmentsCache.value = assignmentsCache.value.toMutableMap().apply {
                        this[courseId] = currentList
                    }
                    Result.success(updatedItem)
                } else {
                    Result.failure(Exception("Failed to submit assignment"))
                }
            } else {
                Timber.e("Submit assignment API error: ${response.code()}")
                Result.failure(Exception("Server error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Submit assignment error")
            Result.failure(e)
        }
    }

    private fun getCachedAssignments(courseId: String): Result<List<AssignmentItem>> {
        val cached = assignmentsCache.value[courseId]
        return if (!cached.isNullOrEmpty()) {
            Result.success(cached)
        } else {
            Result.failure(Exception("No cached assignments available"))
        }
    }

    private fun mapDtoToDomain(dto: AssignmentDto): AssignmentItem {
        return AssignmentItem(
            id = dto.id,
            courseId = dto.courseId,
            title = dto.title,
            description = dto.description,
            dueDate = dto.dueDate,
            status = AssignmentStatus.fromString(dto.status),
            maxScore = dto.maxScore,
            score = dto.score,
            submissionUrl = dto.submissionUrl,
            submittedAt = dto.submittedAt,
            gradedAt = dto.gradedAt,
            feedback = dto.feedback,
            createdAt = dto.createdAt
        )
    }
}
