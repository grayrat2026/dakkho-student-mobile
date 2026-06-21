package com.dakkho.android.presentation.screens.progress

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dakkho.android.presentation.theme.DeepBlue
import com.dakkho.android.presentation.theme.Green
import com.dakkho.android.presentation.theme.Neutral400
import com.dakkho.android.presentation.theme.SkyBlue
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseProgressScreen(
    courseId: String,
    courseTitle: String,
    onBackClick: () -> Unit,
    viewModel: CourseProgressViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    androidx.compose.runtime.LaunchedEffect(courseId) {
        viewModel.initialize(courseId, courseTitle)
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Course Progress",
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
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = SkyBlue)
            }
        } else if (uiState.error != null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        uiState.error ?: "Error",
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(onClick = { viewModel.loadProgress() }) {
                        Text("Retry")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(Modifier.height(4.dp)) }

                // Circular progress indicator
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        ProgressCircle(
                            percentage = uiState.progressPercent,
                            completedLessons = uiState.completedLessons,
                            totalLessons = uiState.totalLessons
                        )
                    }
                }

                // Stats row
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatCard("Lessons", "${uiState.completedLessons}/${uiState.totalLessons}")
                        StatCard("Studied", "${"%.1f".format(uiState.studiedHours)}h")
                        StatCard("Total", "${"%.1f".format(uiState.totalHours)}h")
                    }
                }

                // Weekly study chart
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 2.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Weekly Study",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Spacer(Modifier.height(16.dp))
                            if (uiState.weeklyStudyData.isNotEmpty()) {
                                WeeklyStudyChart(
                                    data = uiState.weeklyStudyData,
                                    maxHours = uiState.weeklyStudyData.maxOfOrNull { it.hoursStudied }?.coerceAtLeast(1f) ?: 1f
                                )
                            } else {
                                Text(
                                    "No study data yet",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Neutral400,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // Learning path tracker
                if (uiState.learningPath.isNotEmpty()) {
                    item {
                        Text(
                            "Learning Path",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }

                    itemsIndexed(uiState.learningPath) { index, step ->
                        LearningPathStepItem(
                            step = step,
                            isLast = index == uiState.learningPath.lastIndex
                        )
                    }
                }

                item { Spacer(Modifier.height(32.dp)) }
            }
        }
    }
}

@Composable
private fun ProgressCircle(
    percentage: Int,
    completedLessons: Int,
    totalLessons: Int
) {
    val animatedColor = if (percentage >= 80) Green else SkyBlue
    Box(contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(180.dp)) {
            val strokeWidth = 14.dp.toPx()
            val diameter = size.width - strokeWidth
            val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)

            // Background circle
            drawArc(
                color = Neutral400.copy(alpha = 0.2f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = Size(diameter, diameter),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Progress arc
            val sweepAngle = (percentage / 100f) * 360f
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(SkyBlue, DeepBlue),
                    center = center
                ),
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = Size(diameter, diameter),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "$percentage%",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = animatedColor
            )
            Text(
                "$completedLessons of $totalLessons lessons",
                style = MaterialTheme.typography.labelMedium,
                color = Neutral400
            )
        }
    }
}

@Composable
private fun StatCard(label: String, value: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = SkyBlue
            )
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = Neutral400
            )
        }
    }
}

@Composable
private fun WeeklyStudyChart(
    data: List<WeeklyStudyDay>,
    maxHours: Float
) {
    val barColor = SkyBlue
    val todayColor = DeepBlue
    val density = LocalDensity.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEach { day ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                // Hours label
                if (day.hoursStudied > 0) {
                    Text(
                        text = "${"%.1f".format(day.hoursStudied)}h",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 9.sp
                        ),
                        color = if (day.isToday) todayColor else barColor
                    )
                    Spacer(Modifier.height(4.dp))
                }

                // Bar
                val barHeight = if (maxHours > 0 && day.hoursStudied > 0) {
                    (day.hoursStudied / maxHours).coerceIn(0.05f, 1f)
                } else 0.02f

                Box(
                    modifier = Modifier
                        .width(20.dp)
                        .height(80.dp * barHeight)
                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                        .background(
                            if (day.isToday) todayColor
                            else if (day.hoursStudied > 0) barColor.copy(alpha = 0.7f)
                            else Neutral400.copy(alpha = 0.2f)
                        )
                )

                Spacer(Modifier.height(6.dp))

                // Day label
                Text(
                    text = day.dayName,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 10.sp
                    ),
                    color = if (day.isToday) todayColor else Neutral400
                )
            }
        }
    }
}

@Composable
private fun LearningPathStepItem(
    step: LearningPathStep,
    isLast: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Vertical line + icon column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(40.dp)
        ) {
            // Status icon
            Icon(
                imageVector = if (step.isCompleted) Icons.Filled.CheckCircle
                else if (step.isCurrent) Icons.Filled.RadioButtonUnchecked
                else Icons.Filled.RadioButtonUnchecked,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = when {
                    step.isCompleted -> Green
                    step.isCurrent -> SkyBlue
                    else -> Neutral400.copy(alpha = 0.5f)
                }
            )

            // Connecting line
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(24.dp)
                        .background(
                            if (step.isCompleted) Green.copy(alpha = 0.5f)
                            else Neutral400.copy(alpha = 0.3f)
                        )
                )
            }
        }

        Spacer(Modifier.width(8.dp))

        // Step content
        Column(
            modifier = Modifier.padding(top = 2.dp, bottom = if (isLast) 0.dp else 12.dp)
        ) {
            Text(
                text = step.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (step.isCurrent || step.isCompleted) FontWeight.SemiBold else FontWeight.Normal
                ),
                color = when {
                    step.isCompleted -> Green
                    step.isCurrent -> MaterialTheme.colorScheme.onSurface
                    else -> Neutral400
                }
            )
            if (step.isCurrent) {
                Spacer(Modifier.height(2.dp))
                Text(
                    "In progress",
                    style = MaterialTheme.typography.labelSmall,
                    color = SkyBlue
                )
            }
        }
    }
}
