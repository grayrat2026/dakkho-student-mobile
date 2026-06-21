package com.dakkho.android.domain.model

data class Enrollment(
    val id: String,
    val userId: String,
    val courseId: String,
    val progress: Float = 0f,
    val isCompleted: Boolean = false,
    val enrolledAt: String? = null,
    val completedAt: String? = null
)
