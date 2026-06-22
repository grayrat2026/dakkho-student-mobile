package com.dakkho.android.data.api

import com.dakkho.android.data.api.ApiResult
import com.dakkho.android.domain.model.EnrollmentStatusDto
import com.dakkho.android.domain.model.PackageDto
import com.dakkho.android.domain.model.PaymentDto
import com.dakkho.android.domain.model.PaymentStatusDto
import com.dakkho.android.domain.model.StreamSessionDto
import com.dakkho.android.domain.model.WatchHistoryDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface EnrollmentApiService {

    @GET("api/enrollments/check")
    suspend fun checkEnrollment(@Query("course_id") courseId: String): Response<ApiResult<EnrollmentStatusDto>>

    @GET("api/packages/mine")
    suspend fun getMyPackages(): Response<ApiResult<List<PackageDto>>>

    @GET("api/watch-history")
    suspend fun getWatchHistory(): Response<ApiResult<List<WatchHistoryDto>>>

    @POST("api/video/stream/session/{id}")
    suspend fun getStreamSession(@Path("id") videoId: String): Response<ApiResult<StreamSessionDto>>

    @GET("api/payments/create")
    suspend fun createPayment(@Query("course_id") courseId: String): Response<ApiResult<PaymentDto>>

    @GET("api/payments/status")
    suspend fun getPaymentStatus(@Query("order_id") orderId: String): Response<ApiResult<PaymentStatusDto>>
}
