package com.dakkho.android.domain.model

data class Payment(
    val orderId: String,
    val paymentUrl: String? = null,
    val amount: Double,
    val currency: String? = null,
    val status: String? = null,
    val transactionId: String? = null,
    val paidAt: String? = null
)
