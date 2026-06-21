package com.dakkho.android.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Domain model for a live class session.
 */
data class LiveClass(
    val id: String,
    val title: String,
    val description: String? = null,
    val instructorId: String,
    val instructorName: String? = null,
    val courseId: String? = null,
    val courseName: String? = null,
    val scheduledAt: String? = null,
    val startedAt: String? = null,
    val endedAt: String? = null,
    val durationMinutes: Int = 60,
    val meetingUrl: String? = null,
    val thumbnailUrl: String? = null,
    val status: LiveClassStatus = LiveClassStatus.SCHEDULED,
    val isRecorded: Boolean = false,
    val recordingUrl: String? = null
)

enum class LiveClassStatus {
    SCHEDULED,
    LIVE,
    ENDED,
    CANCELLED;

    val displayName: String
        get() = when (this) {
            SCHEDULED -> "Upcoming"
            LIVE -> "Live Now"
            ENDED -> "Ended"
            CANCELLED -> "Cancelled"
        }
}

/**
 * DTO for live class from API.
 */
@JsonClass(generateAdapter = true)
data class LiveClassDto(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String?,
    @Json(name = "instructor_id") val instructorId: String,
    @Json(name = "instructor_name") val instructorName: String?,
    @Json(name = "course_id") val courseId: String?,
    @Json(name = "course_name") val courseName: String?,
    @Json(name = "scheduled_at") val scheduledAt: String?,
    @Json(name = "started_at") val startedAt: String?,
    @Json(name = "ended_at") val endedAt: String?,
    @Json(name = "duration_minutes") val durationMinutes: Int?,
    @Json(name = "meeting_url") val meetingUrl: String?,
    @Json(name = "thumbnail_url") val thumbnailUrl: String?,
    @Json(name = "status") val status: String?,
    @Json(name = "is_recorded") val isRecorded: Boolean?,
    @Json(name = "recording_url") val recordingUrl: String?
)
