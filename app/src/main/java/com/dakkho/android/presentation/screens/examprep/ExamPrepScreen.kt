package com.dakkho.android.presentation.screens.examprep

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dakkho.android.domain.model.ExamModels.StudyPlanItem
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.Green
import com.dakkho.android.presentation.theme.SkyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamPrepScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: ExamPrepViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "পরীক্ষা প্রস্তুতি",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "ফিরে যান"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = SkyBlue,
                    strokeWidth = 3.dp,
                    strokeCap = StrokeCap.Round
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = DesignToken.Spacing.md)
            ) {
                Spacer(modifier = Modifier.height(DesignToken.Spacing.sm))

                // Countdown Timer Card
                if (uiState.isCountdownActive || uiState.examDateMillis != null) {
                    CountdownTimerCard(
                        days = uiState.remainingDays,
                        hours = uiState.remainingHours,
                        minutes = uiState.remainingMinutes,
                        seconds = uiState.remainingSeconds,
                        isActive = uiState.isCountdownActive
                    )
                    Spacer(modifier = Modifier.height(DesignToken.Spacing.md))
                }

                // Syllabus Overview Card
                SyllabusOverviewCard(
                    totalTopics = uiState.importantTopics.size,
                    completedTopics = uiState.checkedTopics.size,
                    totalStudyDays = uiState.studyPlan.size,
                    totalStudyHours = uiState.studyPlan.mapNotNull { planItem ->
                        planItem.duration.takeWhile { it.isDigit() }.toIntOrNull()
                    }.sum() / 60.0
                )
                Spacer(modifier = Modifier.height(DesignToken.Spacing.md))

                // Important Topics
                if (uiState.importantTopics.isNotEmpty()) {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(DesignToken.Spacing.md)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = null,
                                    tint = SkyBlue,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(DesignToken.Spacing.sm))
                                Text(
                                    text = "গুরুত্বপূর্ণ বিষয়",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.height(DesignToken.Spacing.sm))

                            // Progress bar
                            val progress = if (uiState.importantTopics.isNotEmpty()) {
                                uiState.checkedTopics.size.toFloat() / uiState.importantTopics.size.toFloat()
                            } else 0f

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(progress)
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(
                                            Brush.horizontalGradient(
                                                colors = listOf(SkyBlue, Green)
                                            )
                                        )
                                )
                            }

                            Spacer(modifier = Modifier.height(DesignToken.Spacing.xs))
                            Text(
                                text = "${uiState.checkedTopics.size} / ${uiState.importantTopics.size} সম্পন্ন",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(DesignToken.Spacing.md))

                            // Topics list
                            uiState.importantTopics.forEach { topic ->
                                val isChecked = uiState.checkedTopics.contains(topic.id)
                                ImportantTopicItem(
                                    topicName = topic.title,
                                    isChecked = isChecked,
                                    onCheckedChange = { viewModel.toggleTopicChecked(topic.id) }
                                )
                                if (topic != uiState.importantTopics.last()) {
                                    Spacer(modifier = Modifier.height(DesignToken.Spacing.xs))
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(DesignToken.Spacing.md))
                }

                // Study Plan Timeline
                if (uiState.studyPlan.isNotEmpty()) {
                    Text(
                        text = "স্টাডি প্ল্যান",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(DesignToken.Spacing.sm))

                    uiState.studyPlan.forEachIndexed { index, planItem ->
                        StudyPlanTimelineCard(
                            planItem = planItem,
                            isLast = index == uiState.studyPlan.lastIndex,
                            dayIndex = index
                        )
                        if (!planItem.equals(uiState.studyPlan.last())) {
                            Spacer(modifier = Modifier.height(DesignToken.Spacing.sm))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(DesignToken.Spacing.xl))
            }
        }
    }
}

@Composable
private fun CountdownTimerCard(
    days: Long,
    hours: Long,
    minutes: Long,
    seconds: Long,
    isActive: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "countdown_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignToken.Spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = SkyBlue,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(DesignToken.Spacing.xs))
                Text(
                    text = "পরীক্ষার আর বাকি",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(DesignToken.Spacing.md))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CountdownUnit(value = days, label = "দিন", isActive = isActive)
                CountdownSeparator(isActive = isActive)
                CountdownUnit(value = hours, label = "ঘণ্টা", isActive = isActive)
                CountdownSeparator(isActive = isActive)
                CountdownUnit(value = minutes, label = "মিনিট", isActive = isActive)
                CountdownSeparator(isActive = isActive)
                CountdownUnit(value = seconds, label = "সেকেন্ড", isActive = isActive)
            }

            if (!isActive && days == 0L && hours == 0L && minutes == 0L && seconds == 0L) {
                Spacer(modifier = Modifier.height(DesignToken.Spacing.sm))
                Text(
                    text = "পরীক্ষার সময় শুরু হয়েছে!",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF5722)
                )
            }
        }
    }
}

