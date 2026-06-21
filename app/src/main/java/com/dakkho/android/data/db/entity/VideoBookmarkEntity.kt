package com.dakkho.android.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "video_bookmarks",
    indices = [
        Index(value = ["video_id", "course_id"], name = "index_video_bookmarks_video_course"),
        Index(value = ["user_id"], name = "index_video_bookmarks_user_id"),
        Index(value = ["created_at"], name = "index_video_bookmarks_created_at")
    ]
)
data class VideoBookmarkEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "video_id") val videoId: String,
    @ColumnInfo(name = "course_id") val courseId: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "position_ms") val positionMs: Long,
    @ColumnInfo(name = "note") val note: String? = null,
    @ColumnInfo(name = "video_title") val videoTitle: String? = null,
    @ColumnInfo(name = "course_title") val courseTitle: String? = null,
    @ColumnInfo(name = "thumbnail_url") val thumbnailUrl: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)
