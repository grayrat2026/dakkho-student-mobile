package com.dakkho.android.domain.model

/**
 * Domain model for a Semester within a Department.
 *
 * Bangladesh Diploma (Polytechnic) system:
 * - 7 regular semesters (Semester 1 – Semester 7)
 * - 8th semester = ইন্টার্নি (Internship) — not a regular academic semester
 *
 * Each department defines how many semesters it has via Technology.semesterCount.
 * The app dynamically renders semester tabs based on the department's semesterCount.
 * When semesterCount = 8, the 8th tab shows "ইন্টার্নি" instead of "Sem 8".
 */
data class Semester(
    val id: String,
    val departmentSlug: String,
    val number: Int,            // 1–8  (8 = internship)
    val name: String,           // "Semester 1" … "Semester 7", "ইন্টার্নি"
    val subjectCount: Int = 0,
    val totalCredits: Int = 0,
    val isActive: Boolean = true
) {
    /** Whether this semester is the internship semester */
    val isInternship: Boolean get() = number == INTERNSHIP_SEMESTER

    companion object {
        /** The semester number that represents internship */
        const val INTERNSHIP_SEMESTER = 8

        /** Display name for a given semester number */
        fun semesterName(number: Int): String {
            return if (number == INTERNSHIP_SEMESTER) {
                "ইন্টার্নি"
            } else {
                "Semester $number"
            }
        }

        /** Short chip label for semester tabs */
        fun semesterLabel(number: Int): String {
            return if (number == INTERNSHIP_SEMESTER) {
                "ইন্টার্নি"
            } else {
                "Sem $number"
            }
        }
    }
}

/**
 * Domain model for a Subject within a Semester.
 *
 * A subject belongs to a specific department + semester combination.
 * Each subject can link to a Course on the platform.
 */
data class Subject(
    val id: String,
    val semesterId: String,
    val departmentSlug: String,
    val semesterNumber: Int,
    val name: String,
    val code: String = "",
    val creditHours: Int = 0,
    val instructorName: String? = null,
    val instructorId: String? = null,
    val courseId: String? = null,            // Link to a Course on DAKKHO platform
    val description: String? = null,
    val syllabusTopics: List<String> = emptyList(),
    val sortOrder: Int = 0,
    val color: String? = null,               // Color hex for routine/schedule display
    val isActive: Boolean = true
)

/**
 * Domain model for a routine/schedule entry.
 * Represents a single time slot in the weekly timetable.
 */
data class RoutineEntry(
    val id: String,
    val subjectId: String,
    val subjectName: String,
    val subjectCode: String,
    val dayOfWeek: Int,          // 1=Saturday, 2=Sunday, ... 7=Friday (Bangladesh week)
    val startTime: String,       // "09:00"
    val endTime: String,         // "10:30"
    val roomNumber: String? = null,
    val instructorName: String? = null,
    val color: String? = null
) {
    companion object {
        /** Day names in Bangladesh week order (Sat–Fri) */
        val DAY_NAMES = listOf(
            "Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"
        )

        /** Bengali day names */
        val DAY_NAMES_BN = listOf(
            "শনিবার", "রবিবার", "সোমবার", "মঙ্গলবার", "বুধবার", "বৃহস্পতিবার", "শুক্রবার"
        )
    }
}

/**
 * Semester progress model.
 * Tracks where the student is within the 1–8 semester timeline.
 */
data class SemesterProgress(
    val currentSemester: Int,        // Which semester the student is currently in
    val totalSemesters: Int,         // Total semesters for the department (7 or 8)
    val progressPercent: Float,      // 0f – 1f, how far along the student is
    val isInternshipSemester: Boolean, // Whether current semester is the internship
    val estimatedCompletionDate: String? = null
) {
    /** Progress description text */
    val progressText: String
        get() = if (isInternshipSemester) {
            "ইন্টার্নি চলছে"
        } else {
            "Semester $currentSemester of $totalSemesters"
        }
}
