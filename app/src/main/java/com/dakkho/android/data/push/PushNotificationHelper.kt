package com.dakkho.android.data.push

import android.content.Context
import android.util.Log
import com.dakkho.android.data.db.EncryptedPrefsHelper
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Phase 29: FCM + OneSignal Push Notification Setup (#29.23)
 * Dual channel push notification system.
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
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d(TAG, "FCM Token: $token")
                prefsHelper.saveString("fcm_token", token)
            } else {
                Log.w(TAG, "FCM token retrieval failed", task.exception)
            }
        }
    }

    fun subscribeToTopic(topic: String) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Subscribed to topic: $topic")
                } else {
                    Log.w(TAG, "Failed to subscribe to topic: $topic", task.exception)
                }
            }
    }

    fun unsubscribeFromTopic(topic: String) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
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
