package com.dakkho.android.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// ── Phase 28: Exam #91-95 Domain Models ──
// #89 Exam Prep, #90 Exam Schedule, #91 Exam Results,
// #92 Exam Practice, #93 Exam Tips

// ════════════════════════════════════════════════════
// #89: Exam Prep
// ════════════════════════════════════════════════════

data class ExamPrepInfo(
    val syllabusOverview: String = "",
    val importantTopics: List<ImportantTopic> = emptyList(),
    val studyPlan: List<StudyPlanItem> = emptyList(),
    val upcomingExamDate: String = "",
    val countdownDays: Int = 0
)

data class ImportantTopic(
    val id: String,
    val title: String,
    val subject: String,
    val weightage: String = "",
    val isCompleted: Boolean = false
)

data class StudyPlanItem(
    val id: String,
    val title: String,
    val description: String = "",
    val dayNumber: Int,
    val duration: String = "",
    val topics: List<String> = emptyList(),
    val isCompleted: Boolean = false
)

// ════════════════════════════════════════════════════
// #90: Exam Schedule
// ════════════════════════════════════════════════════

data class ExamSchedule(
    val id: String,
    val subject: String,
    val subjectCode: String = "",
    val date: String,         // ISO date string
    val time: String,         // e.g. "10:00 AM - 1:00 PM"
    val duration: String = "3 ঘন্টা",
    val examType: String = "ফাইনাল",
    val room: String = "",
    val semester: Int = 0,
    val department: String = ""
) {
    val countdownText: String get() {
        return try {
            val examDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).parse(date)
            val now = java.util.Date()
            val diff = (examDate?.time ?: 0L) - now.time
            val days = (diff / (1000 * 60 * 60 * 24)).toInt()
            when {
                days < 0 -> "শেষ"
                days == 0 -> "আজ"
                days == 1 -> "আগামীকাল"
                else -> "$days দিন বাকি"
            }
        } catch (e: Exception) { "" }
    }
}

// ════════════════════════════════════════════════════
// #91: Exam Results
// ════════════════════════════════════════════════════

data class ExamResult(
    val id: String,
    val semester: Int,
    val gpa: Float,
    val totalCredits: Float = 0f,
    val earnedCredits: Float = 0f,
    val subjects: List<SubjectResult> = emptyList()
)

data class SubjectResult(
    val id: String,
    val subjectName: String,
    val subjectCode: String = "",
    val credit: Float = 0f,
    val gradePoint: Float = 0f,
    val letterGrade: String = "",
    val marksObtained: Float = 0f,
    val totalMarks: Float = 100f
) {
    val marksPercent: Float get() = if (totalMarks > 0) (marksObtained / totalMarks) * 100f else 0f
}

// ════════════════════════════════════════════════════
// #92: Exam Practice
// ════════════════════════════════════════════════════

data class PracticeTest(
    val id: String,
    val title: String,
    val subject: String = "",
    val duration: Int = 30,        // minutes
    val totalQuestions: Int = 0,
    val marks: Int = 0,
    val year: String = "",          // e.g. "2024" for previous year
    val testType: PracticeTestType = PracticeTestType.MODEL_TEST
)

enum class PracticeTestType(val label: String, val value: String) {
    MODEL_TEST("মডেল টেস্ট", "model_test"),
    PREVIOUS_YEAR("পূর্ববর্তী বছর", "previous_year"),
    PRACTICE("অনুশীলন", "practice");

    companion object {
        fun fromValue(value: String): PracticeTestType =
            entries.find { it.value == value } ?: MODEL_TEST
    }
}

data class PracticeQuestion(
    val id: String,
    val question: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val explanation: String = "",
    val marks: Int = 1,
    val subject: String = ""
)

data class PracticeAttempt(
    val testId: String,
    val answers: Map<Int, Int> = emptyMap(),  // questionIndex -> selectedOptionIndex
    val timeTakenSeconds: Int = 0,
    val score: Int = 0,
    val totalMarks: Int = 0,
    val isSubmitted: Boolean = false
)

// ════════════════════════════════════════════════════
// #93: Exam Tips
// ════════════════════════════════════════════════════

data class ExamTip(
    val id: String,
    val title: String,
    val content: String,
    val category: TipCategory = TipCategory.STUDY_HACK,
    val isRead: Boolean = false
)

enum class TipCategory(val label: String, val value: String) {
    STUDY_HACK("পড়াশোনার টিপস", "study_hack"),
    TIME_MANAGEMENT("সময় ব্যবস্থাপনা", "time_management"),
    EXAM_STRATEGY("পরীক্ষার কৌশল", "exam_strategy"),
    WELLNESS("সুস্থতা", "wellness");

    companion object {
        fun fromValue(value: String): TipCategory =
            entries.find { it.value == value } ?: STUDY_HACK
    }
}

// ════════════════════════════════════════════════════
// DTOs
// ════════════════════════════════════════════════════

@JsonClass(generateAdapter = true)
data class ExamScheduleDto(
    @Json(name = "id") val id: String,
    @Json(name = "subject") val subject: String,
    @Json(name = "subject_code") val subjectCode: String = "",
    @Json(name = "date") val date: String,
    @Json(name = "time") val time: String,
    @Json(name = "duration") val duration: String = "3 ঘন্টা",
    @Json(name = "exam_type") val examType: String = "ফাইনাল",
    @Json(name = "room") val room: String = "",
    @Json(name = "semester") val semester: Int = 0,
    @Json(name = "department") val department: String = ""
) {
    fun toDomain(): ExamSchedule = ExamSchedule(
        id = id, subject = subject, subjectCode = subjectCode,
        date = date, time = time, duration = duration,
        examType = examType, room = room, semester = semester, department = department
    )
}

@JsonClass(generateAdapter = true)
data class ExamResultDto(
    @Json(name = "id") val id: String,
    @Json(name = "semester") val semester: Int,
    @Json(name = "gpa") val gpa: Float,
    @Json(name = "total_credits") val totalCredits: Float = 0f,
    @Json(name = "earned_credits") val earnedCredits: Float = 0f,
    @Json(name = "subjects") val subjects: List<SubjectResultDto> = emptyList()
) {
    fun toDomain(): ExamResult = ExamResult(
        id = id, semester = semester, gpa = gpa,
        totalCredits = totalCredits, earnedCredits = earnedCredits,
        subjects = subjects.map { it.toDomain() }
    )
}

@JsonClass(generateAdapter = true)
data class SubjectResultDto(
    @Json(name = "id") val id: String,
    @Json(name = "subject_name") val subjectName: String,
    @Json(name = "subject_code") val subjectCode: String = "",
    @Json(name = "credit") val credit: Float = 0f,
    @Json(name = "grade_point") val gradePoint: Float = 0f,
    @Json(name = "letter_grade") val letterGrade: String = "",
    @Json(name = "marks_obtained") val marksObtained: Float = 0f,
    @Json(name = "total_marks") val totalMarks: Float = 100f
) {
    fun toDomain(): SubjectResult = SubjectResult(
        id = id, subjectName = subjectName, subjectCode = subjectCode,
        credit = credit, gradePoint = gradePoint, letterGrade = letterGrade,
        marksObtained = marksObtained, totalMarks = totalMarks
    )
}
