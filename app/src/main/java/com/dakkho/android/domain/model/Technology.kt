package com.dakkho.android.domain.model

/**
 * Domain model for Technology / Department.
 * Fully dynamic — no hardcoded departments exist.
 * Admin and Instructor add departments, and only those appear in the Student app.
 *
 * Phase 24: semesterCount represents the total semesters including internship.
 * Default = 8 (7 regular semesters + 8th = ইন্টার্নি/Internship).
 * The 8th semester chip always shows "ইন্টার্নি" instead of "Sem 8".
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
    val semesterCount: Int = 8,       // 7 regular + 1 internship = 8
    val isActive: Boolean = true
)
