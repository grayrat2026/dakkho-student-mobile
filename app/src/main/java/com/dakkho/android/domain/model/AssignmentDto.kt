package com.dakkho.android.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AssignmentDto(
    @Json(name = "id") val id: String,
    @Json(name = "course_id") val courseId: String,
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String?,
    @Json(name = "due_date") val dueDate: String?,
    @Json(name = "status") val status: String?, // pending, submitted, graded
    @Json(name = "max_score") val maxScore: Float?,
    @Json(name = "score") val score: Float?,
    @Json(name = "submission_url") val submissionUrl: String?,
    @Json(name = "submitted_at") val submittedAt: String?,
    @Json(name = "graded_at") val gradedAt: String?,
    @Json(name = "feedback") val feedback: String?,
    @Json(name = "created_at") val createdAt: String?
)
