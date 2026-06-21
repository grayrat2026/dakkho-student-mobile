package com.dakkho.android.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// ── Phase 29: Social #96-101 Domain Models ──
// #96 Leaderboard, #97-98 Study Groups, #99-100 Peer Connections, #101 Community

// ════════════════════════════════════════════════════
// #96: Leaderboard
// ════════════════════════════════════════════════════

data class LeaderboardEntry(
    val rank: Int,
    val userId: String,
    val name: String,
    val avatarUrl: String? = null,
    val xpPoints: Int,
    val technology: String? = null,
    val coursesCompleted: Int = 0,
    val weeklyChange: Int = 0,  // positive = moved up, negative = moved down
    val isCurrentUser: Boolean = false
)

enum class LeaderboardPeriod(val label: String, val value: String) {
    WEEKLY("সাপ্তাহিক", "weekly"),
    MONTHLY("মাসিক", "monthly"),
    ALL_TIME("সর্বকালের", "all_time");

    companion object {
        fun fromValue(value: String): LeaderboardPeriod =
            entries.find { it.value == value } ?: WEEKLY
    }
}

data class LeaderboardData(
    val entries: List<LeaderboardEntry> = emptyList(),
    val currentUserRank: LeaderboardEntry? = null,
    val period: LeaderboardPeriod = LeaderboardPeriod.WEEKLY
)

// ════════════════════════════════════════════════════
// #97-98: Study Groups
// ════════════════════════════════════════════════════

data class StudyGroup(
    val id: String,
    val name: String,
    val description: String,
    val subject: String,
    val technology: String? = null,
    val memberCount: Int = 0,
    val maxMembers: Int = 20,
    val avatarUrls: List<String> = emptyList(),  // first few member avatars
    val isJoined: Boolean = false,
    val createdBy: String = "",
    val createdAt: String = "",
    val sharedNotesCount: Int = 0
)

data class StudyGroupMessage(
    val id: String,
    val senderId: String,
    val senderName: String,
    val senderAvatar: String? = null,
    val content: String,
    val timestamp: String = "",
    val type: MessageType = MessageType.TEXT
)

enum class MessageType(val value: String) {
    TEXT("text"),
    NOTE("note"),
    LINK("link"),
    IMAGE("image");

    companion object {
        fun fromValue(value: String): MessageType =
            entries.find { it.value == value } ?: TEXT
    }
}

data class SharedNote(
    val id: String,
    val title: String,
    val content: String,
    val authorName: String,
    val authorAvatar: String? = null,
    val subject: String = "",
    val createdAt: String = "",
    val likes: Int = 0,
    val isLiked: Boolean = false
)

// ════════════════════════════════════════════════════
// #99-100: Peer Connections
// ════════════════════════════════════════════════════

data class PeerUser(
    val id: String,
    val name: String,
    val avatarUrl: String? = null,
    val technology: String? = null,
    val semester: Int? = null,
    val xpPoints: Int = 0,
    val isFollowing: Boolean = false,
    val mutualConnections: Int = 0,
    val coursesInProgress: Int = 0
)

data class PeerSuggestion(
    val peer: PeerUser,
    val reason: String  // e.g., "একই বিভাগ", "একই সেমিস্টার"
)

data class PeerActivity(
    val id: String,
    val peerId: String,
    val peerName: String,
    val peerAvatar: String? = null,
    val type: PeerActivityType,
    val description: String,
    val timestamp: String = ""
)

enum class PeerActivityType(val label: String, val value: String) {
    COMPLETED_COURSE("কোর্স সম্পন্ন", "completed_course"),
    EARNED_CERTIFICATE("সার্টিফিকেট অর্জন", "earned_certificate"),
    JOINED_GROUP("গ্রুপে যোগ", "joined_group"),
    ACHIEVEMENT("অর্জন", "achievement"),
    NEW_POST("নতুন পোস্ট", "new_post");

    companion object {
        fun fromValue(value: String): PeerActivityType =
            entries.find { it.value == value } ?: COMPLETED_COURSE
    }
}

// ════════════════════════════════════════════════════
// #101: Community / Forum
// ════════════════════════════════════════════════════

