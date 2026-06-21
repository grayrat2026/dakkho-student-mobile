package com.dakkho.android.data.api

import com.dakkho.android.domain.model.ApiResult
import com.dakkho.android.domain.model.CertificateDto
import com.dakkho.android.domain.model.CourseCompletionStatus
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CertificateApiService {

    @GET("api/certificates")
    suspend fun getCertificates(): Response<ApiResult<List<CertificateDto>>>

    @GET("api/courses/{id}")
    suspend fun getCourseCompletionStatus(
        @Path("id") courseId: String
    ): Response<ApiResult<CourseCompletionStatus>>
}
