package com.dakkho.android.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationChannelManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val CHANNEL_COURSES = "courses"
        const val CHANNEL_PAYMENTS = "payments"
        const val CHANNEL_SYSTEM = "system"
        const val CHANNEL_LIVE_CLASSES = "live_classes"
    }

    fun createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channels = listOf(
                NotificationChannel(
                    CHANNEL_COURSES,
                    "Courses",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Course updates, new content, and enrollment notifications"
                },
                NotificationChannel(
                    CHANNEL_PAYMENTS,
                    "Payments",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Payment confirmations and transaction alerts"
                },
                NotificationChannel(
                    CHANNEL_SYSTEM,
                    "System",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "App updates, maintenance, and system announcements"
                },
                NotificationChannel(
                    CHANNEL_LIVE_CLASSES,
                    "Live Classes",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Live class reminders and schedules"
                }
            )

            notificationManager.createNotificationChannels(channels)
        }
    }
}
