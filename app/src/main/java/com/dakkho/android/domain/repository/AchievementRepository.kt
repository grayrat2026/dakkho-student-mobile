package com.dakkho.android.domain.repository

import com.dakkho.android.domain.model.Achievement
import com.dakkho.android.domain.model.AchievementMilestone
import com.dakkho.android.domain.model.StreakData

interface AchievementRepository {

    suspend fun getAchievements(): Result<AchievementData>

    suspend fun getStreak(): Result<StreakData>
}

data class AchievementData(
    val achievements: List<Achievement>,
    val totalXp: Int,
    val unlockedCount: Int,
    val totalCount: Int,
    val milestones: List<AchievementMilestone>,
    val streak: StreakData
)
