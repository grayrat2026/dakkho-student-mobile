package com.dakkho.android.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// ── Phase 27: Settings Part 2 Domain Models #77-82 ──
// #77 Theme Settings, #78 Download Settings, #79 Video Quality,
// #80 Network & Data, #81 Content Protection, #82 Active Sessions

// ════════════════════════════════════════════════════
// #77: Theme Settings
// ════════════════════════════════════════════════════

enum class ThemeMode(val label: String, val value: String) {
    LIGHT("লাইট", "light"),
    DARK("ডার্ক", "dark"),
    SYSTEM("সিস্টেম", "system");

    companion object {
        fun fromValue(value: String): ThemeMode =
            entries.find { it.value == value } ?: SYSTEM
    }
}

enum class AccentColor(val label: String, val hex: String, val value: String) {
    SKY_BLUE("আকাশি নীল", "#0EA5E9", "sky_blue"),
    GREEN("সবুজ", "#22C55E", "green"),
    PURPLE("বেগুনি", "#8B5CF6", "purple"),
    ORANGE("কমলা", "#F97316", "orange"),
    RED("লাল", "#EF4444", "red");

    companion object {
        fun fromValue(value: String): AccentColor =
            entries.find { it.value == value } ?: SKY_BLUE
    }
}

data class ThemeSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val accentColor: AccentColor = AccentColor.SKY_BLUE,
    val isDynamicColorEnabled: Boolean = false  // Material You on Android 12+
)

@JsonClass(generateAdapter = true)
data class ThemeSettingsDto(
    @Json(name = "theme_mode") val themeMode: String = "system",
    @Json(name = "accent_color") val accentColor: String = "sky_blue",
    @Json(name = "is_dynamic_color_enabled") val isDynamicColorEnabled: Boolean = false
) {
    fun toDomain(): ThemeSettings = ThemeSettings(
        themeMode = ThemeMode.fromValue(themeMode),
        accentColor = AccentColor.fromValue(accentColor),
        isDynamicColorEnabled = isDynamicColorEnabled
    )
}

// ════════════════════════════════════════════════════
// #78: Download Settings
// ════════════════════════════════════════════════════

enum class DownloadQuality(val label: String, val value: String) {
    QUALITY_720P("720p", "720"),
    QUALITY_1080P("1080p (HD)", "1080");

    companion object {
        fun fromValue(value: String): DownloadQuality =
            entries.find { it.value == value } ?: QUALITY_720P
    }
}

data class DownloadSettings(
    val downloadQuality: DownloadQuality = DownloadQuality.QUALITY_720P,
    val isAutoDownloadOnWifi: Boolean = false,
    val storagePathUri: String? = null,       // SAF directory URI
    val storagePathLabel: String? = null,     // Human-readable path
    val availableStorageBytes: Long = 0L,
    val usedByDownloadsBytes: Long = 0L,
    val usedByCacheBytes: Long = 0L,
    val usedByOtherBytes: Long = 0L
) {
    val availableStorageGB: Float get() = availableStorageBytes / (1024f * 1024f * 1024f)
    val usedByDownloadsGB: Float get() = usedByDownloadsBytes / (1024f * 1024f * 1024f)
    val usedByCacheGB: Float get() = usedByCacheBytes / (1024f * 1024f * 1024f)
    val usedByOtherGB: Float get() = usedByOtherBytes / (1024f * 1024f * 1024f)
    val totalUsedGB: Float get() = usedByDownloadsGB + usedByCacheGB + usedByOtherGB
}

@JsonClass(generateAdapter = true)
data class DownloadSettingsDto(
    @Json(name = "download_quality") val downloadQuality: String = "720",
    @Json(name = "auto_download_wifi") val isAutoDownloadOnWifi: Boolean = false,
    @Json(name = "storage_path_uri") val storagePathUri: String? = null,
    @Json(name = "storage_path_label") val storagePathLabel: String? = null,
    @Json(name = "available_storage_bytes") val availableStorageBytes: Long = 0L,
    @Json(name = "used_by_downloads_bytes") val usedByDownloadsBytes: Long = 0L,
    @Json(name = "used_by_cache_bytes") val usedByCacheBytes: Long = 0L,
    @Json(name = "used_by_other_bytes") val usedByOtherBytes: Long = 0L
) {
    fun toDomain(): DownloadSettings = DownloadSettings(
        downloadQuality = DownloadQuality.fromValue(downloadQuality),
        isAutoDownloadOnWifi = isAutoDownloadOnWifi,
        storagePathUri = storagePathUri,
        storagePathLabel = storagePathLabel,
        availableStorageBytes = availableStorageBytes,
        usedByDownloadsBytes = usedByDownloadsBytes,
        usedByCacheBytes = usedByCacheBytes,
        usedByOtherBytes = usedByOtherBytes
    )
}

// ════════════════════════════════════════════════════
// #79: Video Quality Settings
// ════════════════════════════════════════════════════

enum class StreamingQuality(val label: String, val value: String, val maxBitrate: Int) {
    AUTO("স্বয়ংক্রিয়", "auto", Int.MAX_VALUE),
    QUALITY_720P("720p", "720", 2_500_000),
    QUALITY_1080P("1080p (HD)", "1080", 5_000_000);

