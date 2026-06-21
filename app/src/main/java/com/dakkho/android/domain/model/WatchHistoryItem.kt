package com.dakkho.android.domain.model

data class WatchHistoryItem(
    val id: String,
    val videoId: String,
    val courseId: String,
    val progressSeconds: Int = 0,
    val totalSeconds: Int = 0,
    val completed: Boolean = false,
    val lastWatchedAt: String? = null
)
