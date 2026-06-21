package com.dakkho.android.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Route {

    @Serializable
    data object Splash : Route()

    @Serializable
    data object Login : Route()

    @Serializable
    data object Signup : Route()

    @Serializable
    data object ForgotPassword : Route()

    @Serializable
    data object Home : Route()

    @Serializable
    data object Explore : Route()

    @Serializable
    data object MyCourses : Route()

    @Serializable
    data object Profile : Route()

    @Serializable
    data class CourseDetail(val courseId: String) : Route()

    @Serializable
    data class VideoPlayer(val videoId: String, val courseId: String) : Route()

    @Serializable
    data object InstructorList : Route()

    @Serializable
    data class InstructorProfile(val instructorId: String) : Route()

    @Serializable
    data object Search : Route()

    @Serializable
    data object Notifications : Route()

    @Serializable
    data class NotificationDetail(val notificationId: String) : Route()

    @Serializable
    data class Category(val technology: String) : Route()

    @Serializable
    data object About : Route()

    @Serializable
    data object Settings : Route()

    @Serializable
    data object Downloads : Route()

    @Serializable
    data object Bookmarks : Route()

    @Serializable
    data object WatchHistory : Route()

    @Serializable
    data object Certificates : Route()

    @Serializable
    data object Achievements : Route()

    @Serializable
    data object PaymentStatus : Route()

    @Serializable
    data class OtpVerification(val email: String) : Route()

    @Serializable
    data class Assignments(val courseId: String) : Route()

    @Serializable
    data class CourseCurriculum(
        val courseId: String,
        val courseTitle: String,
        val isEnrolled: Boolean
    ) : Route()

    @Serializable
    data class CourseReviews(
        val courseId: String,
        val courseTitle: String,
        val averageRating: Float,
        val reviewCount: Int,
        val isEnrolled: Boolean
    ) : Route()

    @Serializable
    data class CourseQnA(
        val courseId: String,
        val courseTitle: String,
        val isEnrolled: Boolean
    ) : Route()

    @Serializable
    data class CourseAnnouncements(
        val courseId: String,
        val courseTitle: String
    ) : Route()

    @Serializable
    data class CourseResources(
        val courseId: String,
        val courseTitle: String,
        val isEnrolled: Boolean
    ) : Route()

    @Serializable
    data class CourseNotes(
        val courseId: String,
        val courseTitle: String,
        val videoId: String = ""
    ) : Route()

    @Serializable
    data class CourseQuizzes(
        val courseId: String,
        val courseTitle: String
    ) : Route()

    @Serializable
    data class CourseProgress(
        val courseId: String,
        val courseTitle: String
    ) : Route()

    // ── Instructor Sub-pages ──

    @Serializable
    data class InstructorCourses(
        val instructorId: String,
        val instructorName: String
    ) : Route()

    @Serializable
    data class InstructorReviews(
        val instructorId: String,
        val instructorName: String,
        val averageRating: Float = 0f,
        val reviewCount: Int = 0
    ) : Route()

    @Serializable
    data class InstructorSchedule(
        val instructorId: String,
        val instructorName: String
    ) : Route()

    @Serializable
    data class InstructorContact(
        val instructorId: String,
        val instructorName: String
    ) : Route()

    // ── Live Sessions, Achievements, Discussion ──

    @Serializable
    data object LiveSessions : Route()

    @Serializable
    data class LiveClassDetail(val liveClassId: String) : Route()

    @Serializable
    data object DiscussionForum : Route()

    @Serializable
    data class DiscussionThread(val threadId: String) : Route()

    // ── Phase 23: Dynamic Department Routes ──
    // No hardcoded 20 departments — all departments come from the API
    // Admin/Instructor adds departments → API returns them → Student sees them

    @Serializable
    data object DepartmentList : Route()

    @Serializable
    data class Department(val slug: String) : Route()

    // ── Phase 24: Semester Routes ──
    // 7 regular semesters + 8th = ইন্টার্নি (Internship)
    // Each semester belongs to a department, parameterized by deptSlug + semesterNumber

    @Serializable
    data class Semester(
        val departmentSlug: String,
        val semesterNumber: Int   // 1–8, where 8 = ইন্টার্নি
    ) : Route()

    // ── Phase 25: Profile Sub-pages #65-71 ──
    // EditProfile, ChangePassword, LearningStats, Subscription, Referral
    // Bookmarks and Settings routes already exist above

    @Serializable
    data object EditProfile : Route()

    @Serializable
    data object ChangePassword : Route()

    @Serializable
    data object LearningStats : Route()

    @Serializable
    data object Subscription : Route()

    @Serializable
    data object Referral : Route()

    // ── Phase 26: Settings Part 1 #72-76 ──
    // Storage Management, Notification Preferences, Data Saver, Accessibility, About & Legal

    @Serializable
    data object StorageManagement : Route()

    @Serializable
    data object NotificationPreferences : Route()

    @Serializable
    data object DataSaver : Route()

    @Serializable
    data object AccessibilitySettings : Route()

    @Serializable
    data object AboutLegal : Route()

    // ── Phase 27: Settings Part 2 #77-82 ──
    // Theme Settings, Download Settings, Video Quality, Network & Data,
    // Content Protection, Active Sessions

    @Serializable
    data object ThemeSettings : Route()

    @Serializable
    data object DownloadSettings : Route()

    @Serializable
    data object VideoQualitySettings : Route()

    @Serializable
    data object NetworkData : Route()

    @Serializable
    data object ContentProtection : Route()

    @Serializable
    data object ActiveSessions : Route()
}
