package com.dakkho.android.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for Subject data within a Semester — cached for offline access.
 * Each subject belongs to a department + semester combination.
 */
@Entity(
    tableName = "subjects",
    foreignKeys = [
        ForeignKey(
            entity = SemesterEntity::class,
            parentColumns = ["id"],
            childColumns = ["semesterId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["semesterId"]),
        Index(value = ["departmentSlug", "semesterNumber"]),
        Index(value = ["courseId"]),
        Index(value = ["sortOrder"])
    ]
)
data class SubjectEntity(
    @PrimaryKey val id: String,
    val semesterId: String,
    val departmentSlug: String,
    val semesterNumber: Int,
    val name: String,
    val code: String = "",
    val creditHours: Int = 0,
    val instructorName: String? = null,
    val instructorId: String? = null,
    val courseId: String? = null,
    val description: String? = null,
    val syllabusTopics: String = "",     // JSON array stored as string
    val sortOrder: Int = 0,
    val color: String? = null,
    val isActive: Boolean = true,
    val updatedAt: Long = System.currentTimeMillis()
)
