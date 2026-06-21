package com.dakkho.android.domain.model

/**
 * Domain model for a Q&A discussion thread within a course.
 */
data class Discussion(
    val id: String,
    val courseId: String,
    val userId: String,
    val userName: String,
    val title: String,
    val body: String,
    val tags: List<String> = emptyList(),
    val likes: Int = 0,
    val replyCount: Int = 0,
    val isPinned: Boolean = false,
    val isClosed: Boolean = false,
    val isLikedByUser: Boolean = false,
    val createdAt: String,
    val updatedAt: String? = null
)

/**
 * Domain model for a reply to a discussion thread.
 */
data class DiscussionReply(
    val id: String,
    val threadId: String,
    val userId: String,
    val userName: String,
    val body: String,
    val likes: Int = 0,
    val isLikedByUser: Boolean = false,
    val createdAt: String,
    val updatedAt: String? = null
)

/**
 * DTO for creating a new discussion thread.
 */
data class CreateDiscussionRequest(
    val courseId: String,
    val title: String,
    val body: String,
    val tags: List<String> = emptyList()
)

/**
 * DTO for creating a reply.
 */
data class CreateReplyRequest(
    val body: String
)

/**
 * Response for listing discussion threads.
 */
data class DiscussionListResponse(
    val discussions: List<DiscussionDto> = emptyList(),
    val total: Int = 0,
    val page: Int = 1,
    val limit: Int = 20
)

/**
 * DTO for a discussion thread from the API.
 */
data class DiscussionDto(
    val id: String = "",
    val courseId: String = "",
    val userId: String = "",
    val userName: String = "",
    val title: String = "",
    val body: String = "",
    val tags: List<String> = emptyList(),
    val likes: Int = 0,
    val replies: Int = 0,
    val isPinned: Boolean = false,
    val isClosed: Boolean = false,
    val createdAt: String = "",
    val updatedAt: String? = null
)

/**
 * DTO for a reply from the API.
 */
data class DiscussionReplyDto(
    val id: String = "",
    val threadId: String = "",
    val userId: String = "",
    val userName: String = "",
    val body: String = "",
    val likes: Int = 0,
    val createdAt: String = "",
    val updatedAt: String? = null
)

/**
 * Response for a single discussion thread with replies.
 */
data class DiscussionDetailResponse(
    val thread: DiscussionDto = DiscussionDto(),
    val replies: List<DiscussionReplyDto> = emptyList(),
    val totalReplies: Int = 0
)

/**
 * Response for toggling like on a thread or reply.
 */
data class LikeToggleResponse(
    val liked: Boolean = false,
    val likes: Int = 0
)
