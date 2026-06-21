package com.dakkho.android.domain.model

/**
 * Domain model for a course note.
 */
data class CourseNote(
    val id: Long = 0,
    val videoId: String,
    val courseId: String,
    val userId: String,
    val positionMs: Long = 0,
    val content: String,
    val videoTitle: String? = null,
    val timestampLabel: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
