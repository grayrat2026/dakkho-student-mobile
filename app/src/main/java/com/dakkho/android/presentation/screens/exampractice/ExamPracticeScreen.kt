package com.dakkho.android.presentation.screens.exampractice

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.dakkho.android.domain.model.ExamModels.PracticeQuestion
import com.dakkho.android.domain.model.ExamModels.PracticeTest
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.SkyBlue
import com.dakkho.android.presentation.theme.Green

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamPracticeScreen(
    onBackClick: () -> Unit,
    viewModel: ExamPracticeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Hide system bars in quiz mode
    val view = LocalView.current
    DisposableEffect(uiState.phase) {
        if (!view.isInEditMode) {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                val controller = WindowInsetsControllerCompat(window, view)
                if (uiState.phase == PracticePhase.QUIZ) {
                    controller.hide(WindowInsetsCompat.Type.systemBars())
                    controller.systemBarsBehavior =
                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                } else {
                    controller.show(WindowInsetsCompat.Type.systemBars())
                }
            }
        }
        onDispose { }
    }

    when (uiState.phase) {
        PracticePhase.LIST -> {
            PracticeTestList(
                uiState = uiState,
                onStartTest = { viewModel.startTest(it) },
                onBackClick = onBackClick,
                onRetry = { viewModel.retry() },
                toBengaliNumber = { viewModel.toBengaliNumber(it) }
            )
        }
        PracticePhase.QUIZ -> {
            QuizMode(
                uiState = uiState,
                onSelectAnswer = { viewModel.selectAnswer(it) },
                onNext = { viewModel.goToNextQuestion() },
                onPrevious = { viewModel.goToPreviousQuestion() },
                onGoToQuestion = { viewModel.goToQuestion(it) },
                onSubmit = { viewModel.submitTest() },
                formatTime = { viewModel.formatTime(it) },
                toBengaliNumber = { viewModel.toBengaliNumber(it) }
            )
        }
        PracticePhase.RESULT -> {
            ResultScreen(
                uiState = uiState,
                onBackToList = { viewModel.backToList() },
                onToggleReview = { viewModel.toggleReview() },
                toBengaliNumber = { viewModel.toBengaliNumber(it) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PracticeTestList(
    uiState: ExamPracticeUiState,
    onStartTest: (PracticeTest) -> Unit,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
    toBengaliNumber: (Number) -> String
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "পরীক্ষার অনুশীলন",
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "ফিরে যান"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = SkyBlue)
                }
            }
            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = uiState.error!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(DesignToken.Spacing.md))
                        OutlinedButton(onClick = onRetry) {
                            Text("আবার চেষ্টা করুন")
                        }
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(
                        horizontal = DesignToken.Spacing.md,
                        vertical = DesignToken.Spacing.sm
                    ),
                    verticalArrangement = Arrangement.spacedBy(DesignToken.Spacing.md)
                ) {
                    items(uiState.tests, key = { it.id }) { test ->
                        PracticeTestCard(
                            test = test,
                            onStart = { onStartTest(test) },
                            toBengaliNumber = toBengaliNumber
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PracticeTestCard(
    test: PracticeTest,
    onStart: () -> Unit,
    toBengaliNumber: (Number) -> String
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignToken.Spacing.md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = test.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = test.subject,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SkyBlue,
                        fontWeight = FontWeight.Medium
                    )
                }
                Icon(
                    imageVector = Icons.Default.Quiz,
                    contentDescription = null,
                    tint = SkyBlue.copy(alpha = 0.6f),
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(DesignToken.Spacing.sm))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(DesignToken.Spacing.md)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${toBengaliNumber(test.duration)} মিনিট",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${toBengaliNumber(test.totalQuestions)} প্রশ্ন",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${toBengaliNumber(test.marks)} নম্বর",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(DesignToken.Spacing.md))

            Button(
                onClick = onStart,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(DesignToken.Spacing.sm),
                colors = ButtonDefaults.buttonColors(containerColor = SkyBlue)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "শুরু করুন",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun QuizMode(
    uiState: ExamPracticeUiState,
    onSelectAnswer: (Int) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onGoToQuestion: (Int) -> Unit,
    onSubmit: () -> Unit,
    formatTime: (Int) -> String,
    toBengaliNumber: (Number) -> String
) {
    val currentQuestion = uiState.questions.getOrNull(uiState.currentQuestionIndex) ?: return
    val totalQuestions = uiState.questions.size
    val timerColor = if (uiState.remainingSeconds < 300) Color(0xFFEF4444) else SkyBlue

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top bar: timer + question counter
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                .padding(horizontal = DesignToken.Spacing.md, vertical = DesignToken.Spacing.sm),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = timerColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = formatTime(uiState.remainingSeconds),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = timerColor
                )
            }

            Text(
                text = "${toBengaliNumber(uiState.currentQuestionIndex + 1)}/${toBengaliNumber(totalQuestions)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            OutlinedButton(
                onClick = onSubmit,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444))
            ) {
                Text("জমা দিন", fontSize = 12.sp)
            }
        }

        // Progress bar
        androidx.compose.material3.LinearProgressIndicator(
            progress = { (uiState.currentQuestionIndex + 1).toFloat() / totalQuestions.toFloat() },
            modifier = Modifier.fillMaxWidth(),
            color = SkyBlue,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        // Question content
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(DesignToken.Spacing.md),
            verticalArrangement = Arrangement.spacedBy(DesignToken.Spacing.md)
        ) {
            // Question number and text
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(DesignToken.Spacing.md)
                    ) {
                        Text(
                            text = "প্রশ্ন ${toBengaliNumber(uiState.currentQuestionIndex + 1)}",
                            style = MaterialTheme.typography.labelLarge,
                            color = SkyBlue,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = currentQuestion.question,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 24.sp
                        )
                    }
                }
            }

            // Options
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(DesignToken.Spacing.sm)
                ) {
                    currentQuestion.options.forEachIndexed { optionIndex, option ->
                        val isSelected = uiState.selectedAnswers[uiState.currentQuestionIndex] == optionIndex
                        val optionLabel = when (optionIndex) {
                            0 -> "A"
                            1 -> "B"
                            2 -> "C"
                            3 -> "D"
                            else -> ""
                        }

                        OptionCard(
                            label = optionLabel,
                            text = option,
                            isSelected = isSelected,
                            onClick = { onSelectAnswer(optionIndex) }
                        )
                    }
                }
            }
        }

        // Bottom navigation
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                .padding(horizontal = DesignToken.Spacing.md, vertical = DesignToken.Spacing.sm)
        ) {
            // Previous / Next buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = onPrevious,
                    enabled = uiState.currentQuestionIndex > 0,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("পূর্ববর্তী")
                }

                Button(
                    onClick = if (uiState.currentQuestionIndex < totalQuestions - 1) onNext else onSubmit,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SkyBlue)
                ) {
                    Text(
                        if (uiState.currentQuestionIndex < totalQuestions - 1) "পরবর্তী" else "জমা দিন"
                    )
                }
            }

            Spacer(modifier = Modifier.height(DesignToken.Spacing.sm))

            // Question dots navigation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                uiState.questions.indices.forEach { index ->
                    val isAnswered = index in uiState.selectedAnswers
                    val isCurrent = index == uiState.currentQuestionIndex

                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isCurrent -> SkyBlue
                                    isAnswered -> Green.copy(alpha = 0.7f)
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                            )
                            .border(
                                width = if (isCurrent) 2.dp else 0.dp,
                                color = if (isCurrent) SkyBlue else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onGoToQuestion(index) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = toBengaliNumber(index + 1),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isCurrent) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OptionCard(
    label: String,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) SkyBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    val bgColor = if (isSelected) SkyBlue.copy(alpha = 0.1f) else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(DesignToken.Spacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = SkyBlue,
                unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$label. $text",
            style = MaterialTheme.typography.bodyLarge,
            color = if (isSelected) SkyBlue else MaterialTheme.colorScheme.onSurface,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ResultScreen(
    uiState: ExamPracticeUiState,
    onBackToList: () -> Unit,
    onToggleReview: () -> Unit,
    toBengaliNumber: (Number) -> String
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "পরীক্ষার ফলাফল",
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackToList) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "ফিরে যান"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                horizontal = DesignToken.Spacing.md,
                vertical = DesignToken.Spacing.sm
            ),
            verticalArrangement = Arrangement.spacedBy(DesignToken.Spacing.md)
        ) {
            // Score card
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(DesignToken.Spacing.lg),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = toBengaliNumber(uiState.score),
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.Bold,
                            color = SkyBlue
                        )
                        Text(
                            text = "মোট নম্বর",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )

                        Spacer(modifier = Modifier.height(DesignToken.Spacing.md))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatItem(
                                icon = Icons.Default.CheckCircle,
                                label = "সঠিক",
                                value = toBengaliNumber(uiState.correctCount),
                                color = Green
                            )
                            StatItem(
                                icon = Icons.Default.Cancel,
                                label = "ভুল",
                                value = toBengaliNumber(uiState.wrongCount),
                                color = Color(0xFFEF4444)
                            )
                            StatItem(
                                icon = Icons.Default.AccessTime,
                                label = "বাদ",
                                value = toBengaliNumber(uiState.skippedCount),
                                color = Color(0xFFF97316)
                            )
                        }
                    }
                }
            }

            // Action buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(DesignToken.Spacing.sm)
                ) {
                    OutlinedButton(
                        onClick = onToggleReview,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (uiState.showReview) "উত্তর লুকান" else "উত্তর পর্যালোচনা")
                    }
                    Button(
                        onClick = onBackToList,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SkyBlue)
                    ) {
                        Text("তালিকায় ফিরুন")
                    }
                }
            }

            // Review answers
            if (uiState.showReview) {
                items(uiState.questions.size) { index ->
                    val question = uiState.questions[index]
                    val selectedAnswer = uiState.selectedAnswers[index]
                    ReviewQuestionCard(
                        questionIndex = index,
                        question = question,
                        selectedAnswer = selectedAnswer,
                        toBengaliNumber = toBengaliNumber
                    )
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun ReviewQuestionCard(
    questionIndex: Int,
    question: PracticeQuestion,
    selectedAnswer: Int?,
    toBengaliNumber: (Number) -> String
) {
    val isCorrect = selectedAnswer == question.correctOptionIndex
    val isSkipped = selectedAnswer == null

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignToken.Spacing.md)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                    contentDescription = null,
                    tint = when {
                        isCorrect -> Green
                        isSkipped -> Color(0xFFF97316)
                        else -> Color(0xFFEF4444)
                    },
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "প্রশ্ন ${toBengaliNumber(questionIndex + 1)}",
                    style = MaterialTheme.typography.labelLarge,
                    color = SkyBlue,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = question.question,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            question.options.forEachIndexed { optionIndex, option ->
                val isThisCorrect = optionIndex == question.correctOptionIndex
                val isThisSelected = optionIndex == selectedAnswer
                val optionLabel = when (optionIndex) {
                    0 -> "A"
                    1 -> "B"
                    2 -> "C"
                    3 -> "D"
                    else -> ""
                }

                val optionColor = when {
                    isThisCorrect -> Green
                    isThisSelected && !isThisCorrect -> Color(0xFFEF4444)
                    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                }

                val bgColor = when {
                    isThisCorrect -> Green.copy(alpha = 0.1f)
                    isThisSelected && !isThisCorrect -> Color(0xFFEF4444).copy(alpha = 0.1f)
                    else -> Color.Transparent
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(bgColor)
                        .padding(vertical = 4.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$optionLabel. $option",
                        style = MaterialTheme.typography.bodySmall,
                        color = optionColor,
                        fontWeight = if (isThisCorrect || isThisSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                    if (isThisCorrect) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "✓",
                            color = Green,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
