package com.dakkho.android.data.repository

import com.dakkho.android.data.api.ProfileApiService
import com.dakkho.android.data.db.dao.WatchHistoryDao
import com.dakkho.android.data.db.EncryptedPrefsHelper
import com.dakkho.android.domain.model.ChangePasswordRequest
import com.dakkho.android.domain.model.DailyActivity
import com.dakkho.android.domain.model.LearningStats
import com.dakkho.android.domain.model.MonthlyProgress
import com.dakkho.android.domain.model.PaymentHistoryItem
import com.dakkho.android.domain.model.ReferralData
import com.dakkho.android.domain.model.ReferralHistoryItem
import com.dakkho.android.domain.model.ReferralStatus
import com.dakkho.android.domain.model.SubjectDistribution
import com.dakkho.android.domain.model.Subscription
import com.dakkho.android.domain.model.SubscriptionPlan
import com.dakkho.android.domain.model.SubscriptionPlanType
import com.dakkho.android.domain.model.SubscriptionStatus
import com.dakkho.android.domain.model.UpdateProfileRequest
import com.dakkho.android.domain.model.User
import com.dakkho.android.domain.repository.ProfileRepository
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val profileApiService: ProfileApiService,
    private val watchHistoryDao: WatchHistoryDao,
    private val encryptedPrefsHelper: EncryptedPrefsHelper
) : ProfileRepository {

    // ── Edit Profile (#65) ──

    override suspend fun updateProfile(
        fullName: String?,
        phone: String?,
        avatarUrl: String?,
        instituteId: String?,
        technology: String?
    ): Result<User> {
        return try {
            val request = UpdateProfileRequest(
                fullName = fullName,
                phone = phone,
                avatarUrl = avatarUrl,
                instituteId = instituteId,
                technology = technology
            )
            val response = profileApiService.updateProfile(request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    Result.success(mapUserDtoToDomain(body.data))
                } else {
                    Result.failure(Exception(body?.message ?: "Update profile failed"))
                }
            } else {
                Result.failure(Exception("Update profile failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Update profile error")
            Result.failure(e)
        }
    }

    // ── Change Password (#66) ──

    override suspend fun changePassword(
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
    ): Result<Unit> {
        return try {
            val request = ChangePasswordRequest(
                currentPassword = currentPassword,
                newPassword = newPassword,
                confirmPassword = confirmPassword
            )
            val response = profileApiService.changePassword(request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(body?.data?.message ?: "Change password failed"))
                }
            } else {
                Result.failure(Exception("Change password failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Change password error")
            Result.failure(e)
        }
    }

    // ── Learning Stats (#67) ──

    override suspend fun getLearningStats(): Result<LearningStats> {
        return try {
            val response = profileApiService.getLearningStats()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    Result.success(mapLearningStatsDtoToDomain(body.data))
                } else {
                    // Fallback to local data
                    getLocalLearningStats()
                }
            } else {
                getLocalLearningStats()
            }
        } catch (e: Exception) {
            Timber.e(e, "Get learning stats error, falling back to local")
            getLocalLearningStats()
        }
    }

    private suspend fun getLocalLearningStats(): Result<LearningStats> {
        return try {
            val userId = encryptedPrefsHelper.getUserId() ?: return Result.failure(
                Exception("Not logged in")
            )
            val history = watchHistoryDao.getWatchHistory(userId)
            val totalHours = history.map {
                (it.progressSeconds ?: 0) / 3600f
            }.sum()
            Result.success(
                LearningStats(
                    totalHoursWatched = totalHours,
                    totalLessonsCompleted = history.count { it.completed == true },
                    weeklyActivity = generateDefaultWeeklyActivity(),
                    monthlyProgress = generateDefaultMonthlyProgress()
                )
            )
        } catch (e: Exception) {
            Timber.e(e, "Local learning stats error")
            Result.failure(e)
        }
    }

    // ── Subscription (#68) ──

    override suspend fun getCurrentSubscription(): Result<Subscription> {
        return try {
            val response = profileApiService.getCurrentSubscription()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    Result.success(mapSubscriptionDtoToDomain(body.data))
                } else {
                    // Default free subscription
                    Result.success(
                        Subscription(
                            id = "free",
                            planName = "Free Plan",
                            planType = SubscriptionPlanType.FREE,
                            status = SubscriptionStatus.ACTIVE
                        )
                    )
                }
            } else {
                Result.success(
                    Subscription(
                        id = "free",
                        planName = "Free Plan",
                        planType = SubscriptionPlanType.FREE,
                        status = SubscriptionStatus.ACTIVE
                    )
                )
            }
        } catch (e: Exception) {
            Timber.e(e, "Get subscription error")
            Result.success(
                Subscription(
                    id = "free",
                    planName = "Free Plan",
                    planType = SubscriptionPlanType.FREE,
                    status = SubscriptionStatus.ACTIVE
                )
            )
        }
    }

    override suspend fun getSubscriptionPlans(): Result<List<SubscriptionPlan>> {
        return try {
            val response = profileApiService.getSubscriptionPlans()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    Result.success(body.data.map { mapSubscriptionPlanDtoToDomain(it) })
                } else {
                    Result.success(getDefaultPlans())
                }
            } else {
                Result.success(getDefaultPlans())
            }
        } catch (e: Exception) {
            Timber.e(e, "Get subscription plans error")
            Result.success(getDefaultPlans())
        }
    }

    override suspend fun subscribeToPlan(planId: String): Result<Subscription> {
        return try {
            val response = profileApiService.subscribeToPlan(planId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    Result.success(mapSubscriptionDtoToDomain(body.data))
                } else {
                    Result.failure(Exception(body?.message ?: "Subscribe failed"))
                }
            } else {
                Result.failure(Exception("Subscribe failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Subscribe error")
            Result.failure(e)
        }
    }

    override suspend fun cancelSubscription(): Result<Subscription> {
        return try {
            val response = profileApiService.cancelSubscription()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    Result.success(mapSubscriptionDtoToDomain(body.data))
                } else {
                    Result.failure(Exception(body?.message ?: "Cancel failed"))
                }
            } else {
                Result.failure(Exception("Cancel failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Cancel subscription error")
            Result.failure(e)
        }
    }

    override suspend fun getPaymentHistory(): Result<List<PaymentHistoryItem>> {
        return try {
            val response = profileApiService.getPaymentHistory()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    Result.success(body.data.map { mapPaymentHistoryDtoToDomain(it) })
                } else {
                    Result.success(emptyList())
                }
            } else {
                Result.success(emptyList())
            }
        } catch (e: Exception) {
            Timber.e(e, "Get payment history error")
            Result.success(emptyList())
        }
    }

    // ── Referral (#69) ──

    override suspend fun getReferralData(): Result<ReferralData> {
        return try {
            val response = profileApiService.getReferralData()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    Result.success(mapReferralDataDtoToDomain(body.data))
                } else {
                    // Default referral data with generated code
                    Result.success(generateDefaultReferralData())
                }
            } else {
                Result.success(generateDefaultReferralData())
            }
        } catch (e: Exception) {
            Timber.e(e, "Get referral data error")
            Result.success(generateDefaultReferralData())
        }
    }

    override suspend fun generateReferralCode(): Result<ReferralData> {
        return try {
            val response = profileApiService.generateReferralCode()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    Result.success(mapReferralDataDtoToDomain(body.data))
                } else {
                    Result.failure(Exception("Generate referral code failed"))
                }
            } else {
                Result.failure(Exception("Generate referral code failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Generate referral code error")
            Result.failure(e)
        }
    }

    // ── Mapper Functions ──

    private fun mapUserDtoToDomain(dto: com.dakkho.android.domain.model.UserDto): User {
        return User(
            id = dto.id,
            email = dto.email,
            fullName = dto.fullName,
            instituteId = dto.instituteId,
            technology = dto.technology,
            avatarUrl = dto.avatarUrl,
            role = dto.role,
            phone = dto.phone,
            isVerified = dto.isVerified ?: false,
            createdAt = dto.createdAt
        )
    }

    private fun mapLearningStatsDtoToDomain(dto: com.dakkho.android.domain.model.LearningStatsDto): LearningStats {
        return LearningStats(
            coursesEnrolled = dto.coursesEnrolled,
            coursesCompleted = dto.coursesCompleted,
            totalHoursWatched = dto.totalHoursWatched,
            totalLessonsCompleted = dto.totalLessonsCompleted,
            currentStreak = dto.currentStreak,
            longestStreak = dto.longestStreak,
            totalXp = dto.totalXp,
            achievementsUnlocked = dto.achievementsUnlocked,
            certificatesEarned = dto.certificatesEarned,
            weeklyActivity = dto.weeklyActivity?.map {
                DailyActivity(day = it.day, hoursWatched = it.hoursWatched, lessonsCompleted = it.lessonsCompleted)
            } ?: generateDefaultWeeklyActivity(),
            monthlyProgress = dto.monthlyProgress?.map {
                MonthlyProgress(month = it.month, coursesCompleted = it.coursesCompleted, hoursWatched = it.hoursWatched)
            } ?: generateDefaultMonthlyProgress(),
            subjectDistribution = dto.subjectDistribution?.map {
                SubjectDistribution(subject = it.subject, hours = it.hours, color = it.color)
            } ?: emptyList()
        )
    }

    private fun mapSubscriptionDtoToDomain(dto: com.dakkho.android.domain.model.SubscriptionDto): Subscription {
        return Subscription(
            id = dto.id,
            planName = dto.planName,
            planType = when (dto.planType.uppercase()) {
                "BASIC" -> SubscriptionPlanType.BASIC
                "PRO" -> SubscriptionPlanType.PRO
                "PREMIUM" -> SubscriptionPlanType.PREMIUM
                else -> SubscriptionPlanType.FREE
            },
            status = when (dto.status.uppercase()) {
                "ACTIVE" -> SubscriptionStatus.ACTIVE
                "EXPIRED" -> SubscriptionStatus.EXPIRED
                "CANCELLED" -> SubscriptionStatus.CANCELLED
                "TRIAL" -> SubscriptionStatus.TRIAL
                else -> SubscriptionStatus.INACTIVE
            },
            startDate = dto.startDate,
            endDate = dto.endDate,
            autoRenew = dto.autoRenew,
            price = dto.price,
            currency = dto.currency,
            features = dto.features ?: emptyList(),
            daysRemaining = dto.daysRemaining
        )
    }

    private fun mapSubscriptionPlanDtoToDomain(dto: com.dakkho.android.domain.model.SubscriptionPlanDto): SubscriptionPlan {
        return SubscriptionPlan(
            id = dto.id,
            name = dto.name,
            planType = when (dto.planType.uppercase()) {
                "BASIC" -> SubscriptionPlanType.BASIC
                "PRO" -> SubscriptionPlanType.PRO
                "PREMIUM" -> SubscriptionPlanType.PREMIUM
                else -> SubscriptionPlanType.FREE
            },
            price = dto.price,
            currency = dto.currency,
            billingCycle = dto.billingCycle,
            features = dto.features ?: emptyList(),
            isPopular = dto.isPopular,
            discountPercent = dto.discountPercent
        )
    }

    private fun mapPaymentHistoryDtoToDomain(dto: com.dakkho.android.domain.model.PaymentHistoryDto): PaymentHistoryItem {
        return PaymentHistoryItem(
            id = dto.id,
            amount = dto.amount,
            currency = dto.currency,
            status = dto.status,
            planName = dto.planName,
            paidAt = dto.paidAt
        )
    }

    private fun mapReferralDataDtoToDomain(dto: com.dakkho.android.domain.model.ReferralDataDto): ReferralData {
        return ReferralData(
            referralCode = dto.referralCode,
            referralLink = dto.referralLink,
            totalReferrals = dto.totalReferrals,
            successfulReferrals = dto.successfulReferrals,
            earnedCredits = dto.earnedCredits,
            pendingCredits = dto.pendingCredits,
            referralHistory = dto.referralHistory?.map {
                ReferralHistoryItem(
                    id = it.id,
                    referredName = it.referredName,
                    referredEmail = it.referredEmail,
                    status = when (it.status.uppercase()) {
                        "REGISTERED" -> ReferralStatus.REGISTERED
                        "ENROLLED" -> ReferralStatus.ENROLLED
                        "REWARDED" -> ReferralStatus.REWARDED
                        else -> ReferralStatus.PENDING
                    },
                    earnedCredits = it.earnedCredits,
                    date = it.date
                )
            } ?: emptyList()
        )
    }

    private fun generateDefaultReferralData(): ReferralData {
        val userId = encryptedPrefsHelper.getUserId() ?: "unknown"
        val code = "DAKKHO${userId.takeLast(6).uppercase()}"
        return ReferralData(
            referralCode = code,
            referralLink = "https://dakkho.com/ref/$code"
        )
    }

    private fun generateDefaultWeeklyActivity(): List<DailyActivity> {
        return listOf(
            DailyActivity(day = "শনি", hoursWatched = 0f, lessonsCompleted = 0),
            DailyActivity(day = "রবি", hoursWatched = 0f, lessonsCompleted = 0),
            DailyActivity(day = "সোম", hoursWatched = 0f, lessonsCompleted = 0),
            DailyActivity(day = "মঙ্গল", hoursWatched = 0f, lessonsCompleted = 0),
            DailyActivity(day = "বুধ", hoursWatched = 0f, lessonsCompleted = 0),
            DailyActivity(day = "বৃহঃ", hoursWatched = 0f, lessonsCompleted = 0),
            DailyActivity(day = "শুক্র", hoursWatched = 0f, lessonsCompleted = 0)
        )
    }

    private fun generateDefaultMonthlyProgress(): List<MonthlyProgress> {
        return listOf(
            MonthlyProgress(month = "জানু", coursesCompleted = 0, hoursWatched = 0f),
            MonthlyProgress(month = "ফেব্রু", coursesCompleted = 0, hoursWatched = 0f),
            MonthlyProgress(month = "মার্চ", coursesCompleted = 0, hoursWatched = 0f),
            MonthlyProgress(month = "এপ্রিল", coursesCompleted = 0, hoursWatched = 0f),
            MonthlyProgress(month = "মে", coursesCompleted = 0, hoursWatched = 0f),
            MonthlyProgress(month = "জুন", coursesCompleted = 0, hoursWatched = 0f)
        )
    }

    private fun getDefaultPlans(): List<SubscriptionPlan> {
        return listOf(
            SubscriptionPlan(
                id = "free",
                name = "Free",
                planType = SubscriptionPlanType.FREE,
                price = 0.0,
                billingCycle = "forever",
                features = listOf("5 free courses", "Basic video quality", "Community access")
            ),
            SubscriptionPlan(
                id = "basic",
                name = "Basic",
                planType = SubscriptionPlanType.BASIC,
                price = 299.0,
                billingCycle = "monthly",
                features = listOf("Unlimited courses", "HD video quality", "Download offline", "Email support"),
                isPopular = false
            ),
            SubscriptionPlan(
                id = "pro",
                name = "Pro",
                planType = SubscriptionPlanType.PRO,
                price = 599.0,
                billingCycle = "monthly",
                features = listOf("Everything in Basic", "Live classes", "Priority support", "Certificate access", "Quiz practice"),
                isPopular = true,
                discountPercent = 20
            ),
            SubscriptionPlan(
                id = "premium",
                name = "Premium",
                planType = SubscriptionPlanType.PREMIUM,
                price = 999.0,
                billingCycle = "monthly",
                features = listOf("Everything in Pro", "1-on-1 mentoring", "Job placement support", "Exclusive resources", "Lifetime access"),
                discountPercent = 30
            )
        )
    }
}
