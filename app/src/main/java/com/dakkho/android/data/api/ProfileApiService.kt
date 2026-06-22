package com.dakkho.android.data.api

import com.dakkho.android.data.api.ApiResult
import com.dakkho.android.domain.model.ChangePasswordRequest
import com.dakkho.android.domain.model.ChangePasswordResponse
import com.dakkho.android.domain.model.LearningStatsDto
import com.dakkho.android.domain.model.PaymentHistoryDto
import com.dakkho.android.domain.model.ReferralDataDto
import com.dakkho.android.domain.model.SubscriptionDto
import com.dakkho.android.domain.model.SubscriptionPlanDto
import com.dakkho.android.domain.model.UserDto
import com.dakkho.android.domain.model.UpdateProfileRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Phase 25: Profile Sub-pages API Service
 * Covers: EditProfile, ChangePassword, LearningStats, Subscription, Referral
 */
interface ProfileApiService {

    // ── Edit Profile (#65) ──

    @PUT("api/profile/update")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<ApiResult<UserDto>>

    @POST("api/profile/avatar")
    suspend fun uploadAvatar(@Body avatarData: Map<String, String>): Response<ApiResult<UserDto>>

    // ── Change Password (#66) ──

    @POST("api/profile/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<ApiResult<ChangePasswordResponse>>

    // ── Learning Stats (#67) ──

    @GET("api/profile/learning-stats")
    suspend fun getLearningStats(): Response<ApiResult<LearningStatsDto>>

    // ── Subscription (#68) ──

    @GET("api/profile/subscription")
    suspend fun getCurrentSubscription(): Response<ApiResult<SubscriptionDto>>

    @GET("api/profile/subscription/plans")
    suspend fun getSubscriptionPlans(): Response<ApiResult<List<SubscriptionPlanDto>>>

    @POST("api/profile/subscription/subscribe")
    suspend fun subscribeToPlan(@Query("plan_id") planId: String): Response<ApiResult<SubscriptionDto>>

    @POST("api/profile/subscription/cancel")
    suspend fun cancelSubscription(): Response<ApiResult<SubscriptionDto>>

    @GET("api/profile/subscription/payments")
    suspend fun getPaymentHistory(): Response<ApiResult<List<PaymentHistoryDto>>>

    // ── Referral (#69) ──

    @GET("api/profile/referral")
    suspend fun getReferralData(): Response<ApiResult<ReferralDataDto>>

    @POST("api/profile/referral/generate-code")
    suspend fun generateReferralCode(): Response<ApiResult<ReferralDataDto>>
}
