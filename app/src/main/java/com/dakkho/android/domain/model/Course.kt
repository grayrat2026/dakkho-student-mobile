package com.dakkho.android.domain.model

data class Course(
    val id: String,
    val title: String,
    val description: String? = null,
    val instructorId: String,
    val instructorName: String? = null,
    val technology: String? = null,
    val price: Double? = null,
    val discountedPrice: Double? = null,
    val thumbnailUrl: String? = null,
    val isPublished: Boolean = true,
    val rating: Float? = null,
    val enrollmentCount: Int? = null,
    val durationHours: Float? = null,
    val level: String? = null,
    val createdAt: String? = null
)

data class CourseDetail(
    val id: String,
    val title: String,
    val description: String? = null,
    val instructorId: String,
    val instructorName: String? = null,
    val instructorAvatar: String? = null,
    val technology: String? = null,
    val price: Double? = null,
    val discountedPrice: Double? = null,
    val thumbnailUrl: String? = null,
    val isPublished: Boolean = true,
    val rating: Float? = null,
    val reviewCount: Int? = null,
    val enrollmentCount: Int? = null,
    val durationHours: Float? = null,
    val level: String? = null,
    val whatYouLearn: List<String> = emptyList(),
    val requirements: List<String> = emptyList(),
    val targetAudience: List<String> = emptyList(),
    val createdAt: String? = null
)
