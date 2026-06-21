package com.dakkho.android.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Domain model for a general forum thread (not course-specific).
 */
data class ForumThread(
    val id: String,
    val authorId: String,
    val authorName: String,
    val authorAvatar: String? = null,
    val title: String,
    val body: String,
    val category: String = "general",
    val upvotes: Int = 0,
    val commentCount: Int = 0,
    val isUpvotedByUser: Boolean = false,
    val isPinned: Boolean = false,
    val createdAt: String,
    val updatedAt: String? = null
) {
    /** Relative time display */
    val relativeTime: String
        get() = formatRelativeTime(createdAt)

    private fun formatRelativeTime(dateStr: String): String {
        return try {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.US)
            val date = sdf.parse(dateStr) ?: return dateStr
            val diff = System.currentTimeMillis() - date.time
            val minutes = diff / 60_000
            val hours = diff / 3_600_000
            val days = diff / 86_400_000
            when {
                minutes < 1 -> "Just now"
                minutes < 60 -> "${minutes}m ago"
                hours < 24 -> "${hours}h ago"
                days < 7 -> "${days}d ago"
                else -> dateStr.take(10)
            }
        } catch (_: Exception) { dateStr }
    }
}

/**
 * Domain model for a comment on a forum thread.
 */
data class ForumComment(
    val id: String,
    val threadId: String,
    val authorId: String,
    val authorName: String,
    val authorAvatar: String? = null,
    val body: String,
    val upvotes: Int = 0,
    val isUpvotedByUser: Boolean = false,
    val createdAt: String,
    val updatedAt: String? = null
)

/**
 * DTO for forum thread from API.
 */
@JsonClass(generateAdapter = true)
data class ForumThreadDto(
    @Json(name = "id") val id: String = "",
    @Json(name = "user_id") val userId: String = "",
    @Json(name = "user_name") val userName: String = "",
    @Json(name = "user_avatar") val userAvatar: String? = null,
    @Json(name = "title") val title: String = "",
    @Json(name = "body") val body: String = "",
    @Json(name = "category") val category: String = "general",
    @Json(name = "upvotes") val upvotes: Int = 0,
    @Json(name = "comment_count") val commentCount: Int = 0,
    @Json(name = "is_upvoted") val isUpvoted: Boolean = false,
    @Json(name = "is_pinned") val isPinned: Boolean = false,
    @Json(name = "created_at") val createdAt: String = "",
    @Json(name = "updated_at") val updatedAt: String? = null
)

/**
 * DTO for forum comment from API.
 */
@JsonClass(generateAdapter = true)
data class ForumCommentDto(
    @Json(name = "id") val id: String = "",
    @Json(name = "thread_id") val threadId: String = "",
    @Json(name = "user_id") val userId: String = "",
    @Json(name = "user_name") val userName: String = "",
    @Json(name = "user_avatar") val userAvatar: String? = null,
    @Json(name = "body") val body: String = "",
    @Json(name = "upvotes") val upvotes: Int = 0,
    @Json(name = "is_upvoted") val isUpvoted: Boolean = false,
    @Json(name = "created_at") val createdAt: String = "",
    @Json(name = "updated_at") val updatedAt: String? = null
)

/**
 * Response for listing forum threads.
 */
data class ForumThreadListResponse(
    val threads: List<ForumThreadDto> = emptyList(),
    val total: Int = 0,
    val page: Int = 1
)

/**
 * Response for forum thread detail with comments.
 */
data class ForumThreadDetailResponse(
    val thread: ForumThreadDto = ForumThreadDto(),
    val comments: List<ForumCommentDto> = emptyList(),
    val totalComments: Int = 0
)

/**
 * Request body for creating a new forum thread.
 */
data class CreateForumThreadRequest(
    val title: String,
    val body: String,
    val category: String = "general"
)

/**
 * Request body for creating a comment.
 */
data class CreateForumCommentRequest(
    val body: String
)

/**
 * Forum category definitions.
 */
enum class ForumCategory(val displayName: String, val icon: String) {
    GENERAL("General", "💬"),
    ACADEMIC("Academic", "📚"),
    TECH("Technology", "💻"),
    HELP("Help & Support", "🆘"),
    CAREER("Career", "💼"),
    EVENTS("Events", "🎉");

    companion object {
        fun fromSlug(slug: String): ForumCategory =
            entries.find { it.name.equals(slug, ignoreCase = true) } ?: GENERAL
    }
}
