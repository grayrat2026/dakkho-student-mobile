package com.dakkho.android.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CertificateDto(
    @Json(name = "id") val id: String,
    @Json(name = "course_id") val courseId: String,
    @Json(name = "course_name") val courseName: String?,
    @Json(name = "student_name") val studentName: String?,
    @Json(name = "completion_date") val completionDate: String?,
    @Json(name = "certificate_url") val certificateUrl: String?,
    @Json(name = "instructor_name") val instructorName: String?,
    @Json(name = "grade") val grade: String?,
    @Json(name = "duration_hours") val durationHours: Int?
)

data class Certificate(
    val id: String,
    val courseId: String,
    val courseName: String,
    val studentName: String,
    val completionDate: String,
    val certificateUrl: String?,
    val instructorName: String?,
    val grade: String?,
    val durationHours: Int?
) {
    /** Formatted completion date for display */
    val displayDate: String
        get() = completionDate
}

@JsonClass(generateAdapter = true)
data class CourseCompletionStatus(
    @Json(name = "course_id") val courseId: String,
    @Json(name = "completed") val completed: Boolean,
    @Json(name = "completion_percentage") val completionPercentage: Float?,
    @Json(name = "completion_date") val completionDate: String?,
    @Json(name = "certificate_id") val certificateId: String?
)
