package com.dakkho.android.data.payment

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.dakkho.android.data.db.EncryptedPrefsHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Phase 29: PipraPay Chrome Custom Tab Integration (#29.13)
 * Payment Flow:
 * 1. User taps "Enroll" → POST /api/payments/create
 * 2. Worker returns { order_id, payment_url, amount }
 * 3. App launches payment_url in CustomTabsIntent
 * 4. PipraPay redirects to dakkho://payment/status?order_id=X
 * 5. Deep link handled → PaymentSuccess/Failed/Cancel screen
 * 6. App calls GET /api/payments/status?order_id=X to verify
 */
@Singleton
class PipraPayHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefsHelper: EncryptedPrefsHelper
) {

    companion object {
        const val DEEP_LINK_FULL = "dakkho://payment/status"

        fun extractOrderIdFromDeepLink(uri: Uri): String? {
            return uri.getQueryParameter("order_id")
        }

        fun extractStatusFromDeepLink(uri: Uri): String? {
            return uri.getQueryParameter("status")
        }
    }

    fun launchPayment(paymentUrl: String) {
        val customTabsIntent = CustomTabsIntent.Builder()
            .setShowTitle(true)
            .setUrlBarHidingEnabled(true)
            .build()

        try {
            customTabsIntent.launchUrl(context, Uri.parse(paymentUrl))
        } catch (e: Exception) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    fun savePendingOrder(orderId: String) {
        prefsHelper.saveString("pending_order_id", orderId)
    }

    fun getPendingOrder(): String? {
        val orderId = prefsHelper.getString("pending_order_id", null)
        if (orderId != null) {
            prefsHelper.saveString("pending_order_id", "")
        }
        return orderId
    }

    fun getPaymentResult(uri: Uri): PaymentResult {
        val status = extractStatusFromDeepLink(uri) ?: return PaymentResult.UNKNOWN
        return when (status.lowercase()) {
            "success", "completed", "paid" -> PaymentResult.SUCCESS
            "failed", "error", "declined" -> PaymentResult.FAILED
            "cancelled", "canceled" -> PaymentResult.CANCELLED
            else -> PaymentResult.UNKNOWN
        }
    }

    enum class PaymentResult { SUCCESS, FAILED, CANCELLED, UNKNOWN }
}
