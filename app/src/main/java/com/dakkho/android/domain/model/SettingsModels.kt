package com.dakkho.android.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// ── Phase 26: Settings Part 1 Domain Models #72-76 ──
// #72 Storage, #73 Notification Preferences, #74 Data Saver, #75 Accessibility, #76 About & Legal

// ════════════════════════════════════════════════════
// #72: Storage Management
// ════════════════════════════════════════════════════

data class StorageInfo(
    val totalStorageBytes: Long = 0L,
    val usedStorageBytes: Long = 0L,
    val freeStorageBytes: Long = 0L,
    val cacheSizeBytes: Long = 0L,
    val downloadSizeBytes: Long = 0L,
    val imageDataSizeBytes: Long = 0L,
    val databaseSizeBytes: Long = 0L,
    val otherDataSizeBytes: Long = 0L
) {
    val usedStorageMB: Float get() = usedStorageBytes / (1024f * 1024f)
    val totalStorageGB: Float get() = totalStorageBytes / (1024f * 1024f * 1024f)
    val cacheSizeMB: Float get() = cacheSizeBytes / (1024f * 1024f)
    val downloadSizeMB: Float get() = downloadSizeBytes / (1024f * 1024f)
    val imageDataSizeMB: Float get() = imageDataSizeBytes / (1024f * 1024f)
    val databaseSizeMB: Float get() = databaseSizeBytes / (1024f * 1024f)
    val otherDataSizeMB: Float get() = otherDataSizeBytes / (1024f * 1024f)
    val usagePercent: Float get() = if (totalStorageBytes > 0) (usedStorageBytes.toFloat() / totalStorageBytes) * 100f else 0f
}

data class StorageBreakdown(
    val category: StorageCategory,
    val sizeBytes: Long,
    val color: String
)

enum class StorageCategory(val label: String, val color: String) {
    CACHE("ক্যাশে", "#0EA5E9"),
    DOWNLOADS("ডাউনলোড", "#22C55E"),
    IMAGES("ছবি", "#F59E0B"),
    DATABASE("ডেটাবেস", "#8B5CF6"),
    OTHER("অন্যান্য", "#64748B")
}

// ════════════════════════════════════════════════════
// #73: Notification Preferences
// ════════════════════════════════════════════════════

data class NotificationPreferences(
    val isPushEnabled: Boolean = true,
    val isEmailEnabled: Boolean = true,
    val isCourseUpdatesEnabled: Boolean = true,
    val isLiveClassReminderEnabled: Boolean = true,
    val isAssignmentReminderEnabled: Boolean = true,
    val isDiscussionReplyEnabled: Boolean = true,
    val isPromotionalEnabled: Boolean = false,
    val isAchievementNotificationEnabled: Boolean = true,
    val quietHoursEnabled: Boolean = false,
    val quietHoursStart: String = "22:00",
    val quietHoursEnd: String = "07:00",
    val notificationSound: NotificationSound = NotificationSound.DEFAULT,
    val isVibrationEnabled: Boolean = true,
    val isLedEnabled: Boolean = true
)

enum class NotificationSound(val label: String, val value: String) {
    DEFAULT("ডিফল্ট", "default"),
    SILENT("নীরব", "silent"),
    GENTLE("মৃদু", "gentle"),
    BRIGHT("উজ্জ্বল", "bright"),
    URGENT("জরুরি", "urgent")
}

// ════════════════════════════════════════════════════
// #74: Data Saver Mode
// ════════════════════════════════════════════════════

data class DataSaverConfig(
    val isDataSaverEnabled: Boolean = false,
    val mobileVideoQuality: MobileVideoQuality = MobileVideoQuality.LOW,
    val isAutoplayOnMobileEnabled: Boolean = false,
    val isImageCompressionEnabled: Boolean = true,
    val isBackgroundDataEnabled: Boolean = true,
    val isDownloadOnMobileEnabled: Boolean = false,
    val imageDataQuality: ImageDataQuality = ImageDataQuality.MEDIUM,
    val monthlyDataLimitMB: Int = 0,  // 0 = unlimited
    val dataUsedThisMonthMB: Float = 0f
)

