package com.dakkho.android.domain.model

data class Review(
    val id: String,
    val userId: String,
    val courseId: String,
    val userName: String? = null,
    val userAvatar: String? = null,
    val rating: Float,
    val comment: String? = null,
    val createdAt: String? = null
)

data class CoursePackage(
    val id: String,
    val name: String,
    val description: String? = null,
    val price: Double? = null,
    val isFree: Boolean = false,
    val features: List<String> = emptyList(),
    val courseIds: List<String> = emptyList(),
    val isActive: Boolean = true
)
