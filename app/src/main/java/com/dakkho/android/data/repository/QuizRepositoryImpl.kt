package com.dakkho.android.data.repository

import com.dakkho.android.data.api.QuizApiService
import com.dakkho.android.domain.model.Quiz
import com.dakkho.android.domain.model.QuizAnswer
import com.dakkho.android.domain.model.QuizQuestion
import com.dakkho.android.domain.model.QuizSubmitRequest
import com.dakkho.android.domain.model.QuizSubmitResult
import com.dakkho.android.domain.repository.QuizRepository
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizRepositoryImpl @Inject constructor(
    private val quizApiService: QuizApiService
) : QuizRepository {

    override suspend fun getQuizzesForCourse(courseId: String): Result<List<Quiz>> {
        return try {
            val response = quizApiService.getQuizzesForCourse(courseId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val quizzes = body.quizzes.map { it.toDomain() }
                    Result.success(quizzes)
                } else {
                    Result.success(emptyList())
                }
            } else {
                Result.failure(Exception("Failed to load quizzes: ${response.code()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Get quizzes for course error")
            Result.failure(e)
        }
    }

    override suspend fun getQuizDetail(
        courseId: String,
        quizId: String
    ): Result<Pair<Quiz, List<QuizQuestion>>> {
        return try {
            val response = quizApiService.getQuizDetail(courseId, quizId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val quiz = body.quiz.toDomain()
                    val questions = body.questions.map { it.toDomain() }
                    Result.success(Pair(quiz, questions))
                } else {
                    Result.failure(Exception("Quiz not found"))
                }
            } else {
                Result.failure(Exception("Failed to load quiz: ${response.code()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Get quiz detail error")
            Result.failure(e)
        }
    }

    override suspend fun submitQuiz(
        quizId: String,
        answers: Map<String, String>
    ): Result<QuizSubmitResult> {
        return try {
            val answerList = answers.map { (questionId, selected) ->
                QuizAnswer(questionId = questionId, selected = selected)
            }
            val request = QuizSubmitRequest(answers = answerList)
            val response = quizApiService.submitQuiz(quizId, request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body?.message ?: "Failed to submit quiz"))
                }
            } else {
                Result.failure(Exception("Submit quiz failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Submit quiz error")
            Result.failure(e)
        }
    }
}
