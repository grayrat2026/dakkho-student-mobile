package com.dakkho.android.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Domain model for a student achievement badge.
 */
data class Achievement(
    val id: Int,
    val slug: String = "",
    val title: String,
    val nameBn: String? = null,
    val description: String? = null,
    val descriptionBn: String? = null,
    val category: String = "learning",
    val icon: String = "trophy",
    val xp: Int = 0,
    val conditionType: String? = null,
    val conditionValue: String? = null,
    val isUnlocked: Boolean = false,
    val unlockedAt: String? = null,
    val iconUrl: String? = null,
    val earnedAt: String? = null
) {
    /** Category display name for UI */
    val categoryDisplayName: String
        get() = when (category) {
            "learning" -> "Learning"
            "social" -> "Social"
            "streak" -> "Streak"
            "mastery" -> "Mastery"
            "special" -> "Special"
            else -> category.replaceFirstChar { it.uppercase() }
        }

    /** Whether this achievement was recently unlocked (within last 24h) */
    val isRecentlyUnlocked: Boolean
        get() = isUnlocked && unlockedAt != null && run {
            try {
                val unlockedMs = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
                    .parse(unlockedAt)?.time ?: 0L
                System.currentTimeMillis() - unlockedMs < 86_400_000
            } catch (_: Exception) { false }
        }
}

/**
 * DTO for achievement from API.
 */
@JsonClass(generateAdapter = true)
data class AchievementDto(
    @Json(name = "id") val id: Int,
    @Json(name = "slug") val slug: String = "",
    @Json(name = "name") val name: String = "",
    @Json(name = "nameBn") val nameBn: String? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "descriptionBn") val descriptionBn: String? = null,
    @Json(name = "category") val category: String = "learning",
    @Json(name = "icon") val icon: String = "trophy",
    @Json(name = "xp") val xp: Int = 0,
    @Json(name = "conditionType") val conditionType: String? = null,
    @Json(name = "unlocked") val unlocked: Boolean = false,
    @Json(name = "unlockedAt") val unlockedAt: String? = null
)

/**
 * Response for achievements list.
 */
data class AchievementListResponse(
    val achievements: List<AchievementDto> = emptyList(),
    val totalXp: Int = 0,
    val unlockedCount: Int = 0,
    val totalCount: Int = 0
)

/**
 * Milestone progress for achievements.
 */
data class AchievementMilestone(
    val label: String,
    val target: Int,
    val current: Int,
    val percentage: Float
) {
    val isCompleted: Boolean get() = current >= target
}

/**
 * Streak data for the student.
 */
data class StreakData(
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastActiveDate: String? = null,
    val isActiveToday: Boolean = false
)
