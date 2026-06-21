package com.dakkho.android.presentation.screens.curriculum

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dakkho.android.domain.model.Lesson
import com.dakkho.android.domain.model.Subject
import com.dakkho.android.domain.model.SubjectClass
import com.dakkho.android.domain.model.Unit
import com.dakkho.android.presentation.theme.Green
import com.dakkho.android.presentation.theme.Neutral400
import com.dakkho.android.presentation.theme.SkyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseCurriculumScreen(
    courseId: String,
    courseTitle: String,
    isEnrolled: Boolean,
    onBackClick: () -> Unit,
    onLessonClick: (Lesson) -> Unit,
    viewModel: CourseCurriculumViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    androidx.compose.runtime.LaunchedEffect(courseId) {
        viewModel.initialize(courseId, courseTitle, isEnrolled)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Curriculum",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = SkyBlue)
            }
        } else if (uiState.error != null && uiState.curriculum == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = uiState.error ?: "Something went wrong",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(onClick = { viewModel.retry() }) {
                        Text(text = "Retry")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Progress summary header
                CurriculumProgressHeader(uiState = uiState)

                // Expand/Collapse all controls
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { viewModel.expandAll() }) {
                        Text(text = "Expand All", style = MaterialTheme.typography.labelMedium)
                    }
                    TextButton(onClick = { viewModel.collapseAll() }) {
                        Text(text = "Collapse All", style = MaterialTheme.typography.labelMedium)
                    }
                }

                // Curriculum tree
                val curriculum = uiState.curriculum
                if (curriculum == null || curriculum.sections.isEmpty()) {
                    EmptyCurriculumState()
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        curriculum.sections.forEachIndexed { index, subject ->
                            SubjectTreeItem(
                                subject = subject,
                                isExpanded = uiState.expandedSubjects.contains(subject.id),
                                expandedClasses = uiState.expandedClasses,
                                expandedUnits = uiState.expandedUnits,
                                isEnrolled = isEnrolled,
                                completedLessons = uiState.completedLessons,
                                downloadingLessons = uiState.downloadingLessons,
                                onToggleSubject = { viewModel.toggleSubject(subject.id) },
                                onToggleClass = { viewModel.toggleClass(it) },
                                onToggleUnit = { viewModel.toggleUnit(it) },
                                onLessonClick = onLessonClick,
                                onDownloadClick = { viewModel.toggleDownload(it) }
                            )
                            if (index < curriculum.sections.lastIndex) {
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outlineVariant,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun CurriculumProgressHeader(uiState: CurriculumUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Course title
        Text(
            text = uiState.courseTitle,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Stats row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${uiState.totalLessons} lessons \u2022 ${formatDuration(uiState.totalDurationSeconds)}",
                style = MaterialTheme.typography.bodySmall,
                color = Neutral400
            )
            Text(
                text = "${uiState.completedCount}/${uiState.totalLessons} completed",
                style = MaterialTheme.typography.bodySmall,
                color = if (uiState.completedCount > 0) Green else Neutral400,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Overall progress bar
        val animatedProgress by animateFloatAsState(
            targetValue = uiState.overallProgress,
            label = "curriculumProgress"
        )
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = if (uiState.overallProgress >= 1f) Green else SkyBlue,
            trackColor = MaterialTheme.colorScheme.outlineVariant,
            strokeCap = StrokeCap.Round
        )
    }
}

@Composable
private fun SubjectTreeItem(
    subject: Subject,
    isExpanded: Boolean,
    expandedClasses: Set<String>,
    expandedUnits: Set<String>,
    isEnrolled: Boolean,
    completedLessons: Set<String>,
    downloadingLessons: Set<String>,
    onToggleSubject: () -> Unit,
    onToggleClass: (String) -> Unit,
    onToggleUnit: (String) -> Unit,
    onLessonClick: (Lesson) -> Unit,
    onDownloadClick: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Subject header — top level
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleSubject() }
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Expand/collapse icon with rotation
            val rotation by animateFloatAsState(
                targetValue = if (isExpanded) 0f else -90f,
                label = "subjectArrow"
            )
            Icon(
                imageVector = Icons.Default.ExpandMore,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = subject.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            // Lesson count badge
            val lessonCount = subject.classes.sumOf { cls -> cls.units.sumOf { unit -> unit.lessons.size } }
            Text(
                text = "$lessonCount",
                style = MaterialTheme.typography.labelSmall,
                color = Neutral400
            )
        }

        // Subject children (Classes)
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(modifier = Modifier.padding(start = 12.dp)) {
                subject.classes.forEach { cls ->
                    ClassTreeItem(
                        cls = cls,
                        isExpanded = expandedClasses.contains(cls.id),
                        expandedUnits = expandedUnits,
                        isEnrolled = isEnrolled,
                        completedLessons = completedLessons,
                        downloadingLessons = downloadingLessons,
                        onToggleClass = { onToggleClass(cls.id) },
                        onToggleUnit = onToggleUnit,
                        onLessonClick = onLessonClick,
                        onDownloadClick = onDownloadClick
                    )
                }
            }
        }
    }
}

