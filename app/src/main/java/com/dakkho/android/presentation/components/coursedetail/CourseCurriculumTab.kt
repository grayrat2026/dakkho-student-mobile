package com.dakkho.android.presentation.components.coursedetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dakkho.android.domain.model.Curriculum
import com.dakkho.android.domain.model.Lesson
import com.dakkho.android.domain.model.Section
import com.dakkho.android.presentation.theme.Green
import com.dakkho.android.presentation.theme.Neutral400
import com.dakkho.android.presentation.theme.SkyBlue

@Composable
fun CourseCurriculumTab(
    curriculum: Curriculum?,
    isEnrolled: Boolean,
    onLessonClick: (Lesson) -> Unit,
    modifier: Modifier = Modifier
) {
    if (curriculum == null || curriculum.sections.isEmpty()) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Curriculum not available yet",
                style = MaterialTheme.typography.bodyLarge,
                color = Neutral400
            )
        }
        return
    }

    val totalLessons = curriculum.sections.sumOf { it.lessons.size }
    val totalDuration = curriculum.sections
        .flatMap { it.lessons }
        .mapNotNull { it.durationSeconds }
        .sum()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Summary
        Text(
            text = "${curriculum.sections.size} sections \u2022 $totalLessons lessons \u2022 ${formatDuration(totalDuration)}",
            style = MaterialTheme.typography.bodySmall,
            color = Neutral400
        )

        Spacer(modifier = Modifier.height(12.dp))

        curriculum.sections.forEachIndexed { index, section ->
            SectionItem(
                section = section,
                isEnrolled = isEnrolled,
                onLessonClick = onLessonClick,
                isLast = index == curriculum.sections.lastIndex
            )
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun SectionItem(
    section: Section,
    isEnrolled: Boolean,
    onLessonClick: (Lesson) -> Unit,
    isLast: Boolean
) {
    var isExpanded by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Section header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = section.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                modifier = Modifier.size(24.dp),
                tint = Neutral400
            )
        }

        // Lessons
        AnimatedVisibility(visible = isExpanded) {
            Column {
                section.lessons.forEach { lesson ->
                    LessonItem(
                        lesson = lesson,
                        isEnrolled = isEnrolled,
                        onClick = { onLessonClick(lesson) }
                    )
                }
            }
        }

        if (!isLast) {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        }
    }
}

@Composable
private fun LessonItem(
    lesson: Lesson,
    isEnrolled: Boolean,
    onClick: () -> Unit
) {
    val isAccessible = isEnrolled || lesson.isFree
    val lessonType = lesson.type ?: "video"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isAccessible) { onClick() }
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Lesson icon
        Icon(
            imageVector = when {
                lessonType == "quiz" -> Icons.Default.Quiz
                isAccessible -> Icons.Default.PlayCircleFilled
                else -> Icons.Default.Lock
            },
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = when {
                lesson.isFree -> Green
                isAccessible -> SkyBlue
                else -> Neutral400
            }
        )

        Spacer(modifier = Modifier.width(12.dp))

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
            }
        }

        if (!isAccessible) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Locked",
                modifier = Modifier.size(16.dp),
                tint = Neutral400
            )
        }
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
