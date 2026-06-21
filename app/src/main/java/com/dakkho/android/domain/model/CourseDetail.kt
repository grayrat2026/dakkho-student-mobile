package com.dakkho.android.domain.model

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
    val enrollmentCount: Int? = null,
    val durationHours: Float? = null,
    val level: String? = null,
    val whatYouLearn: List<String> = emptyList(),
    val requirements: List<String> = emptyList(),
    val curriculum: Curriculum? = null,
    val createdAt: String? = null
)

data class Curriculum(
    val sections: List<Section> = emptyList()
)

data class Section(
    val id: String,
    val title: String,
    val order: Int = 0,
    val lessons: List<Lesson> = emptyList()
)

data class Lesson(
    val id: String,
    val title: String,
    val type: String? = null,
    val durationSeconds: Int? = null,
    val isFree: Boolean = false,
    val order: Int = 0,
    val videoUrl: String? = null
)
