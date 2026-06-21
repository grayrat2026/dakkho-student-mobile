package com.dakkho.android.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "downloads",
    indices = [
        Index(value = ["course_id"], name = "index_downloads_course_id"),
        Index(value = ["video_id"], name = "index_downloads_video_id"),
        Index(value = ["status"], name = "index_downloads_status")
    ]
)
data class DownloadEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "video_id") val videoId: String,
    @ColumnInfo(name = "course_id") val courseId: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "file_path") val filePath: String?,
    @ColumnInfo(name = "file_size_bytes") val fileSizeBytes: Long = 0,
    @ColumnInfo(name = "downloaded_bytes") val downloadedBytes: Long = 0,
    @ColumnInfo(name = "status") val status: String = "pending",
    @ColumnInfo(name = "thumbnail_url") val thumbnailUrl: String?,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "completed_at") val completedAt: Long?
)
