package com.dakkho.android.domain.model

/**
 * Domain model for a course quiz.
 */
data class Quiz(
    val id: String,
    val courseId: String,
    val title: String,
    val description: String? = null,
    val timeLimitMinutes: Int? = null,
    val maxAttempts: Int = 1,
    val passingScore: Int = 50,
    val isActive: Boolean = true,
    val userAttemptCount: Int = 0,
    val bestPercentage: Double? = null,
    val canAttempt: Boolean = true
)

/**
 * Domain model for a quiz question (without correct answer — for display during quiz).
 */
data class QuizQuestion(
    val id: String,
    val quizId: String,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val orderNum: Int = 0,
    val correctOption: String? = null,
    val explanation: String? = null
)

/**
 * Domain model for a single quiz question with answer context (post-submission).
 */
data class QuizQuestionResult(
    val questionId: String,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val selected: String,
    val correct: String,
    val isCorrect: Boolean,
    val explanation: String? = null
)

/**
 * Domain model for a quiz attempt summary.
 */
data class QuizAttempt(
    val id: String,
    val quizId: String,
    val userId: String,
    val score: Int,
    val totalQuestions: Int,
    val percentage: Int,
    val passed: Boolean,
    val startedAt: String,
    val completedAt: String,
    val timeTaken: Int? = null
)

/**
 * Domain model for a user's answer submission.
 */
data class QuizAnswer(
    val questionId: String,
    val selected: String
)

/**
 * Domain model for quiz submission result.
 */
data class QuizSubmitResult(
    val quizId: String = "",
    val score: Int = 0,
    val totalQuestions: Int = 0,
    val percentage: Int = 0,
    val passed: Boolean = false,
    val answers: List<ScoredAnswerDto> = emptyList(),
    val attemptNumber: Int = 0,
    val maxAttempts: Int = 1,
    val canRetry: Boolean = false
)

/**
 * DTO for a scored answer from the API.
 */
data class ScoredAnswerDto(
    val questionId: String = "",
    val selected: String = "",
    val correct: String? = null,
    val isCorrect: Boolean = false
)

/**
 * Request body for submitting quiz answers.
 */
data class QuizSubmitRequest(
    val answers: List<QuizAnswer>
)

/**
 * Response for listing quizzes for a course.
 */
data class QuizListResponse(
    val quizzes: List<QuizDto> = emptyList()
)

/**
 * DTO for a quiz from the API.
 */
data class QuizDto(
    val id: String = "",
    val courseId: String = "",
    val title: String = "",
    val description: String? = null,
    val timeLimitMinutes: Int? = null,
    val maxAttempts: Int = 1,
    val passingScore: Int = 50,
    val isActive: Boolean = true,
    val userAttemptCount: Int = 0,
    val bestPercentage: Double? = null,
    val canAttempt: Boolean = true,
    val createdAt: String = "",
    val updatedAt: String = ""
) {
    fun toDomain() = Quiz(
        id = id,
        courseId = courseId,
        title = title,
        description = description,
        timeLimitMinutes = timeLimitMinutes,
        maxAttempts = maxAttempts,
        passingScore = passingScore,
        isActive = isActive,
        userAttemptCount = userAttemptCount,
        bestPercentage = bestPercentage,
        canAttempt = canAttempt
    )
}

/**
 * DTO for a quiz question from the API.
 */
data class QuizQuestionDto(
    val id: String = "",
    val quizId: String = "",
    val questionText: String = "",
    val optionA: String = "",
    val optionB: String = "",
    val optionC: String = "",
    val optionD: String = "",
    val orderNum: Int = 0,
    val correctOption: String? = null,
    val explanation: String? = null
) {
    fun toDomain() = QuizQuestion(
        id = id,
        quizId = quizId,
        questionText = questionText,
        optionA = optionA,
        optionB = optionB,
        optionC = optionC,
        optionD = optionD,
        orderNum = orderNum,
        correctOption = correctOption,
        explanation = explanation
    )
}

/**
 * Response for getting a quiz detail with questions.
 */
data class QuizDetailResponse(
    val quiz: QuizDto = QuizDto(),
    val questions: List<QuizQuestionDto> = emptyList(),
    val previousAttempts: List<QuizAttemptDto> = emptyList()
)

/**
 * DTO for a quiz attempt from the API.
 */
data class QuizAttemptDto(
    val id: String = "",
    val quizId: String = "",
    val score: Int = 0,
    val totalQuestions: Int = 0,
    val percentage: Int = 0,
    val passed: Boolean = false,
    val startedAt: String = "",
    val completedAt: String = "",
    val timeTaken: Int? = null
) {
    fun toDomain() = QuizAttempt(
        id = id,
        quizId = quizId,
        userId = "",
        score = score,
        totalQuestions = totalQuestions,
        percentage = percentage,
        passed = passed,
        startedAt = startedAt,
        completedAt = completedAt,
        timeTaken = timeTaken
    )
}
