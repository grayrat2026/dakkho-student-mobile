package com.dakkho.android.domain.model

/**
 * Domain model for a course announcement.
 */
data class Announcement(
    val id: String,
    val courseId: String,
    val title: String,
    val body: String,
    val type: String = "info", // info, warning, update, urgent
    val isPinned: Boolean = false,
    val isRead: Boolean = false,
    val instructorName: String? = null,
    val createdAt: String,
    val updatedAt: String? = null
)

/**
 * DTO for an announcement from the API.
 */
data class AnnouncementDto(
    val id: String = "",
    val courseId: String = "",
    val title: String = "",
    val body: String = "",
    val type: String = "info",
    val isPinned: Boolean = false,
    val instructorName: String? = null,
    val createdAt: String = "",
    val updatedAt: String? = null
)

/**
 * Response for listing announcements.
 */
data class AnnouncementListResponse(
    val documents: List<AnnouncementDto> = emptyList(),
    val total: Int = 0
)
