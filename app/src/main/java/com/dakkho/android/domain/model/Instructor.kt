package com.dakkho.android.domain.model

data class Instructor(
    val id: String,
    val name: String,
    val avatarUrl: String? = null,
    val title: String? = null,
    val courseCount: Int = 0,
    val studentCount: Int = 0,
    val rating: Float = 0f
)

/**
 * Detailed instructor profile with bio, social links, and courses.
 */
data class InstructorDetail(
    val id: String,
    val name: String,
    val avatarUrl: String? = null,
    val coverUrl: String? = null,
    val title: String? = null,
    val bio: String? = null,
    val specialization: String? = null,
    val email: String? = null,
    val courseCount: Int = 0,
    val studentCount: Int = 0,
    val rating: Float = 0f,
    val socialLinks: SocialLinks = SocialLinks(),
    val isActive: Boolean = true,
    val createdAt: String? = null,
    val courses: List<Course> = emptyList()
)

/**
 * Social media links for an instructor.
 */
data class SocialLinks(
    val youtube: String? = null,
    val github: String? = null,
    val facebook: String? = null,
    val linkedin: String? = null,
    val website: String? = null
) {
    val hasAny: Boolean get() = !youtube.isNullOrBlank() || !github.isNullOrBlank() ||
            !facebook.isNullOrBlank() || !linkedin.isNullOrBlank() || !website.isNullOrBlank()
}
