package com.dakkho.android.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// ── Phase 25: Profile Sub-pages Domain Models ──
// #65-71: EditProfile, ChangePassword, LearningStats, Subscription, Referral, Bookmarks, Settings

// ── Change Password (#66) ──

@JsonClass(generateAdapter = true)
data class ChangePasswordRequest(
    @Json(name = "current_password") val currentPassword: String,
    @Json(name = "new_password") val newPassword: String,
    @Json(name = "confirm_password") val confirmPassword: String
)

@JsonClass(generateAdapter = true)
data class ChangePasswordResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "message") val message: String?
)

// ── Learning Stats (#67) ──

data class LearningStats(
    val coursesEnrolled: Int = 0,
    val coursesCompleted: Int = 0,
    val totalHoursWatched: Float = 0f,
    val totalLessonsCompleted: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalXp: Int = 0,
    val achievementsUnlocked: Int = 0,
    val certificatesEarned: Int = 0,
    val weeklyActivity: List<DailyActivity> = emptyList(),
    val monthlyProgress: List<MonthlyProgress> = emptyList(),
    val subjectDistribution: List<SubjectDistribution> = emptyList()
)

data class DailyActivity(
    val day: String,    // "Sat", "Sun", etc. (Bangladesh week)
    val hoursWatched: Float = 0f,
    val lessonsCompleted: Int = 0
)

data class MonthlyProgress(
    val month: String,  // "Jan", "Feb", etc.
    val coursesCompleted: Int = 0,
    val hoursWatched: Float = 0f
)

data class SubjectDistribution(
    val subject: String,
    val hours: Float = 0f,
    val color: String = "#00B4D8"
)

@JsonClass(generateAdapter = true)
data class LearningStatsDto(
    @Json(name = "courses_enrolled") val coursesEnrolled: Int = 0,
    @Json(name = "courses_completed") val coursesCompleted: Int = 0,
    @Json(name = "total_hours_watched") val totalHoursWatched: Float = 0f,
    @Json(name = "total_lessons_completed") val totalLessonsCompleted: Int = 0,
    @Json(name = "current_streak") val currentStreak: Int = 0,
    @Json(name = "longest_streak") val longestStreak: Int = 0,
    @Json(name = "total_xp") val totalXp: Int = 0,
    @Json(name = "achievements_unlocked") val achievementsUnlocked: Int = 0,
    @Json(name = "certificates_earned") val certificatesEarned: Int = 0,
    @Json(name = "weekly_activity") val weeklyActivity: List<DailyActivityDto>? = null,
    @Json(name = "monthly_progress") val monthlyProgress: List<MonthlyProgressDto>? = null,
    @Json(name = "subject_distribution") val subjectDistribution: List<SubjectDistributionDto>? = null
)

@JsonClass(generateAdapter = true)
data class DailyActivityDto(
    @Json(name = "day") val day: String,
    @Json(name = "hours_watched") val hoursWatched: Float = 0f,
    @Json(name = "lessons_completed") val lessonsCompleted: Int = 0
)

@JsonClass(generateAdapter = true)
data class MonthlyProgressDto(
    @Json(name = "month") val month: String,
    @Json(name = "courses_completed") val coursesCompleted: Int = 0,
    @Json(name = "hours_watched") val hoursWatched: Float = 0f
)

@JsonClass(generateAdapter = true)
data class SubjectDistributionDto(
    @Json(name = "subject") val subject: String,
    @Json(name = "hours") val hours: Float = 0f,
    @Json(name = "color") val color: String = "#00B4D8"
)

// ── Subscription (#68) ──

data class Subscription(
    val id: String,
    val planName: String,
    val planType: SubscriptionPlanType = SubscriptionPlanType.FREE,
    val status: SubscriptionStatus = SubscriptionStatus.INACTIVE,
    val startDate: String? = null,
    val endDate: String? = null,
    val autoRenew: Boolean = false,
    val price: Double = 0.0,
    val currency: String = "BDT",
    val features: List<String> = emptyList(),
    val daysRemaining: Int = 0
)

enum class SubscriptionPlanType {
    FREE, BASIC, PRO, PREMIUM
}

enum class SubscriptionStatus {
    ACTIVE, INACTIVE, EXPIRED, CANCELLED, TRIAL
}

data class SubscriptionPlan(
    val id: String,
    val name: String,
    val planType: SubscriptionPlanType = SubscriptionPlanType.FREE,
    val price: Double = 0.0,
    val currency: String = "BDT",
    val billingCycle: String = "monthly",
    val features: List<String> = emptyList(),
    val isPopular: Boolean = false,
    val discountPercent: Int = 0
)

