package com.dakkho.android.domain.repository

import com.dakkho.android.domain.model.Enrollment
import com.dakkho.android.domain.model.Payment
import com.dakkho.android.domain.model.WatchHistoryItem

interface EnrollmentRepository {

    suspend fun checkEnrollment(courseId: String): Result<Enrollment?>

    suspend fun getWatchHistory(): Result<List<WatchHistoryItem>>

    suspend fun getStreamSession(videoId: String): Result<String>

    suspend fun createPayment(courseId: String): Result<Payment>

    suspend fun getPaymentStatus(orderId: String): Result<Payment>
}
