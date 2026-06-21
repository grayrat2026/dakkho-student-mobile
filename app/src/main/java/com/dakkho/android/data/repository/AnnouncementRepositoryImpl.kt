package com.dakkho.android.data.repository

import com.dakkho.android.data.api.AnnouncementApiService
import com.dakkho.android.domain.model.Announcement
import com.dakkho.android.domain.repository.AnnouncementRepository
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnnouncementRepositoryImpl @Inject constructor(
    private val announcementApiService: AnnouncementApiService
) : AnnouncementRepository {

    override suspend fun getAnnouncements(
        courseId: String,
        page: Int,
        limit: Int
    ): Result<List<Announcement>> {
        return try {
            val response = announcementApiService.getAnnouncements(courseId, page, limit)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val announcements = body.documents.map { dto ->
                        Announcement(
                            id = dto.id,
                            courseId = dto.courseId,
                            title = dto.title,
                            body = dto.body,
                            type = dto.type,
                            isPinned = dto.isPinned,
                            instructorName = dto.instructorName,
                            createdAt = dto.createdAt,
                            updatedAt = dto.updatedAt
                        )
                    }
                    Result.success(announcements)
                } else {
                    Result.success(emptyList())
                }
            } else {
                Result.success(emptyList())
            }
        } catch (e: Exception) {
            Timber.e(e, "Get announcements error")
            Result.failure(e)
        }
    }

    override suspend fun getAnnouncementDetail(announcementId: String): Result<Announcement> {
        return try {
            val response = announcementApiService.getAnnouncementDetail(announcementId)
            if (response.isSuccessful) {
                val dto = response.body()
                if (dto != null) {
                    Result.success(
                        Announcement(
                            id = dto.id,
                            courseId = dto.courseId,
                            title = dto.title,
                            body = dto.body,
                            type = dto.type,
                            isPinned = dto.isPinned,
                            instructorName = dto.instructorName,
                            createdAt = dto.createdAt,
                            updatedAt = dto.updatedAt
                        )
                    )
                } else {
                    Result.failure(Exception("Announcement not found"))
                }
            } else {
                Result.failure(Exception("Failed to load announcement: ${response.code()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Get announcement detail error")
            Result.failure(e)
        }
    }
}
