package com.dakkho.android.domain.model

// ── Curriculum Hierarchy: Subject → Class → Unit → Lesson ──

data class Curriculum(
    val sections: List<CurriculumSubject> = emptyList()
)

data class CurriculumSubject(
    val id: String,
    val title: String,
    val order: Int = 0,
    val classes: List<SubjectClass> = emptyList()
)

data class SubjectClass(
    val id: String,
    val title: String,
    val order: Int = 0,
    val units: List<CourseUnit> = emptyList()
)

data class CourseUnit(
    val id: String,
    val title: String,
    val order: Int = 0,
    val lessons: List<Lesson> = emptyList()
)

data class Lesson(
    val id: String,
    val title: String,
    val type: String? = null, // "video", "quiz", "pdf", etc.
    val durationSeconds: Int? = null,
    val isFree: Boolean = false,
    val order: Int = 0,
    val videoUrl: String? = null,
    val label: String? = null, // e.g. "1.1", "1.2"
    val resources: LessonResources? = null,
    val progress: Float = 0f, // 0.0 to 1.0
    val isCompleted: Boolean = false
)

/**
 * Resources attached to a lesson — available alongside the video.
 */
data class LessonResources(
    val lectureSheets: List<ResourceFile> = emptyList(),
    val pdfs: List<ResourceFile> = emptyList(),
    val notes: List<ResourceFile> = emptyList(),
    val quizzes: List<QuizItem> = emptyList(),
    val hasTimestamps: Boolean = false,
    val hasQA: Boolean = false
)

data class ResourceFile(
    val id: String,
    val title: String,
    val fileUrl: String? = null,
    val fileSize: Long? = null,
    val fileType: String? = null // "pdf", "doc", "ppt", etc.
)

data class QuizItem(
    val id: String,
    val title: String,
    val type: String = "mcq", // "mcq", "true_false", "short_answer"
    val questionCount: Int = 0,
    val durationMinutes: Int? = null,
    val isCompleted: Boolean = false,
    val score: Float? = null
)

// ── Keep backward compatibility: Section maps to Subject ──
data class Section(
    val id: String,
    val title: String,
    val order: Int = 0,
    val lessons: List<Lesson> = emptyList()
)
