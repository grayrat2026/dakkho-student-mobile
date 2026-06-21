package com.dakkho.android.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "watch_history",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["user_id"], name = "index_watch_history_user_id"),
        Index(value = ["video_id"], name = "index_watch_history_video_id"),
        Index(value = ["last_watched_at"], name = "index_watch_history_last_watched")
    ]
)
data class WatchHistoryEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "video_id") val videoId: String,
    @ColumnInfo(name = "course_id") val courseId: String,
    @ColumnInfo(name = "video_title") val videoTitle: String = "",
    @ColumnInfo(name = "course_title") val courseTitle: String = "",
    @ColumnInfo(name = "thumbnail_url") val thumbnailUrl: String? = null,
    @ColumnInfo(name = "progress_seconds") val progressSeconds: Int = 0,
    @ColumnInfo(name = "total_seconds") val totalSeconds: Int = 0,
    @ColumnInfo(name = "completed") val completed: Boolean = false,
    @ColumnInfo(name = "last_watched_at") val lastWatchedAt: String?,
    @ColumnInfo(name = "cached_at") val cachedAt: Long = System.currentTimeMillis()
)
