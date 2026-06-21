package com.dakkho.android.data.repository

import com.dakkho.android.data.api.AchievementApiService
import com.dakkho.android.domain.model.Achievement
import com.dakkho.android.domain.model.AchievementData
import com.dakkho.android.domain.model.AchievementMilestone
import com.dakkho.android.domain.model.StreakData
import com.dakkho.android.domain.repository.AchievementRepository
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AchievementRepositoryImpl @Inject constructor(
    private val achievementApi: AchievementApiService
) : AchievementRepository {

    override suspend fun getAchievements(): Result<AchievementData> {
        return try {
            val response = achievementApi.getAchievements()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val achievements = body.achievements.map { dto ->
                        Achievement(
                            id = dto.id,
                            slug = dto.slug,
                            title = dto.name,
                            nameBn = dto.nameBn,
                            description = dto.description,
                            descriptionBn = dto.descriptionBn,
                            category = dto.category,
                            icon = dto.icon,
                            xp = dto.xp,
                            conditionType = dto.conditionType,
                            isUnlocked = dto.unlocked,
                            unlockedAt = dto.unlockedAt
                        )
                    }

                    // Generate milestones based on achievement progress
                    val totalCount = achievements.size
                    val unlockedCount = body.unlockedCount
                    val milestones = generateMilestones(totalCount, unlockedCount)

                    Result.success(
                        AchievementData(
                            achievements = achievements,
                            totalXp = body.totalXp,
                            unlockedCount = unlockedCount,
                            totalCount = totalCount,
                            milestones = milestones,
                            streak = StreakData() // Default, streak data comes from activity endpoint
                        )
                    )
                } else {
                    Result.failure(Exception("No achievement data"))
                }
            } else {
                Result.failure(Exception("Failed to load achievements: ${response.code()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Get achievements error")
            Result.failure(e)
        }
    }

    override suspend fun getStreak(): Result<StreakData> {
        // Streak data would come from /api/student/activity
        // For now, return default data
        return Result.success(StreakData())
    }

    private fun generateMilestones(total: Int, unlocked: Int): List<AchievementMilestone> {
        return listOf(
            AchievementMilestone("First Step", 1, unlocked, if (total > 0) unlocked.toFloat() / total * 100 else 0f),
            AchievementMilestone("Halfway", total / 2, unlocked, if (total > 0) unlocked.toFloat() / total * 100 else 0f),
            AchievementMilestone("Almost There", (total * 0.75).toInt(), unlocked, if (total > 0) unlocked.toFloat() / total * 100 else 0f),
            AchievementMilestone("Completionist", total, unlocked, if (total > 0) unlocked.toFloat() / total * 100 else 0f)
        )
    }
}