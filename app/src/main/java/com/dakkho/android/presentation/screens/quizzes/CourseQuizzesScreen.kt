package com.dakkho.android.presentation.screens.quizzes

import android.view.HapticFeedbackConstants
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dakkho.android.domain.model.Quiz
import com.dakkho.android.domain.model.QuizQuestion
import com.dakkho.android.domain.model.QuizQuestionResult
import com.dakkho.android.presentation.theme.DeepBlue
import com.dakkho.android.presentation.theme.Green
import com.dakkho.android.presentation.theme.Neutral400
import com.dakkho.android.presentation.theme.SkyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseQuizzesScreen(
    courseId: String,
    courseTitle: String,
    onBackClick: () -> Unit,
    viewModel: CourseQuizzesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    androidx.compose.runtime.LaunchedEffect(courseId) {
        viewModel.initialize(courseId)
    }

    when (uiState.screenState) {
        QuizScreenState.LIST -> QuizListContent(
            uiState = uiState,
            courseTitle = courseTitle,
            onBackClick = onBackClick,
            onStartQuiz = { viewModel.startQuiz(it) },
            onRetry = { viewModel.loadQuizzes() }
        )
        QuizScreenState.PLAYING -> QuizPlayContent(
            uiState = uiState,
            onBackClick = { viewModel.backToList() },
            onSelectAnswer = { qId, opt -> viewModel.selectAnswer(qId, opt) },
            onNext = { viewModel.goToNextQuestion() },
            onPrevious = { viewModel.goToPreviousQuestion() },
            onSubmit = { viewModel.submitQuiz() },
            formatTime = { viewModel.formatTime(it) }
        )
        QuizScreenState.RESULT -> QuizResultContent(
            uiState = uiState,
            onBackToList = { viewModel.backToList() },
            onRetry = { viewModel.retryQuiz() },
            onToggleExplanation = { viewModel.toggleExplanation(it) }
        )
    }
}

// ─── Quiz List Screen ───

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuizListContent(
    uiState: CourseQuizzesUiState,
    courseTitle: String,
    onBackClick: () -> Unit,
    onStartQuiz: (Quiz) -> Unit,
    onRetry: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Quizzes",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        if (courseTitle.isNotEmpty()) {
                            Text(
                                text = courseTitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = Neutral400
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = SkyBlue)
            }
        } else if (uiState.error != null && uiState.quizzes.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(uiState.error ?: "Error", color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = onRetry) { Text("Retry") }
                }
            }
        } else if (uiState.quizzes.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.Quiz, null, Modifier.size(64.dp), Neutral400)
                    Spacer(Modifier.height(16.dp))
                    Text("No quizzes available", style = MaterialTheme.typography.titleMedium, color = Neutral400)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(Modifier.height(4.dp)) }
                items(uiState.quizzes.size) { index ->
                    val quiz = uiState.quizzes[index]
                    QuizCard(
                        quiz = quiz,
                        onStart = { onStartQuiz(quiz) }
                    )
                }
                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
private fun QuizCard(quiz: Quiz, onStart: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = quiz.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.weight(1f)
                )
                if (quiz.bestPercentage != null) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (quiz.bestPercentage >= quiz.passingScore) Green.copy(alpha = 0.15f) else Color(0xFFFFA726).copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "${quiz.bestPercentage!!.toInt()}%",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (quiz.bestPercentage >= quiz.passingScore) Green else Color(0xFFFFA726)
                        )
                    }
                }
            }

            if (quiz.description != null) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = quiz.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Neutral400,
                    maxLines = 2
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (quiz.timeLimitMinutes != null) {
                        Icon(Icons.Filled.Timer, null, Modifier.size(14.dp), Neutral400)
                        Spacer(Modifier.width(4.dp))
                        Text("${quiz.timeLimitMinutes} min", style = MaterialTheme.typography.labelSmall, color = Neutral400)
                        Spacer(Modifier.width(12.dp))
                    }
                    Text("${quiz.maxAttempts} attempts", style = MaterialTheme.typography.labelSmall, color = Neutral400)
                    Spacer(Modifier.width(12.dp))
                    Text("Pass: ${quiz.passingScore}%", style = MaterialTheme.typography.labelSmall, color = Neutral400)
                }
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = onStart,
                modifier = Modifier.fillMaxWidth(),
                enabled = quiz.canAttempt,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SkyBlue)
            ) {
                Text(
                    if (!quiz.canAttempt) "Max Attempts Reached" else if (quiz.userAttemptCount > 0) "Retry Quiz" else "Start Quiz",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        }
    }
}