data class PaymentHistoryItem(
    val id: String,
    val amount: Double,
    val currency: String = "BDT",
    val status: String,
    val planName: String,
    val paidAt: String?
)

@JsonClass(generateAdapter = true)
data class SubscriptionDto(
    @Json(name = "id") val id: String,
    @Json(name = "plan_name") val planName: String,
    @Json(name = "plan_type") val planType: String = "FREE",
    @Json(name = "status") val status: String = "INACTIVE",
    @Json(name = "start_date") val startDate: String? = null,
    @Json(name = "end_date") val endDate: String? = null,
    @Json(name = "auto_renew") val autoRenew: Boolean = false,
    @Json(name = "price") val price: Double = 0.0,
    @Json(name = "currency") val currency: String = "BDT",
    @Json(name = "features") val features: List<String>? = null,
    @Json(name = "days_remaining") val daysRemaining: Int = 0
)

@JsonClass(generateAdapter = true)
data class SubscriptionPlanDto(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "plan_type") val planType: String = "FREE",
    @Json(name = "price") val price: Double = 0.0,
    @Json(name = "currency") val currency: String = "BDT",
    @Json(name = "billing_cycle") val billingCycle: String = "monthly",
    @Json(name = "features") val features: List<String>? = null,
    @Json(name = "is_popular") val isPopular: Boolean = false,
    @Json(name = "discount_percent") val discountPercent: Int = 0
)

@JsonClass(generateAdapter = true)
data class PaymentHistoryDto(
    @Json(name = "id") val id: String,
    @Json(name = "amount") val amount: Double,
    @Json(name = "currency") val currency: String = "BDT",
    @Json(name = "status") val status: String,
    @Json(name = "plan_name") val planName: String,
    @Json(name = "paid_at") val paidAt: String? = null
)

// ── Referral (#69) ──

data class ReferralData(
    val referralCode: String,
    val referralLink: String,
    val totalReferrals: Int = 0,
    val successfulReferrals: Int = 0,
    val earnedCredits: Double = 0.0,
    val pendingCredits: Double = 0.0,
    val referralHistory: List<ReferralHistoryItem> = emptyList()
)

data class ReferralHistoryItem(
    val id: String,
    val referredName: String,
    val referredEmail: String,
    val status: ReferralStatus,
    val earnedCredits: Double = 0.0,
    val date: String?
)

enum class ReferralStatus {
    PENDING, REGISTERED, ENROLLED, REWARDED
}

@JsonClass(generateAdapter = true)
data class ReferralDataDto(
    @Json(name = "referral_code") val referralCode: String,
    @Json(name = "referral_link") val referralLink: String,
    @Json(name = "total_referrals") val totalReferrals: Int = 0,
    @Json(name = "successful_referrals") val successfulReferrals: Int = 0,
    @Json(name = "earned_credits") val earnedCredits: Double = 0.0,
    @Json(name = "pending_credits") val pendingCredits: Double = 0.0,
    @Json(name = "referral_history") val referralHistory: List<ReferralHistoryItemDto>? = null
)

@JsonClass(generateAdapter = true)
data class ReferralHistoryItemDto(
    @Json(name = "id") val id: String,
    @Json(name = "referred_name") val referredName: String,
    @Json(name = "referred_email") val referredEmail: String,
    @Json(name = "status") val status: String,
    @Json(name = "earned_credits") val earnedCredits: Double = 0.0,
    @Json(name = "date") val date: String? = null
)

// ── Settings (#71) ──

data class AppSettings(
    val isDarkMode: Boolean = false,
    val isNotificationsEnabled: Boolean = true,
    val isEmailNotificationsEnabled: Boolean = true,
    val isDownloadWifiOnly: Boolean = true,
    val videoQuality: VideoQuality = VideoQuality.AUTO,
    val language: AppLanguage = AppLanguage.BN,
    val isAutoPlayNext: Boolean = true,
    val isPictureInPictureEnabled: Boolean = true,
    val isAnalyticsEnabled: Boolean = true,
    val fontSize: FontSize = FontSize.MEDIUM
)

enum class VideoQuality(val label: String, val value: String) {
    AUTO("Auto", "auto"),
    LOW("Low (360p)", "360"),
    MEDIUM("Medium (480p)", "480"),
    HIGH("High (720p)", "720"),
    ULTRA("Ultra (1080p)", "1080")
}

enum class AppLanguage(val label: String, val code: String) {
    BN("বাংলা", "bn"),
    EN("English", "en")
}

enum class FontSize(val label: String, val scale: Float) {
    SMALL("Small", 0.85f),
    MEDIUM("Medium", 1.0f),
    LARGE("Large", 1.15f)
}
