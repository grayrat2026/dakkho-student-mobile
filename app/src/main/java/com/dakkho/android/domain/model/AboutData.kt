package com.dakkho.android.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AboutData(
    @Json(name = "mission") val mission: String? = null,
    @Json(name = "vision") val vision: String? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "team") val team: List<TeamMember>? = null,
    @Json(name = "founded_year") val foundedYear: Int? = null,
    @Json(name = "total_students") val totalStudents: Int? = null,
    @Json(name = "total_courses") val totalCourses: Int? = null
)

@JsonClass(generateAdapter = true)
data class TeamMember(
    @Json(name = "name") val name: String,
    @Json(name = "role") val role: String?,
    @Json(name = "avatar_url") val avatarUrl: String?,
    @Json(name = "bio") val bio: String?
)
