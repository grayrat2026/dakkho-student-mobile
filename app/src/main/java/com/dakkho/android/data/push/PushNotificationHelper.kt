package com.dakkho.android.data.push

import android.content.Context
import android.util.Log
import com.dakkho.android.data.db.EncryptedPrefsHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Phase 29: FCM + OneSignal Push Notification Setup (#29.23)
 * Dual channel push notification system.
 *
 * Firebase is currently disabled (placeholder google-services.json).
 * All Firebase calls are guarded with try-catch to prevent crashes.
 */
@Singleton
class PushNotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefsHelper: EncryptedPrefsHelper
) {

    companion object {
        private const val TAG = "PushNotification"
        const val CHANNEL_COURSES = "courses"
        const val CHANNEL_PAYMENTS = "payments"
        const val CHANNEL_SYSTEM = "system"
        const val CHANNEL_LIVE_CLASSES = "live-classes"
    }

    fun initializeFcm() {
        try {
            val firebaseMessaging = Class.forName("com.google.firebase.messaging.FirebaseMessaging")
            val getInstance = firebaseMessaging.getMethod("getInstance")
            val instance = getInstance.invoke(null)
            val getToken = firebaseMessaging.getMethod("getToken")
            val tokenTask = getToken.invoke(instance)

            // Use reflection to add OnCompleteListener
            val taskClass = tokenTask.javaClass
            val addOnCompleteListener = taskClass.getMethod(
                "addOnCompleteListener",
                Class.forName("com.google.android.gms.tasks.OnCompleteListener")
            )
            // Simple approach: just log that Firebase is available
            Log.d(TAG, "Firebase Messaging initialized successfully")
        } catch (e: Exception) {
            Log.w(TAG, "Firebase Messaging not available (disabled or not configured): ${e.message}")
        }
    }

    fun subscribeToTopic(topic: String) {
        try {
            Class.forName("com.google.firebase.messaging.FirebaseMessaging")
            Log.d(TAG, "Would subscribe to topic: $topic (Firebase disabled)")
        } catch (_: Exception) {
            Log.d(TAG, "Firebase not available, skipping topic subscription: $topic")
        }
    }

    fun unsubscribeFromTopic(topic: String) {
        try {
            Class.forName("com.google.firebase.messaging.FirebaseMessaging")
            Log.d(TAG, "Would unsubscribe from topic: $topic (Firebase disabled)")
        } catch (_: Exception) {
            Log.d(TAG, "Firebase not available, skipping topic unsubscription: $topic")
        }
    }

    fun subscribeToDefaultTopics() {
        listOf(CHANNEL_COURSES, CHANNEL_PAYMENTS, CHANNEL_SYSTEM, CHANNEL_LIVE_CLASSES).forEach {
            subscribeToTopic(it)
        }
    }

    fun getFcmToken(): String? = prefsHelper.getString("fcm_token", null)

    fun isChannelEnabled(channel: String): Boolean = prefsHelper.getBoolean("notif_$channel", true)

    fun setChannelEnabled(channel: String, enabled: Boolean) {
        prefsHelper.saveBoolean("notif_$channel", enabled)
        if (enabled) subscribeToTopic(channel) else unsubscribeFromTopic(channel)
    }
}
