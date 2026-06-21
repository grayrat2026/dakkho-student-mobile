package com.dakkho.android.domain.model

/**
 * Domain model for an assignment item displayed on the Assignment screen.
 */
data class AssignmentItem(
    val id: String,
    val courseId: String,
    val title: String,
    val description: String?,
    val dueDate: String?,
    val status: AssignmentStatus,
    val maxScore: Float?,
    val score: Float?,
    val submissionUrl: String?,
    val submittedAt: String?,
    val gradedAt: String?,
    val feedback: String?,
    val createdAt: String?
)

enum class AssignmentStatus {
    PENDING,
    SUBMITTED,
    GRADED;

    companion object {
        fun fromString(value: String?): AssignmentStatus {
            return when (value?.lowercase()) {
                "submitted" -> SUBMITTED
                "graded" -> GRADED
                else -> PENDING
            }
        }
    }
}