    companion object {
        fun fromValue(value: String): StreamingQuality =
            entries.find { it.value == value } ?: AUTO
    }
}

data class VideoQualitySettings(
    val streamingQuality: StreamingQuality = StreamingQuality.AUTO,
    val isBandwidthSaverEnabled: Boolean = false
)

@JsonClass(generateAdapter = true)
data class VideoQualitySettingsDto(
    @Json(name = "streaming_quality") val streamingQuality: String = "auto",
    @Json(name = "bandwidth_saver") val isBandwidthSaverEnabled: Boolean = false
) {
    fun toDomain(): VideoQualitySettings = VideoQualitySettings(
        streamingQuality = StreamingQuality.fromValue(streamingQuality),
        isBandwidthSaverEnabled = isBandwidthSaverEnabled
    )
}

// ════════════════════════════════════════════════════
// #80: Network & Data Settings
// ════════════════════════════════════════════════════

data class NetworkDataConfig(
    val isWifiOnlyEnabled: Boolean = false,
    val isDataSaverModeEnabled: Boolean = false,
    val bandwidthLimitMB: Int = 0,     // 0 = unlimited
    val dataUsedThisMonthMB: Float = 0f,
    val isOnMeteredNetwork: Boolean = false,
    val isWifiConnected: Boolean = false
) {
    val dataUsagePercent: Float get() =
        if (bandwidthLimitMB > 0) (dataUsedThisMonthMB / bandwidthLimitMB) * 100f else 0f
    val isBandwidthLimitSet: Boolean get() = bandwidthLimitMB > 0
}

@JsonClass(generateAdapter = true)
data class NetworkDataConfigDto(
    @Json(name = "wifi_only") val isWifiOnlyEnabled: Boolean = false,
    @Json(name = "data_saver_mode") val isDataSaverModeEnabled: Boolean = false,
    @Json(name = "bandwidth_limit_mb") val bandwidthLimitMB: Int = 0,
    @Json(name = "data_used_this_month_mb") val dataUsedThisMonthMB: Float = 0f
) {
    fun toDomain(): NetworkDataConfig = NetworkDataConfig(
        isWifiOnlyEnabled = isWifiOnlyEnabled,
        isDataSaverModeEnabled = isDataSaverModeEnabled,
        bandwidthLimitMB = bandwidthLimitMB,
        dataUsedThisMonthMB = dataUsedThisMonthMB
    )
}

// ════════════════════════════════════════════════════
// #81: Content Protection
// ════════════════════════════════════════════════════

data class ContentProtectionConfig(
    val isScreenshotBlockEnabled: Boolean = true,
    val isDownloadRestrictionEnabled: Boolean = true,
    val isSecurePlayerModeEnabled: Boolean = true,
    val isFlagSecureEnabled: Boolean = true,
    val hasDeviceAdminWarning: Boolean = false
)

@JsonClass(generateAdapter = true)
data class ContentProtectionConfigDto(
    @Json(name = "screenshot_block") val isScreenshotBlockEnabled: Boolean = true,
    @Json(name = "download_restriction") val isDownloadRestrictionEnabled: Boolean = true,
    @Json(name = "secure_player_mode") val isSecurePlayerModeEnabled: Boolean = true,
    @Json(name = "flag_secure") val isFlagSecureEnabled: Boolean = true,
    @Json(name = "device_admin_warning") val hasDeviceAdminWarning: Boolean = false
) {
    fun toDomain(): ContentProtectionConfig = ContentProtectionConfig(
        isScreenshotBlockEnabled = isScreenshotBlockEnabled,
        isDownloadRestrictionEnabled = isDownloadRestrictionEnabled,
        isSecurePlayerModeEnabled = isSecurePlayerModeEnabled,
        isFlagSecureEnabled = isFlagSecureEnabled,
        hasDeviceAdminWarning = hasDeviceAdminWarning
    )
}

// ════════════════════════════════════════════════════
// #82: Active Sessions
// ════════════════════════════════════════════════════

data class ActiveSession(
    val id: String,
    val deviceModel: String,
    val osVersion: String,
    val appVersion: String,
    val lastActiveTime: String,
    val lastIpAddress: String,
    val isActive: Boolean = true,
    val isCurrentDevice: Boolean = false
) {
    val displayModel: String get() = "$deviceModel (Android $osVersion)"
}

@JsonClass(generateAdapter = true)
data class ActiveSessionDto(
    @Json(name = "id") val id: String,
    @Json(name = "device_model") val deviceModel: String,
    @Json(name = "os_version") val osVersion: String,
    @Json(name = "app_version") val appVersion: String,
    @Json(name = "last_active") val lastActiveTime: String,
    @Json(name = "last_ip") val lastIpAddress: String,
    @Json(name = "is_active") val isActive: Boolean = true,
    @Json(name = "is_current") val isCurrentDevice: Boolean = false
) {
    fun toDomain(): ActiveSession = ActiveSession(
        id = id,
        deviceModel = deviceModel,
        osVersion = osVersion,
        appVersion = appVersion,
        lastActiveTime = lastActiveTime,
        lastIpAddress = lastIpAddress,
        isActive = isActive,
        isCurrentDevice = isCurrentDevice
    )
}
