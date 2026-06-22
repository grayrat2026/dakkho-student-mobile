package com.dakkho.android.data.api

import com.dakkho.android.data.api.ApiResult
import com.dakkho.android.domain.model.ExamModels
import com.dakkho.android.domain.model.ExamResultDto
import com.dakkho.android.domain.model.ExamScheduleDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Phase 28: Exam API Service #89-93
 * Covers: Exam Prep, Schedule, Results, Practice, Tips
 */
interface ExamApiService {

    // ── #89: Exam Prep ──

    @GET("api/exam/prep")
    suspend fun getExamPrepInfo(
        @Query("department") department: String,
        @Query("semester") semester: Int
    ): Response<ApiResult<ExamModels.ExamPrepInfo>>

    // ── #90: Exam Schedule ──

    @GET("api/exam/schedule")
    suspend fun getExamSchedule(
        @Query("department") department: String,
        @Query("semester") semester: Int
    ): Response<ApiResult<List<ExamScheduleDto>>>

    // ── #91: Exam Results ──

    @GET("api/exam/results")
    suspend fun getExamResults(): Response<ApiResult<List<ExamResultDto>>>

    @GET("api/exam/results/{semester}")
    suspend fun getSemesterResult(@Path("semester") semester: Int): Response<ApiResult<ExamResultDto>>

    // ── #92: Exam Practice ──

    @GET("api/exam/practice/tests")
    suspend fun getPracticeTests(
        @Query("subject") subject: String? = null
    ): Response<ApiResult<List<ExamModels.PracticeTest>>>

    @GET("api/exam/practice/test/{id}/questions")
    suspend fun getPracticeQuestions(@Path("id") testId: String): Response<ApiResult<List<ExamModels.PracticeQuestion>>>

    @GET("api/exam/practice/previous-year")
    suspend fun getPreviousYearQuestions(
        @Query("year") year: String,
        @Query("subject") subject: String? = null
    ): Response<ApiResult<List<ExamModels.PracticeQuestion>>>

    // ── #93: Exam Tips ──

    @GET("api/exam/tips")
    suspend fun getExamTips(): Response<ApiResult<List<ExamModels.ExamTip>>>
}
