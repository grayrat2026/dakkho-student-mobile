package com.dakkho.android.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for Semester data — cached for offline access.
 * 7 regular semesters + 8th = ইন্টার্নি (Internship).
 */
@Entity(
    tableName = "semesters",
    foreignKeys = [
        ForeignKey(
            entity = DepartmentEntity::class,
            parentColumns = ["slug"],
            childColumns = ["departmentSlug"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["departmentSlug"]),
        Index(value = ["departmentSlug", "number"], unique = true)
    ]
)
data class SemesterEntity(
    @PrimaryKey val id: String,
    val departmentSlug: String,
    val number: Int,               // 1–8 (8 = internship)
    val name: String,              // "Semester 1" … "Semester 7", "ইন্টার্নি"
    val subjectCount: Int = 0,
    val totalCredits: Int = 0,
    val isActive: Boolean = true,
    val updatedAt: Long = System.currentTimeMillis()
)
