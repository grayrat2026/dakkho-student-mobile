package com.dakkho.android.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "course_details",
    indices = [
        Index(value = ["course_id"], unique = true, name = "index_course_details_course_id")
    ]
)
data class CourseDetailEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "course_id") val courseId: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "instructor_id") val instructorId: String,
    @ColumnInfo(name = "instructor_name") val instructorName: String?,
    @ColumnInfo(name = "instructor_avatar") val instructorAvatar: String?,
    @ColumnInfo(name = "technology") val technology: String?,
    @ColumnInfo(name = "price") val price: Double?,
    @ColumnInfo(name = "discounted_price") val discountedPrice: Double?,
    @ColumnInfo(name = "thumbnail_url") val thumbnailUrl: String?,
    @ColumnInfo(name = "is_published") val isPublished: Boolean = true,
    @ColumnInfo(name = "rating") val rating: Float?,
    @ColumnInfo(name = "review_count") val reviewCount: Int?,
    @ColumnInfo(name = "enrollment_count") val enrollmentCount: Int?,
    @ColumnInfo(name = "duration_hours") val durationHours: Float?,
    @ColumnInfo(name = "level") val level: String?,
    @ColumnInfo(name = "what_you_learn") val whatYouLearn: String?, // JSON list
    @ColumnInfo(name = "requirements") val requirements: String?, // JSON list
    @ColumnInfo(name = "target_audience") val targetAudience: String?, // JSON list
    @ColumnInfo(name = "created_at") val createdAt: String?,
    @ColumnInfo(name = "cached_at") val cachedAt: Long = System.currentTimeMillis()
)
