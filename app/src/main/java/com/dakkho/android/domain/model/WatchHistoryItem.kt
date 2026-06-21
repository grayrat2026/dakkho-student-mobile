package com.dakkho.android.domain.model

data class WatchHistoryItem(
    val id: String,
    val videoId: String,
    val courseId: String,
    val videoTitle: String = "",
    val courseTitle: String = "",
    val thumbnailUrl: String? = null,
    val progressSeconds: Int = 0,
    val totalSeconds: Int = 0,
    val completed: Boolean = false,
    val lastWatchedAt: String? = null
) {
    /** Watch progress as a 0-100 percentage */
    val progressPercent: Int
        get() = if (totalSeconds > 0) {
            ((progressSeconds.toFloat() / totalSeconds) * 100).coerceIn(0f, 100f).toInt()
        } else 0
}
