package com.dakkho.android.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// ── Phase 28: Help & Support #83-90 Domain Models ──
// #83 Help Hub, #84 FAQ, #85 Contact Support, #86 Ticket Detail,
// #87 Report Issue, #88 Legal Screens (Terms/Privacy/Refund)

// ════════════════════════════════════════════════════
// #83: Help Hub
// ════════════════════════════════════════════════════

data class HelpCategory(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val articleCount: Int = 0
)

// ════════════════════════════════════════════════════
// #84: FAQ
// ════════════════════════════════════════════════════

data class FAQCategory(
    val id: String,
    val title: String,
    val questions: List<FAQItem>
)

data class FAQItem(
    val id: String,
    val question: String,
    val answer: String,
    val category: String = ""
)

// ════════════════════════════════════════════════════
// #85-86: Support Tickets
// ════════════════════════════════════════════════════

enum class TicketStatus(val label: String, val value: String) {
    OPEN("খোলা", "open"),
    IN_PROGRESS("প্রক্রিয়াধীন", "in_progress"),
    RESOLVED("সমাধান", "resolved"),
    CLOSED("বন্ধ", "closed");

    companion object {
        fun fromValue(value: String): TicketStatus =
            entries.find { it.value == value } ?: OPEN
    }
}

data class SupportTicket(
    val id: String,
    val subject: String,
    val description: String,
    val category: String,
    val status: TicketStatus = TicketStatus.OPEN,
    val priority: TicketPriority = TicketPriority.MEDIUM,
    val createdAt: String = "",
    val updatedAt: String = "",
    val messages: List<TicketMessage> = emptyList(),
    val attachments: List<TicketAttachment> = emptyList()
)

data class TicketMessage(
    val id: String,
    val senderName: String,
    val senderRole: String = "student",  // student, support, admin
    val content: String,
    val timestamp: String = "",
    val attachments: List<TicketAttachment> = emptyList(),
    val isFromStudent: Boolean = true
)

data class TicketAttachment(
    val id: String,
    val fileName: String,
    val fileUrl: String,
    val fileSize: Long = 0L,
    val mimeType: String = "image/jpeg"
) {
    val fileSizeKB: Float get() = fileSize / 1024f
}

enum class TicketPriority(val label: String, val value: String) {
    LOW("কম", "low"),
    MEDIUM("মাঝারি", "medium"),
    HIGH("উচ্চ", "high"),
    URGENT("জরুরি", "urgent");

    companion object {
        fun fromValue(value: String): TicketPriority =
            entries.find { it.value == value } ?: MEDIUM
    }
}

// ════════════════════════════════════════════════════
// #87: Report Issue / Bug Report
// ════════════════════════════════════════════════════

enum class BugCategory(val label: String, val value: String) {
    APP_CRASH("অ্যাপ ক্র্যাশ", "app_crash"),
    VIDEO_PLAYBACK("ভিডিও প্লেব্যাক", "video_playback"),
    DOWNLOAD_ISSUE("ডাউনলোড সমস্যা", "download_issue"),
    PAYMENT_ISSUE("পেমেন্ট সমস্যা", "payment_issue"),
    LOGIN_ISSUE("লগইন সমস্যা", "login_issue"),
    CONTENT_ISSUE("কন্টেন্ট সমস্যা", "content_issue"),
    OTHER("অন্যান্য", "other");

    companion object {
        fun fromValue(value: String): BugCategory =
            entries.find { it.value == value } ?: OTHER
    }
}

enum class BugSeverity(val label: String, val value: String) {
    MINOR("সামান্য", "minor"),
    MODERATE("মাঝারি", "moderate"),
    MAJOR("গুরুতর", "major"),
    CRITICAL("সংকটময়", "critical");

    companion object {
        fun fromValue(value: String): BugSeverity =
            entries.find { it.value == value } ?: MODERATE
    }
}

data class BugReport(
    val category: BugCategory = BugCategory.OTHER,
    val severity: BugSeverity = BugSeverity.MODERATE,
    val description: String = "",
    val screenshotPath: String? = null,
    val logData: String? = null,
    val deviceInfo: String = "",
    val appVersion: String = ""
)

// ════════════════════════════════════════════════════
// DTOs
// ════════════════════════════════════════════════════

@JsonClass(generateAdapter = true)
data class SupportTicketDto(
    @Json(name = "id") val id: String,
    @Json(name = "subject") val subject: String,
    @Json(name = "description") val description: String,
    @Json(name = "category") val category: String,
    @Json(name = "status") val status: String = "open",
    @Json(name = "priority") val priority: String = "medium",
    @Json(name = "created_at") val createdAt: String = "",
    @Json(name = "updated_at") val updatedAt: String = "",
    @Json(name = "messages") val messages: List<TicketMessageDto> = emptyList(),
    @Json(name = "attachments") val attachments: List<TicketAttachmentDto> = emptyList()
) {
    fun toDomain(): SupportTicket = SupportTicket(
        id = id, subject = subject, description = description, category = category,
        status = TicketStatus.fromValue(status), priority = TicketPriority.fromValue(priority),
        createdAt = createdAt, updatedAt = updatedAt,
        messages = messages.map { it.toDomain() }, attachments = attachments.map { it.toDomain() }
    )
}

@JsonClass(generateAdapter = true)
data class TicketMessageDto(
    @Json(name = "id") val id: String,
    @Json(name = "sender_name") val senderName: String,
    @Json(name = "sender_role") val senderRole: String = "student",
    @Json(name = "content") val content: String,
    @Json(name = "timestamp") val timestamp: String = "",
    @Json(name = "attachments") val attachments: List<TicketAttachmentDto> = emptyList(),
    @Json(name = "is_from_student") val isFromStudent: Boolean = true
) {
    fun toDomain(): TicketMessage = TicketMessage(
        id = id, senderName = senderName, senderRole = senderRole,
        content = content, timestamp = timestamp,
        attachments = attachments.map { it.toDomain() }, isFromStudent = isFromStudent
    )
}

@JsonClass(generateAdapter = true)
data class TicketAttachmentDto(
    @Json(name = "id") val id: String,
    @Json(name = "file_name") val fileName: String,
    @Json(name = "file_url") val fileUrl: String,
    @Json(name = "file_size") val fileSize: Long = 0L,
    @Json(name = "mime_type") val mimeType: String = "image/jpeg"
) {
    fun toDomain(): TicketAttachment = TicketAttachment(
        id = id, fileName = fileName, fileUrl = fileUrl,
        fileSize = fileSize, mimeType = mimeType
    )
}

@JsonClass(generateAdapter = true)
data class CreateTicketRequest(
    @Json(name = "subject") val subject: String,
    @Json(name = "description") val description: String,
    @Json(name = "category") val category: String,
    @Json(name = "priority") val priority: String = "medium"
)

@JsonClass(generateAdapter = true)
data class SendMessageRequest(
    @Json(name = "content") val content: String
)
