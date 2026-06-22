package com.dakkho.android.data.api

import com.dakkho.android.data.api.ApiResult
import com.dakkho.android.domain.model.ChangelogEntryDto
import com.dakkho.android.domain.model.CreatePaymentRequest
import com.dakkho.android.domain.model.MaintenanceInfoDto
import com.dakkho.android.domain.model.PaymentDto
import com.dakkho.android.domain.model.PaymentReceiptDto
import com.dakkho.android.domain.model.PaymentStatusDto
import com.dakkho.android.domain.model.PricingPlanDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Phase 29: Payment & Misc API Service #102-109
 * Covers: Payment (Success/Failed/Cancel), Pricing, Changelog, Maintenance
 */
interface PaymentMiscApiService {

    // ── #102-104: Payment ──

    @POST("api/payments/create")
    suspend fun createPayment(@Body request: CreatePaymentRequest): Response<ApiResult<PaymentDto>>

    @GET("api/payments/status")
    suspend fun getPaymentStatus(@Query("order_id") orderId: String): Response<ApiResult<PaymentStatusDto>>

    @GET("api/payments/receipt")
    suspend fun getPaymentReceipt(@Query("order_id") orderId: String): Response<ApiResult<PaymentReceiptDto>>

    // ── #104: Pricing ──

    @GET("api/packages")
    suspend fun getPricingPlans(): Response<ApiResult<List<PricingPlanDto>>>

    // ── #105: Changelog ──

    @GET("api/changelog")
    suspend fun getChangelog(): Response<ApiResult<List<ChangelogEntryDto>>>

    // ── #106: Maintenance ──

    @GET("api/maintenance/status")
    suspend fun getMaintenanceStatus(): Response<ApiResult<MaintenanceInfoDto>>
}
