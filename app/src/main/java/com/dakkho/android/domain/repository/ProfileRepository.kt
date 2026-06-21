package com.dakkho.android.domain.repository

import com.dakkho.android.domain.model.LearningStats
import com.dakkho.android.domain.model.PaymentHistoryItem
import com.dakkho.android.domain.model.ReferralData
import com.dakkho.android.domain.model.Subscription
import com.dakkho.android.domain.model.SubscriptionPlan
import com.dakkho.android.domain.model.User

/**
 * Phase 25: Profile Sub-pages Repository
 * Covers: EditProfile, ChangePassword, LearningStats, Subscription, Referral
 */
interface ProfileRepository {

    // ── Edit Profile (#65) ──

    suspend fun updateProfile(
        fullName: String?,
        phone: String?,
        avatarUrl: String?,
        instituteId: String?,
        technology: String?
    ): Result<User>

    // ── Change Password (#66) ──

    suspend fun changePassword(
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
    ): Result<Unit>

    // ── Learning Stats (#67) ──

    suspend fun getLearningStats(): Result<LearningStats>

    // ── Subscription (#68) ──

    suspend fun getCurrentSubscription(): Result<Subscription>

    suspend fun getSubscriptionPlans(): Result<List<SubscriptionPlan>>

    suspend fun subscribeToPlan(planId: String): Result<Subscription>

    suspend fun cancelSubscription(): Result<Subscription>

    suspend fun getPaymentHistory(): Result<List<PaymentHistoryItem>>

    // ── Referral (#69) ──

    suspend fun getReferralData(): Result<ReferralData>

    suspend fun generateReferralCode(): Result<ReferralData>
}