data class CommunityPost(
    val id: String,
    val authorId: String,
    val authorName: String,
    val authorAvatar: String? = null,
    val title: String,
    val content: String,
    val category: String = "",
    val upvotes: Int = 0,
    val commentCount: Int = 0,
    val isUpvoted: Boolean = false,
    val createdAt: String = "",
    val tags: List<String> = emptyList()
)

data class CommunityComment(
    val id: String,
    val authorId: String,
    val authorName: String,
    val authorAvatar: String? = null,
    val content: String,
    val upvotes: Int = 0,
    val isUpvoted: Boolean = false,
    val createdAt: String = "",
    val parentId: String? = null  // for nested replies
)

// ════════════════════════════════════════════════════
// Feedback & Roadmap
// ════════════════════════════════════════════════════

data class FeedbackData(
    val rating: Int = 0,
    val comment: String = "",
    val category: FeedbackCategory = FeedbackCategory.GENERAL,
    val screenName: String = ""
)

enum class FeedbackCategory(val label: String, val value: String) {
    GENERAL("সাধারণ", "general"),
    BUG("বাগ", "bug"),
    FEATURE("ফিচার", "feature"),
    IMPROVEMENT("উন্নতি", "improvement");

    companion object {
        fun fromValue(value: String): FeedbackCategory =
            entries.find { it.value == value } ?: GENERAL
    }
}

data class RoadmapFeature(
    val id: String,
    val title: String,
    val description: String,
    val status: FeatureStatus = FeatureStatus.PLANNED,
    val upvotes: Int = 0,
    val isUpvoted: Boolean = false,
    val category: String = ""
)

enum class FeatureStatus(val label: String, val value: String) {
    PLANNED("পরিকল্পিত", "planned"),
    IN_PROGRESS("চলমান", "in_progress"),
    COMPLETED("সম্পন্ন", "completed");

    companion object {
        fun fromValue(value: String): FeatureStatus =
            entries.find { it.value == value } ?: PLANNED
    }
}

// ════════════════════════════════════════════════════
// DTOs
// ════════════════════════════════════════════════════

@JsonClass(generateAdapter = true)
data class LeaderboardEntryDto(
    @Json(name = "rank") val rank: Int,
    @Json(name = "user_id") val userId: String,
    @Json(name = "name") val name: String,
    @Json(name = "avatar_url") val avatarUrl: String? = null,
    @Json(name = "xp_points") val xpPoints: Int,
    @Json(name = "technology") val technology: String? = null,
    @Json(name = "courses_completed") val coursesCompleted: Int = 0,
    @Json(name = "weekly_change") val weeklyChange: Int = 0,
    @Json(name = "is_current_user") val isCurrentUser: Boolean = false
) {
    fun toDomain(): LeaderboardEntry = LeaderboardEntry(
        rank = rank, userId = userId, name = name, avatarUrl = avatarUrl,
        xpPoints = xpPoints, technology = technology,
        coursesCompleted = coursesCompleted, weeklyChange = weeklyChange,
        isCurrentUser = isCurrentUser
    )
}

@JsonClass(generateAdapter = true)
data class StudyGroupDto(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "description") val description: String,
    @Json(name = "subject") val subject: String,
    @Json(name = "technology") val technology: String? = null,
    @Json(name = "member_count") val memberCount: Int = 0,
    @Json(name = "max_members") val maxMembers: Int = 20,
    @Json(name = "avatar_urls") val avatarUrls: List<String> = emptyList(),
    @Json(name = "is_joined") val isJoined: Boolean = false,
    @Json(name = "created_by") val createdBy: String = "",
    @Json(name = "created_at") val createdAt: String = "",
    @Json(name = "shared_notes_count") val sharedNotesCount: Int = 0
) {
    fun toDomain(): StudyGroup = StudyGroup(
        id = id, name = name, description = description, subject = subject,
        technology = technology, memberCount = memberCount, maxMembers = maxMembers,
        avatarUrls = avatarUrls, isJoined = isJoined, createdBy = createdBy,
        createdAt = createdAt, sharedNotesCount = sharedNotesCount
    )
}

