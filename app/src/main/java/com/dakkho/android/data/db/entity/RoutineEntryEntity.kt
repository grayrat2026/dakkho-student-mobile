package com.dakkho.android.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for Routine entries — weekly timetable.
 * Each entry is a single time slot for a subject on a specific day.
 */
@Entity(
    tableName = "routine_entries",
    foreignKeys = [
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["subjectId"]),
        Index(value = ["departmentSlug", "semesterNumber", "dayOfWeek"]),
        Index(value = ["dayOfWeek", "startTime"])
    ]
)
data class RoutineEntryEntity(
    @PrimaryKey val id: String,
    val subjectId: String,
    val subjectName: String,
    val subjectCode: String,
    val departmentSlug: String,
    val semesterNumber: Int,
    val dayOfWeek: Int,            // 1=Saturday, 2=Sunday, ... 7=Friday
    val startTime: String,         // "09:00"
    val endTime: String,           // "10:30"
    val roomNumber: String? = null,
    val instructorName: String? = null,
    val color: String? = null
)
