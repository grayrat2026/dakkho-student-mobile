package com.dakkho.android.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for caching department/technology data.
 * Departments are 100% dynamic — populated from the API only.
 * No default departments are pre-seeded.
 */
@Entity(
    tableName = "departments",
    indices = [
        Index(value = ["slug"], unique = true),
        Index(value = ["shortCode"]),
        Index(value = ["courseCount"])
    ]
)
data class DepartmentEntity(
    @PrimaryKey val id: String,
    val slug: String,
    val name: String,
    val shortCode: String,
    val description: String? = null,
    val iconUrl: String? = null,
    val bannerUrl: String? = null,
    val courseCount: Int = 0,
    val instructorCount: Int = 0,
    val studentCount: Int = 0,
    val semesterCount: Int = 8,
    val isActive: Boolean = true,
    val updatedAt: Long = System.currentTimeMillis()
)
