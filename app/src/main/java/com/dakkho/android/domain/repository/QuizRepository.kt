package com.dakkho.android.domain.repository

import com.dakkho.android.domain.model.Quiz
import com.dakkho.android.domain.model.QuizQuestion
import com.dakkho.android.domain.model.QuizSubmitResult

interface QuizRepository {

    suspend fun getQuizzesForCourse(courseId: String): Result<List<Quiz>>

    suspend fun getQuizDetail(courseId: String, quizId: String): Result<Pair<Quiz, List<QuizQuestion>>>

    suspend fun submitQuiz(quizId: String, answers: Map<String, String>): Result<QuizSubmitResult>
}
