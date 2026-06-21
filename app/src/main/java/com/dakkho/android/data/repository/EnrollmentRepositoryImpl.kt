package com.dakkho.android.data.repository

import com.dakkho.android.data.api.EnrollmentApiService
import com.dakkho.android.data.db.EncryptedPrefsHelper
import com.dakkho.android.domain.model.Enrollment
import com.dakkho.android.domain.model.Payment
import com.dakkho.android.domain.model.WatchHistoryItem
import com.dakkho.android.domain.repository.EnrollmentRepository
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EnrollmentRepositoryImpl @Inject constructor(
    private val enrollmentApiService: EnrollmentApiService,
    private val encryptedPrefsHelper: EncryptedPrefsHelper
) : EnrollmentRepository {

    override suspend fun checkEnrollment(courseId: String): Result<Enrollment?> {
        return try {
            val response = enrollmentApiService.checkEnrollment(courseId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    val status = body.data
                    Result.success(
                        Enrollment(
                            id = status.enrollmentId ?: "",
                            userId = encryptedPrefsHelper.getUserId() ?: "",
                            courseId = courseId,
                            progress = status.progress ?: 0f,
                            isCompleted = (status.progress ?: 0f) >= 1f,
                            enrolledAt = status.enrolledAt
                        )
                    )
                } else {
                    Result.success(null)
                }
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Timber.e(e, "Check enrollment error")
            Result.failure(e)
        }
    }

    override suspend fun getWatchHistory(): Result<List<WatchHistoryItem>> {
        return try {
            val response = enrollmentApiService.getWatchHistory()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    Result.success(
                        body.data.map { dto ->
                            WatchHistoryItem(
                                id = dto.id,
                                videoId = dto.videoId,
                                courseId = dto.courseId,
                                progressSeconds = dto.progressSeconds ?: 0,
                                totalSeconds = dto.totalSeconds ?: 0,
                                completed = dto.completed ?: false,
                                lastWatchedAt = dto.lastWatchedAt
                            )
                        }
                    )
                } else {
                    Result.success(emptyList())
                }
            } else {
                Result.success(emptyList())
            }
        } catch (e: Exception) {
            Timber.e(e, "Get watch history error")
            Result.failure(e)
        }
    }

    override suspend fun getStreamSession(videoId: String): Result<String> {
        return try {
            val response = enrollmentApiService.getStreamSession(videoId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    Result.success(body.data.streamUrl)
                } else {
                    Result.failure(Exception(body?.message ?: "Failed to get stream session"))
                }
            } else {
                Result.failure(Exception("Stream session failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Get stream session error")
            Result.failure(e)
        }
    }

    override suspend fun createPayment(courseId: String): Result<Payment> {
        return try {
            val response = enrollmentApiService.createPayment(courseId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    val paymentDto = body.data
                    Result.success(
                        Payment(
                            orderId = paymentDto.orderId,
                            paymentUrl = paymentDto.paymentUrl,
                            amount = paymentDto.amount,
                            currency = paymentDto.currency
                        )
                    )
                } else {
                    Result.failure(Exception(body?.message ?: "Failed to create payment"))
                }
            } else {
                Result.failure(Exception("Create payment failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Create payment error")
            Result.failure(e)
        }
    }

    override suspend fun getPaymentStatus(orderId: String): Result<Payment> {
        return try {
            val response = enrollmentApiService.getPaymentStatus(orderId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    val statusDto = body.data
                    Result.success(
                        Payment(
                            orderId = statusDto.orderId,
                            status = statusDto.status,
                            transactionId = statusDto.transactionId,
                            paidAt = statusDto.paidAt,
                            amount = 0.0
                        )
                    )
                } else {
                    Result.failure(Exception(body?.message ?: "Failed to get payment status"))
                }
            } else {
                Result.failure(Exception("Payment status failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Get payment status error")
            Result.failure(e)
        }
    }
}