@Composable
private fun ClassTreeItem(
    cls: SubjectClass,
    isExpanded: Boolean,
    expandedUnits: Set<String>,
    isEnrolled: Boolean,
    completedLessons: Set<String>,
    downloadingLessons: Set<String>,
    onToggleClass: () -> Unit,
    onToggleUnit: (String) -> Unit,
    onLessonClick: (Lesson) -> Unit,
    onDownloadClick: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Class header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleClass() }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                modifier = Modifier.size(18.dp),
                tint = Neutral400
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = cls.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            val lessonCount = cls.units.sumOf { it.lessons.size }
            Text(
                text = "$lessonCount",
                style = MaterialTheme.typography.labelSmall,
                color = Neutral400
            )
        }

        // Class children (Units)
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(modifier = Modifier.padding(start = 12.dp)) {
                cls.units.forEach { unit ->
                    UnitTreeItem(
                        unit = unit,
                        isExpanded = expandedUnits.contains(unit.id),
                        isEnrolled = isEnrolled,
                        completedLessons = completedLessons,
                        downloadingLessons = downloadingLessons,
                        onToggleUnit = { onToggleUnit(unit.id) },
                        onLessonClick = onLessonClick,
                        onDownloadClick = onDownloadClick
                    )
                }
            }
        }
    }
}

@Composable
private fun UnitTreeItem(
    unit: Unit,
    isExpanded: Boolean,
    isEnrolled: Boolean,
    completedLessons: Set<String>,
    downloadingLessons: Set<String>,
    onToggleUnit: () -> Unit,
    onLessonClick: (Lesson) -> Unit,
    onDownloadClick: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Unit header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleUnit() }
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                modifier = Modifier.size(16.dp),
                tint = Neutral400
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = unit.title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            val lessonCount = unit.lessons.size
            Text(
                text = "$lessonCount",
                style = MaterialTheme.typography.labelSmall,
                color = Neutral400
            )
        }

        // Unit children (Lessons)
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(modifier = Modifier.padding(start = 8.dp)) {
                unit.lessons.forEach { lesson ->
                    CurriculumLessonItem(
                        lesson = lesson,
                        isEnrolled = isEnrolled,
                        isCompleted = completedLessons.contains(lesson.id),
                        isDownloading = downloadingLessons.contains(lesson.id),
                        onClick = { onLessonClick(lesson) },
                        onDownloadClick = { onDownloadClick(lesson.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CurriculumLessonItem(
    lesson: Lesson,
    isEnrolled: Boolean,
    isCompleted: Boolean,
    isDownloading: Boolean,
    onClick: () -> Unit,
    onDownloadClick: () -> Unit
) {
    val isAccessible = isEnrolled || lesson.isFree
    val lessonType = lesson.type ?: "video"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isAccessible) { onClick() }
            .padding(vertical = 6.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Circular progress indicator for video lessons, or completion checkmark
        if (lessonType == "video" && isAccessible) {
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Completed",
                    modifier = Modifier.size(24.dp),
                    tint = Green
                )
            } else if (lesson.progress > 0f) {
                // Partial progress — circular indicator
                CircularProgressBar(
                    progress = lesson.progress,
                    modifier = Modifier.size(24.dp),
                    backgroundColor = MaterialTheme.colorScheme.outlineVariant,
                    foregroundColor = SkyBlue
                )
            } else {
                Icon(
                    imageVector = Icons.Default.PlayCircleFilled,
                    contentDescription = "Play",
                    modifier = Modifier.size(24.dp),
                    tint = SkyBlue
                )
            }
        } else if (lessonType == "quiz") {
            Icon(
                imageVector = Icons.Default.Quiz,
                contentDescription = "Quiz",
                modifier = Modifier.size(22.dp),
                tint = if (isAccessible) SkyBlue else Neutral400
            )
        } else if (!isAccessible) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Locked",
                modifier = Modifier.size(20.dp),
                tint = Neutral400
            )
        } else {
            Icon(
                imageVector = Icons.Default.PlayCircleFilled,
                contentDescription = "Play",
                modifier = Modifier.size(24.dp),
                tint = SkyBlue
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = lesson.title,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isAccessible) MaterialTheme.colorScheme.onSurface else Neutral400,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (lesson.isFree) {
                    Text(
                        text = "Free",
                        style = MaterialTheme.typography.labelSmall,
                        color = Green,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                lesson.durationSeconds?.let { seconds ->
                    Text(
                        text = formatDuration(seconds),
                        style = MaterialTheme.typography.labelSmall,
                        color = Neutral400
                    )
                }
                if (isCompleted) {
                    Text(
                        text = "Completed",
                        style = MaterialTheme.typography.labelSmall,
                        color = Green,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Download icon button (only for enrolled, video lessons)
        if (isEnrolled && lessonType == "video" && lesson.videoUrl != null) {
            IconButton(
                onClick = onDownloadClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = if (isDownloading) "Downloading" else "Download",
                    modifier = Modifier.size(18.dp),
                    tint = if (isDownloading) SkyBlue else Neutral400
                )
            }
        }
    }
}

@Composable
private fun CircularProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.LightGray,
    foregroundColor: Color = SkyBlue,
    strokeWidth: Float = 3f
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        label = "circularProgress"
    )

    Canvas(modifier = modifier) {
        val size = minOf(size.width, size.height)
        val radius = (size - strokeWidth) / 2
        val center = Offset(size / 2, size / 2)

        // Background circle
        drawCircle(
            color = backgroundColor,
            radius = radius,
            center = center,
            style = Stroke(width = strokeWidth)
        )

        // Foreground arc
        drawArc(
            color = foregroundColor,
            startAngle = -90f,
            sweepAngle = 360f * animatedProgress,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

@Composable
private fun EmptyCurriculumState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Course content coming soon",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = Neutral400
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "The instructor hasn't added any content yet.",
            style = MaterialTheme.typography.bodyMedium,
            color = Neutral400
        )
    }
}

private fun formatDuration(seconds: Int): String {
    if (seconds <= 0) return ""
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    return when {
        hours > 0 -> "${hours}h ${minutes}m"
        minutes > 0 -> "${minutes}m"
        else -> "${seconds}s"
    }
}