// ─── Quiz Play Screen ───

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuizPlayContent(
    uiState: CourseQuizzesUiState,
    onBackClick: () -> Unit,
    onSelectAnswer: (String, String) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSubmit: () -> Unit,
    formatTime: (Long) -> String
) {
    val view = LocalView.current
    val questions = uiState.questions
    val currentIndex = uiState.currentIndex
    val currentQuestion = questions.getOrNull(currentIndex)

    Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Q${currentIndex + 1}/${questions.size}",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        if (uiState.isTimerRunning) {
                            Spacer(Modifier.width(16.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (uiState.remainingTimeMs < 30000) Color(0xFFFFEBEE) else SkyBlue.copy(alpha = 0.15f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Filled.Timer, null, Modifier.size(14.dp),
                                        if (uiState.remainingTimeMs < 30000) Color(0xFFE53935) else SkyBlue
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        formatTime(uiState.remainingTimeMs),
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (uiState.remainingTimeMs < 30000) Color(0xFFE53935) else SkyBlue
                                    )
                                }
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (currentQuestion == null) return@Scaffold

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Question number dots
            QuestionDots(
                totalQuestions = questions.size,
                currentIndex = currentIndex,
                answeredQuestions = uiState.selectedAnswers.keys,
                onSelect = { }
            )

            Spacer(Modifier.height(16.dp))

            // Question text
            Text(
                text = currentQuestion.questionText,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )

            Spacer(Modifier.height(20.dp))

            // Options
            val options = listOf(
                "A" to currentQuestion.optionA,
                "B" to currentQuestion.optionB,
                "C" to currentQuestion.optionC,
                "D" to currentQuestion.optionD
            )

            options.forEach { (key, text) ->
                val isSelected = uiState.selectedAnswers[currentQuestion.id] == key
                OptionCard(
                    optionKey = key,
                    optionText = text,
                    isSelected = isSelected,
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                        onSelectAnswer(currentQuestion.id, key)
                    }
                )
                Spacer(Modifier.height(10.dp))
            }

            Spacer(Modifier.weight(1f))

            // Navigation buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = onPrevious,
                    enabled = currentIndex > 0,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Previous")
                }

                if (currentIndex < questions.size - 1) {
                    Button(
                        onClick = onNext,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SkyBlue)
                    ) {
                        Text("Next")
                    }
                } else {
                    Button(
                        onClick = onSubmit,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Green),
                        enabled = !uiState.isSubmitting
                    ) {
                        if (uiState.isSubmitting) {
                            CircularProgressIndicator(Modifier.size(20.dp), Color.White, 2.dp)
                        } else {
                            Text("Submit")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuestionDots(
    totalQuestions: Int,
    currentIndex: Int,
    answeredQuestions: Set<String>,
    onSelect: (Int) -> Unit
) {
    if (totalQuestions <= 15) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(totalQuestions) { index ->
                val isCurrent = index == currentIndex
                val isAnswered = index < totalQuestions // simplified
                Box(
                    modifier = Modifier
                        .size(if (isCurrent) 28.dp else 24.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isCurrent -> SkyBlue
                                isAnswered -> SkyBlue.copy(alpha = 0.3f)
                                else -> Neutral400.copy(alpha = 0.3f)
                            }
                        )
                        .clickable { onSelect(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${index + 1}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = if (isCurrent) Color.White else Neutral400
                    )
                }
            }
        }
    }
}