enum class MobileVideoQuality(val label: String, val value: String) {
    LOW("কম (240p)", "240"),
    MEDIUM("মাঝারি (360p)", "360"),
    HIGH("উচ্চ (480p)", "480")
}

enum class ImageDataQuality(val label: String, val scale: Float) {
    LOW("কম", 0.5f),
    MEDIUM("মাঝারি", 0.75f),
    HIGH("উচ্চ", 1.0f)
}

// ════════════════════════════════════════════════════
// #75: Accessibility Settings
// ════════════════════════════════════════════════════

data class AccessibilitySettings(
    val isHighContrastEnabled: Boolean = false,
    val isReduceMotionEnabled: Boolean = false,
    val isScreenReaderOptimized: Boolean = false,
    val contentScale: ContentScale = ContentScale.DEFAULT,
    val isColorInversionEnabled: Boolean = false,
    val isLargePointerEnabled: Boolean = false,
    val isBoldTextEnabled: Boolean = false,
    val isSpacingAdjustmentEnabled: Boolean = false,
    val minTouchTargetSize: TouchTargetSize = TouchTargetSize.DEFAULT
)

enum class ContentScale(val label: String, val scale: Float) {
    DEFAULT("ডিফল্ট", 1.0f),
    LARGE("বড়", 1.25f),
    EXTRA_LARGE("অতিরিক্ত বড়", 1.5f)
}

enum class TouchTargetSize(val label: String, val dp: Int) {
    DEFAULT("ডিফল্ট (48dp)", 48),
    LARGE("বড় (56dp)", 56),
    EXTRA_LARGE("অতিরিক্ত বড় (64dp)", 64)
}

// ════════════════════════════════════════════════════
// #76: About & Legal
// ════════════════════════════════════════════════════

data class AboutAppInfo(
    val appName: String = "DAKKHO",
    val appVersion: String = "1.0.0",
    val buildNumber: Int = 1,
    val buildDate: String = "",
    val minAndroidVersion: String = "7.0 (API 24)",
    val targetSdkVersion: String = "35 (API 35)",
    val developerName: String = "DAKKHO Team",
    val developerEmail: String = "support@dakkho.com.bd",
    val websiteUrl: String = "https://dakkho.com.bd",
    val privacyPolicyUrl: String = "https://dakkho.com.bd/privacy",
    val termsOfServiceUrl: String = "https://dakkho.com.bd/terms",
    val licensesUrl: String = "https://dakkho.com.bd/licenses",
    val playStoreUrl: String = "https://play.google.com/store/apps/details?id=com.dakkho.android"
)

@JsonClass(generateAdapter = true)
data class AboutAppInfoDto(
    @Json(name = "app_name") val appName: String = "DAKKHO",
    @Json(name = "app_version") val appVersion: String = "1.0.0",
    @Json(name = "build_number") val buildNumber: Int = 1,
    @Json(name = "build_date") val buildDate: String = "",
    @Json(name = "min_android_version") val minAndroidVersion: String = "7.0 (API 24)",
    @Json(name = "target_sdk_version") val targetSdkVersion: String = "35 (API 35)",
    @Json(name = "developer_name") val developerName: String = "DAKKHO Team",
    @Json(name = "developer_email") val developerEmail: String = "support@dakkho.com.bd",
    @Json(name = "website_url") val websiteUrl: String = "https://dakkho.com.bd",
    @Json(name = "privacy_policy_url") val privacyPolicyUrl: String = "https://dakkho.com.bd/privacy",
    @Json(name = "terms_of_service_url") val termsOfServiceUrl: String = "https://dakkho.com.bd/terms",
    @Json(name = "licenses_url") val licensesUrl: String = "https://dakkho.com.bd/licenses",
    @Json(name = "play_store_url") val playStoreUrl: String = "https://play.google.com/store/apps/details?id=com.dakkho.android"
)
