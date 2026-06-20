package com.dakkho.android.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "courses",
    indices = [
        Index(value = ["title"], name = "index_courses_title"),
        Index(value = ["technology"], name = "index_courses_technology"),
        Index(value = ["instructor_id"], name = "index_courses_instructor_id")
    ]
)
data class CourseEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "instructor_id") val instructorId: String,
    @ColumnInfo(name = "instructor_name") val instructorName: String?,
    @ColumnInfo(name = "technology") val technology: String?,
    @ColumnInfo(name = "price") val price: Double?,
    @ColumnInfo(name = "discounted_price") val discountedPrice: Double?,
    @ColumnInfo(name = "thumbnail_url") val thumbnailUrl: String?,
    @ColumnInfo(name = "is_published") val isPublished: Boolean = true,
    @ColumnInfo(name = "rating") val rating: Float?,
    @ColumnInfo(name = "enrollment_count") val enrollmentCount: Int?,
    @ColumnInfo(name = "duration_hours") val durationHours: Float?,
    @ColumnInfo(name = "level") val level: String?,
    @ColumnInfo(name = "created_at") val createdAt: String?,
    @ColumnInfo(name = "cached_at") val cachedAt: Long = System.currentTimeMillis()
)