@JsonClass(generateAdapter = true)
data class PeerUserDto(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "avatar_url") val avatarUrl: String? = null,
    @Json(name = "technology") val technology: String? = null,
    @Json(name = "semester") val semester: Int? = null,
    @Json(name = "xp_points") val xpPoints: Int = 0,
    @Json(name = "is_following") val isFollowing: Boolean = false,
    @Json(name = "mutual_connections") val mutualConnections: Int = 0,
    @Json(name = "courses_in_progress") val coursesInProgress: Int = 0
) {
    fun toDomain(): PeerUser = PeerUser(
        id = id, name = name, avatarUrl = avatarUrl, technology = technology,
        semester = semester, xpPoints = xpPoints, isFollowing = isFollowing,
        mutualConnections = mutualConnections, coursesInProgress = coursesInProgress
    )
}

@JsonClass(generateAdapter = true)
data class CommunityPostDto(
    @Json(name = "id") val id: String,
    @Json(name = "author_id") val authorId: String,
    @Json(name = "author_name") val authorName: String,
    @Json(name = "author_avatar") val authorAvatar: String? = null,
    @Json(name = "title") val title: String,
    @Json(name = "content") val content: String,
    @Json(name = "category") val category: String = "",
    @Json(name = "upvotes") val upvotes: Int = 0,
    @Json(name = "comment_count") val commentCount: Int = 0,
    @Json(name = "is_upvoted") val isUpvoted: Boolean = false,
    @Json(name = "created_at") val createdAt: String = "",
    @Json(name = "tags") val tags: List<String> = emptyList()
) {
    fun toDomain(): CommunityPost = CommunityPost(
        id = id, authorId = authorId, authorName = authorName,
        authorAvatar = authorAvatar, title = title, content = content,
        category = category, upvotes = upvotes, commentCount = commentCount,
        isUpvoted = isUpvoted, createdAt = createdAt, tags = tags
    )
}

@JsonClass(generateAdapter = true)
data class CommunityCommentDto(
    @Json(name = "id") val id: String,
    @Json(name = "author_id") val authorId: String,
    @Json(name = "author_name") val authorName: String,
    @Json(name = "author_avatar") val authorAvatar: String? = null,
    @Json(name = "content") val content: String,
    @Json(name = "upvotes") val upvotes: Int = 0,
    @Json(name = "is_upvoted") val isUpvoted: Boolean = false,
    @Json(name = "created_at") val createdAt: String = "",
    @Json(name = "parent_id") val parentId: String? = null
) {
    fun toDomain(): CommunityComment = CommunityComment(
        id = id, authorId = authorId, authorName = authorName,
        authorAvatar = authorAvatar, content = content,
        upvotes = upvotes, isUpvoted = isUpvoted,
        createdAt = createdAt, parentId = parentId
    )
}

@JsonClass(generateAdapter = true)
data class RoadmapFeatureDto(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String,
    @Json(name = "status") val status: String = "planned",
    @Json(name = "upvotes") val upvotes: Int = 0,
    @Json(name = "is_upvoted") val isUpvoted: Boolean = false,
    @Json(name = "category") val category: String = ""
) {
    fun toDomain(): RoadmapFeature = RoadmapFeature(
        id = id, title = title, description = description,
        status = FeatureStatus.fromValue(status), upvotes = upvotes,
        isUpvoted = isUpvoted, category = category
    )
}

@JsonClass(generateAdapter = true)
data class SubmitFeedbackRequest(
    @Json(name = "rating") val rating: Int,
    @Json(name = "comment") val comment: String,
    @Json(name = "category") val category: String = "general",
    @Json(name = "screen_name") val screenName: String = ""
)

@JsonClass(generateAdapter = true)
data class CreateGroupRequest(
    @Json(name = "name") val name: String,
    @Json(name = "description") val description: String,
    @Json(name = "subject") val subject: String,
    @Json(name = "max_members") val maxMembers: Int = 20
)

@JsonClass(generateAdapter = true)
data class CreatePostRequest(
    @Json(name = "title") val title: String,
    @Json(name = "content") val content: String,
    @Json(name = "category") val category: String = "",
    @Json(name = "tags") val tags: List<String> = emptyList()
)