@Composable
private fun CountdownUnit(
    value: Long,
    label: String,
    isActive: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "unit_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.then(
            if (label == "সেকেন্ড" && isActive) {
                Modifier.graphicsLayer { scaleX = pulseScale; scaleY = pulseScale }
            } else {
                Modifier
            }
        )
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(DesignToken.Spacing.sm))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            SkyBlue.copy(alpha = 0.15f),
                            SkyBlue.copy(alpha = 0.05f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = SkyBlue.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(DesignToken.Spacing.sm)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = String.format("%02d", value),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = SkyBlue
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun CountdownSeparator(isActive: Boolean) {
    val animatedColor by animateColorAsState(
        targetValue = if (isActive) SkyBlue else MaterialTheme.colorScheme.outline,
        animationSpec = tween(500),
        label = "separator_color"
    )
    Text(
        text = ":",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = animatedColor,
        modifier = Modifier.padding(bottom = 16.dp)
    )
}

@Composable
private fun SyllabusOverviewCard(
    totalTopics: Int,
    completedTopics: Int,
    totalStudyDays: Int,
    totalStudyHours: Double
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(DesignToken.Spacing.md)
        ) {
            Text(
                text = "সিলেবাস ওভারভিউ",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(DesignToken.Spacing.md))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OverviewStat(
                    value = totalTopics.toString(),
                    label = "মোট বিষয়"
                )
                OverviewStat(
                    value = completedTopics.toString(),
                    label = "সম্পন্ন",
                )
                OverviewStat(
                    value = totalStudyDays.toString(),
                    label = "স্টাডি দিন"
                )
                OverviewStat(
                    value = String.format("%.0f", totalStudyHours),
                    label = "ঘণ্টা"
                )
            }
        }
    }
}

@Composable
private fun OverviewStat(
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(SkyBlue.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = SkyBlue
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ImportantTopicItem(
    topicName: String,
    isChecked: Boolean,
    onCheckedChange: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isChecked) Green.copy(alpha = 0.08f) else Color.Transparent,
        animationSpec = tween(300),
        label = "topic_bg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isChecked) Green.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
        animationSpec = tween(300),
        label = "topic_border"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(DesignToken.Spacing.sm))
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(DesignToken.Spacing.sm)
            )
            .clickable { onCheckedChange() }
            .padding(horizontal = DesignToken.Spacing.md, vertical = DesignToken.Spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = { onCheckedChange() },
            colors = CheckboxDefaults.colors(
                checkedColor = Green,
                uncheckedColor = MaterialTheme.colorScheme.outline
            )
        )
        Spacer(modifier = Modifier.width(DesignToken.Spacing.sm))
        Text(
            text = topicName,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isChecked) {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            textDecoration = if (isChecked) {
                androidx.compose.ui.text.style.TextDecoration.LineThrough
            } else {
                androidx.compose.ui.text.style.TextDecoration.None
            }
        )
        if (isChecked) {
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Green,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun StudyPlanTimelineCard(
    planItem: StudyPlanItem,
    isLast: Boolean,
    dayIndex: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Timeline indicator
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Day number circle
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(SkyBlue, SkyBlue.copy(alpha = 0.7f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${planItem.dayNumber}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            // Connecting line
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(80.dp)
                        .background(SkyBlue.copy(alpha = 0.3f))
                )
            }
        }

        Spacer(modifier = Modifier.width(DesignToken.Spacing.md))

        // Plan card
        GlassCard(
            modifier = Modifier.weight(1f)
        ) {
            Column(
                modifier = Modifier.padding(DesignToken.Spacing.md)
            ) {
                Text(
                    text = planItem.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(DesignToken.Spacing.xs))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = SkyBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = planItem.duration.ifBlank { "${planItem.dayNumber * 60} মিনিট" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = SkyBlue,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(DesignToken.Spacing.sm))
                    Text(
                        text = "দিন ${planItem.dayNumber}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(DesignToken.Spacing.sm))

                // Topics chips
                planItem.topics.forEach { topic ->
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(SkyBlue.copy(alpha = 0.6f))
                        )
                        Spacer(modifier = Modifier.width(DesignToken.Spacing.sm))
                        Text(
                            text = topic,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