@Composable
private fun OptionCard(
    optionKey: String,
    optionText: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) SkyBlue.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface,
        label = "optionBg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) SkyBlue else Neutral400.copy(alpha = 0.3f),
        label = "optionBorder"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = bgColor
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(28.dp),
                shape = CircleShape,
                color = if (isSelected) SkyBlue else Neutral400.copy(alpha = 0.2f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = optionKey,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isSelected) Color.White else Neutral400
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = optionText,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// ─── Quiz Result Screen ───

@Composable
private fun QuizResultContent(
    uiState: CourseQuizzesUiState,
    onBackToList: () -> Unit,
    onRetry: () -> Unit,
    onToggleExplanation: (Int) -> Unit
) {
    val result = uiState.quizResult ?: return

    Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { Text("Quiz Result") },
                navigationIcon = {
                    IconButton(onClick = onBackToList) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Score circle
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ScoreCircle(
                        percentage = result.percentage,
                        passed = result.passed
                    )
                }
            }

            // Stats row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem("Score", "${result.score}/${result.totalQuestions}")
                    StatItem("Attempt", "#${result.attemptNumber}")
                    StatItem("Status", if (result.passed) "Passed" else "Failed")
                }
            }

            item { Spacer(Modifier.height(20.dp)) }

            // Action buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (result.canRetry) {
                        OutlinedButton(
                            onClick = onRetry,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Retry")
                        }
                    }
                    Button(
                        onClick = onBackToList,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SkyBlue)
                    ) {
                        Text("All Quizzes")
                    }
                }
            }

            item { Spacer(Modifier.height(20.dp)) }

            // Question-by-question breakdown
            item {
                Text(
                    "Answer Review",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(Modifier.height(12.dp))
            }

            items(uiState.questionResults.size) { index ->
                val qResult = uiState.questionResults[index]
                QuestionResultCard(
                    index = index,
                    result = qResult,
                    isExplanationExpanded = uiState.showExplanationIndex == index,
                    onToggleExplanation = { onToggleExplanation(index) }
                )
                Spacer(Modifier.height(10.dp))
            }

            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}

@Composable
private fun ScoreCircle(percentage: Int, passed: Boolean) {
    val scoreColor = if (passed) Green else Color(0xFFE53935)
    Box(contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(140.dp)) {
            val strokeWidth = 10.dp.toPx()
            drawArc(
                color = Neutral400.copy(alpha = 0.2f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                size = Size(size.width - strokeWidth, size.height - strokeWidth),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            drawArc(
                color = scoreColor,
                startAngle = -90f,
                sweepAngle = (percentage / 100f) * 360f,
                useCenter = false,
                topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                size = Size(size.width - strokeWidth, size.height - strokeWidth),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "$percentage%",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp
                ),
                color = scoreColor
            )
            Text(
                if (passed) "PASSED" else "FAILED",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = scoreColor
            )
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        Text(label, style = MaterialTheme.typography.labelSmall, color = Neutral400)
    }
}

@Composable
private fun QuestionResultCard(
    index: Int,
    result: QuizQuestionResult,
    isExplanationExpanded: Boolean,
    onToggleExplanation: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (result.isCorrect) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                    contentDescription = null,
                    tint = if (result.isCorrect) Green else Color(0xFFE53935),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Q${index + 1}",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (result.isCorrect) Green else Color(0xFFE53935)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    result.questionText,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    modifier = Modifier.weight(1f)
                )
            }

            if (!result.isCorrect) {
                Spacer(Modifier.height(6.dp))
                Text(
                    "Your answer: ${result.selected} | Correct: ${result.correct}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Neutral400
                )
            }

            if (result.explanation != null) {
                Spacer(Modifier.height(6.dp))
                TextButton(onClick = onToggleExplanation) {
                    Text(
                        if (isExplanationExpanded) "Hide explanation" else "Show explanation",
                        style = MaterialTheme.typography.labelSmall,
                        color = SkyBlue
                    )
                }
                if (isExplanationExpanded) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SkyBlue.copy(alpha = 0.08f)
                    ) {
                        Text(
                            result.explanation,
                            modifier = Modifier.padding(10.dp),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

// Extension property for convenience
private val CourseQuizzesUiState.currentIndex: Int get() = currentQuestionIndex
