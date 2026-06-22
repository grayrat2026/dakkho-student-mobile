package com.dakkho.android.data.api

import com.dakkho.android.data.api.ApiResult
import com.dakkho.android.domain.model.QuizDetailResponse
import com.dakkho.android.domain.model.QuizListResponse
import com.dakkho.android.domain.model.QuizSubmitRequest
import com.dakkho.android.domain.model.QuizSubmitResult
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface QuizApiService {

    @GET("student/quizzes/{courseId}")
    suspend fun getQuizzesForCourse(
        @Path("courseId") courseId: String
    ): Response<QuizListResponse>

    @GET("student/quizzes/{courseId}/{quizId}")
    suspend fun getQuizDetail(
        @Path("courseId") courseId: String,
        @Path("quizId") quizId: String
    ): Response<QuizDetailResponse>

    @POST("student/quizzes/{quizId}/submit")
    suspend fun submitQuiz(
        @Path("quizId") quizId: String,
        @Body request: QuizSubmitRequest
    ): Response<ApiResult<QuizSubmitResult>>
}
