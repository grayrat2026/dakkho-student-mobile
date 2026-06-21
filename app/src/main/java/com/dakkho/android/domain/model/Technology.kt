package com.dakkho.android.domain.model

/**
 * Domain model for Technology / Department.
 * Fully dynamic — no hardcoded departments exist.
 * Admin and Instructor add departments, and only those appear in the Student app.
 */
data class Technology(
    val id: String,
    val name: String,
    val slug: String = "",
    val shortCode: String = "",
    val description: String? = null,
    val iconUrl: String? = null,
    val bannerUrl: String? = null,
    val courseCount: Int = 0,
    val instructorCount: Int = 0,
    val studentCount: Int = 0,
    val semesterCount: Int = 8,
    val isActive: Boolean = true
)
