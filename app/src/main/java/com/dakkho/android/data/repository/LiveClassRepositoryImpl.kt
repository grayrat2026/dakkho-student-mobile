package com.dakkho.android.data.repository

import com.dakkho.android.data.api.StudentLiveClassApiService
import com.dakkho.android.domain.model.LiveClass
import com.dakkho.android.domain.model.LiveClassDto
import com.dakkho.android.domain.model.LiveClassJoinResult
import com.dakkho.android.domain.model.LiveClassStatus
import com.dakkho.android.domain.repository.LiveClassRepository
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LiveClassRepositoryImpl @Inject constructor(
    private val studentLiveClassApi: StudentLiveClassApiService
) : LiveClassRepository {

    override suspend fun getLiveClasses(
        status: String?,
        limit: Int,
        offset: Int
    ): Result<List<LiveClass>> {
        return try {
            val response = studentLiveClassApi.getLiveClasses(status, limit, offset)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val classes = body.liveClasses.map { mapDtoToDomain(it) }
                    Result.success(classes)
                } else {
                    Result.success(emptyList())
                }
            } else {
                Result.failure(Exception("Failed to load live classes: ${response.code()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Get live classes error")
            Result.failure(e)
        }
    }

    override suspend fun getFeaturedLiveClasses(): Result<List<LiveClass>> {
        return try {
            val response = studentLiveClassApi.getFeaturedLiveClasses()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val classes = body.liveClasses.map { mapDtoToDomain(it) }
                    Result.success(classes)
                } else {
                    Result.success(emptyList())
                }
            } else {
                Result.failure(Exception("Failed to load featured classes: ${response.code()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Get featured live classes error")
            Result.failure(e)
        }
    }

    override suspend fun getLiveClassDetail(id: String): Result<LiveClass> {
        return try {
            val response = studentLiveClassApi.getLiveClassDetail(id)
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.liveClass != null) {
                    Result.success(mapDtoToDomain(body.liveClass))
                } else {
                    Result.failure(Exception("Live class not found"))
                }
            } else {
                Result.failure(Exception("Failed to load live class: ${response.code()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Get live class detail error")
            Result.failure(e)
        }
    }

    override suspend fun joinLiveClass(id: String): Result<LiveClassJoinResult> {
        return try {
            val response = studentLiveClassApi.joinLiveClass(id)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.error == null) {
                    Result.success(
                        LiveClassJoinResult(
                            token = body.token,
                            livekitUrl = body.livekitUrl,
                            roomName = body.roomName,
                            meetingUrl = body.meetingUrl
                        )
                    )
                } else {
                    Result.failure(Exception(body?.error ?: "Failed to join class"))
                }
            } else {
                Result.failure(Exception("Failed to join class: ${response.code()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Join live class error")
            Result.failure(e)
        }
    }

    override suspend fun toggleReminder(id: String): Result<Boolean> {
        return try {
            val response = studentLiveClassApi.toggleReminder(id)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body.reminderSet)
                } else {
                    Result.failure(Exception("Toggle reminder failed"))
                }
            } else {
                Result.failure(Exception("Toggle reminder failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Toggle reminder error")
            Result.failure(e)
        }
    }

    private fun mapDtoToDomain(dto: LiveClassDto): LiveClass {
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
}
