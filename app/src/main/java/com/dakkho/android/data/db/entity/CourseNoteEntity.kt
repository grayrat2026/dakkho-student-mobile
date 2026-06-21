package com.dakkho.android.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "course_notes",
    indices = [
        Index(value = ["video_id", "course_id"], name = "index_course_notes_video_course"),
        Index(value = ["user_id"], name = "index_course_notes_user_id"),
        Index(value = ["course_id", "user_id"], name = "index_course_notes_course_user"),
        Index(value = ["created_at"], name = "index_course_notes_created_at")
    ]
)
data class CourseNoteEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "video_id") val videoId: String,
    @ColumnInfo(name = "course_id") val courseId: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "position_ms") val positionMs: Long = 0,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "video_title") val videoTitle: String? = null,
    @ColumnInfo(name = "timestamp_label") val timestampLabel: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at") val updatedAt: Long = System.currentTimeMillis()
)
