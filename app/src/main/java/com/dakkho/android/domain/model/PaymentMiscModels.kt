package com.dakkho.android.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// ── Phase 29: Payment #102-104 + Misc #105-109 + Error #110-111 Domain Models ──

// ════════════════════════════════════════════════════
// #102-104: Payment Screens (Success / Failed / Cancel)
// ════════════════════════════════════════════════════

data class PaymentReceipt(
    val orderId: String,
    val courseId: String,
    val courseTitle: String,
    val amount: Double,
    val currency: String = "BDT",
    val paymentMethod: String = "",
    val transactionId: String = "",
    val paidAt: String = "",
    val status: PaymentResultStatus = PaymentResultStatus.SUCCESS
)

enum class PaymentResultStatus {
    SUCCESS, FAILED, CANCELLED
}

data class PricingPlan(
    val id: String,
    val name: String,
    val price: Double,
    val currency: String = "BDT",
    val billingPeriod: String = "",  // "monthly", "yearly", "lifetime"
    val features: List<String> = emptyList(),
    val isPopular: Boolean = false,
    val isCurrent: Boolean = false,
    val discount: Int = 0  // percentage off
)

data class PricingData(
    val plans: List<PricingPlan> = emptyList(),
    val currentPlanId: String? = null
)

// ════════════════════════════════════════════════════
// #105: Changelog
// ════════════════════════════════════════════════════

data class ChangelogEntry(
    val version: String,
    val releaseDate: String,
    val features: List<String> = emptyList(),
    val bugFixes: List<String> = emptyList(),
    val improvements: List<String> = emptyList(),
    val isCurrentVersion: Boolean = false
)

// ════════════════════════════════════════════════════
// #106: Maintenance
// ════════════════════════════════════════════════════

data class MaintenanceInfo(
    val isActive: Boolean = false,
    val title: String = "",
    val message: String = "",
    val estimatedReturn: String = "",  // e.g., "২ ঘন্টা"
    val returnTimestamp: String = "",
    val showProgress: Boolean = false,
    val progressPercent: Int = 0
)

// ════════════════════════════════════════════════════
// #107-109: Terms & Privacy (shortened, Markdown)
// ════════════════════════════════════════════════════

data class LegalDocument(
    val title: String,
    val content: String,  // Markdown content
    val lastUpdated: String = ""
)

// ════════════════════════════════════════════════════
// #110-111: Error Screens
// ════════════════════════════════════════════════════

data class ErrorPageData(
    val errorCode: Int = 404,
    val title: String = "",
    val message: String = "",
    val showRetry: Boolean = false,
    val supportLink: String = ""
)

// ════════════════════════════════════════════════════
// DTOs
// ════════════════════════════════════════════════════

@JsonClass(generateAdapter = true)
data class PaymentReceiptDto(
    @Json(name = "order_id") val orderId: String,
    @Json(name = "course_id") val courseId: String,
    @Json(name = "course_title") val courseTitle: String,
    @Json(name = "amount") val amount: Double,
    @Json(name = "currency") val currency: String = "BDT",
    @Json(name = "payment_method") val paymentMethod: String = "",
    @Json(name = "transaction_id") val transactionId: String = "",
    @Json(name = "paid_at") val paidAt: String = "",
    @Json(name = "status") val status: String = "success"
) {
    fun toDomain(): PaymentReceipt = PaymentReceipt(
        orderId = orderId, courseId = courseId, courseTitle = courseTitle,
        amount = amount, currency = currency, paymentMethod = paymentMethod,
        transactionId = transactionId, paidAt = paidAt,
        status = when (status) {
            "success" -> PaymentResultStatus.SUCCESS
            "failed" -> PaymentResultStatus.FAILED
            "cancelled" -> PaymentResultStatus.CANCELLED
            else -> PaymentResultStatus.FAILED
        }
    )
}

@JsonClass(generateAdapter = true)
data class PricingPlanDto(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "price") val price: Double,
    @Json(name = "currency") val currency: String = "BDT",
    @Json(name = "billing_period") val billingPeriod: String = "",
    @Json(name = "features") val features: List<String> = emptyList(),
    @Json(name = "is_popular") val isPopular: Boolean = false,
    @Json(name = "is_current") val isCurrent: Boolean = false,
    @Json(name = "discount") val discount: Int = 0
) {
    fun toDomain(): PricingPlan = PricingPlan(
        id = id, name = name, price = price, currency = currency,
        billingPeriod = billingPeriod, features = features,
        isPopular = isPopular, isCurrent = isCurrent, discount = discount
    )
}

@JsonClass(generateAdapter = true)
data class ChangelogEntryDto(
    @Json(name = "version") val version: String,
    @Json(name = "release_date") val releaseDate: String,
    @Json(name = "features") val features: List<String> = emptyList(),
    @Json(name = "bug_fixes") val bugFixes: List<String> = emptyList(),
    @Json(name = "improvements") val improvements: List<String> = emptyList(),
    @Json(name = "is_current_version") val isCurrentVersion: Boolean = false
) {
    fun toDomain(): ChangelogEntry = ChangelogEntry(
        version = version, releaseDate = releaseDate,
        features = features, bugFixes = bugFixes,
        improvements = improvements, isCurrentVersion = isCurrentVersion
    )
}

@JsonClass(generateAdapter = true)
data class MaintenanceInfoDto(
    @Json(name = "is_active") val isActive: Boolean = false,
    @Json(name = "title") val title: String = "",
    @Json(name = "message") val message: String = "",
    @Json(name = "estimated_return") val estimatedReturn: String = "",
    @Json(name = "return_timestamp") val returnTimestamp: String = "",
    @Json(name = "show_progress") val showProgress: Boolean = false,
    @Json(name = "progress_percent") val progressPercent: Int = 0
) {
    fun toDomain(): MaintenanceInfo = MaintenanceInfo(
        isActive = isActive, title = title, message = message,
        estimatedReturn = estimatedReturn, returnTimestamp = returnTimestamp,
        showProgress = showProgress, progressPercent = progressPercent
    )
}

@JsonClass(generateAdapter = true)
data class CreatePaymentRequest(
    @Json(name = "course_id") val courseId: String,
    @Json(name = "package_id") val packageId: String? = null
)
