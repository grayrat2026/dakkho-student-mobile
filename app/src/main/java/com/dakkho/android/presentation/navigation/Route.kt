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
}
