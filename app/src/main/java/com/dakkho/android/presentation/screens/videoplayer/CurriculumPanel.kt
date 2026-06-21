package com.dakkho.android.presentation.screens.videoplayer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dakkho.android.domain.model.Curriculum
import com.dakkho.android.domain.model.Lesson
import com.dakkho.android.domain.model.LessonResources
import com.dakkho.android.domain.model.QuizItem
import com.dakkho.android.domain.model.ResourceFile
import com.dakkho.android.domain.model.Subject
import com.dakkho.android.domain.model.SubjectClass
import com.dakkho.android.domain.model.Unit
import com.dakkho.android.presentation.theme.Green
import com.dakkho.android.presentation.theme.Neutral400
import com.dakkho.android.presentation.theme.SkyBlue

/**
 * Full curriculum panel displayed under the video player.
 * Hierarchy: Subject → Class → Unit → Lesson (with resources)
 */
@Composable
fun CurriculumPanel(
    curriculum: Curriculum?,
    currentLessonId: String,
    isEnrolled: Boolean,
    onLessonClick: (Lesson) -> Unit,
    onResourceClick: (ResourceFile) -> Unit,
    onQuizClick: (QuizItem) -> Unit,
    modifier: Modifier = Modifier
) {
    if (curriculum == null || curriculum.sections.isEmpty()) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Course content coming soon",
                style = MaterialTheme.typography.bodyLarge,
                color = Neutral400
            )
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        curriculum.sections.forEach { subject ->
            SubjectItem(
                subject = subject,
                currentLessonId = currentLessonId,
                isEnrolled = isEnrolled,
                onLessonClick = onLessonClick,
                onResourceClick = onResourceClick,
                onQuizClick = onQuizClick
            )
        }
    }
}

@Composable
private fun SubjectItem(
    subject: Subject,
    currentLessonId: String,
    isEnrolled: Boolean,
    onLessonClick: (Lesson) -> Unit,
    onResourceClick: (ResourceFile) -> Unit,
    onQuizClick: (QuizItem) -> Unit
) {
    var isExpanded by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Subject header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                modifier = Modifier.size(24.dp),
                tint = SkyBlue
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = subject.title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            Column {
                subject.classes.forEach { cls ->
                    ClassItem(
                        cls = cls,
                        currentLessonId = currentLessonId,
                        isEnrolled = isEnrolled,
                        onLessonClick = onLessonClick,
                        onResourceClick = onResourceClick,
                        onQuizClick = onQuizClick
                    )
                }
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}

@Composable
private fun ClassItem(
    cls: SubjectClass,
    currentLessonId: String,
    isEnrolled: Boolean,
    onLessonClick: (Lesson) -> Unit,
    onResourceClick: (ResourceFile) -> Unit,
    onQuizClick: (QuizItem) -> Unit
) {
    var isExpanded by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(vertical = 8.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = Neutral400
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = cls.title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            Column {
                cls.units.forEach { unit ->
                    UnitItem(
                        unit = unit,
                        currentLessonId = currentLessonId,
                        isEnrolled = isEnrolled,
                        onLessonClick = onLessonClick,
                        onResourceClick = onResourceClick,
                        onQuizClick = onQuizClick
                    )
                }
            }
        }
    }
}

@Composable
private fun UnitItem(
    unit: Unit,
    currentLessonId: String,
    isEnrolled: Boolean,
    onLessonClick: (Lesson) -> Unit,
    onResourceClick: (ResourceFile) -> Unit,
    onQuizClick: (QuizItem) -> Unit
) {
    var isExpanded by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(vertical = 6.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = Neutral400
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = unit.title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            Column {
                unit.lessons.forEach { lesson ->
                    LessonItem(
                        lesson = lesson,
                        isCurrent = lesson.id == currentLessonId,
                        isEnrolled = isEnrolled,
                        onClick = { onLessonClick(lesson) },
                        onResourceClick = onResourceClick,
                        onQuizClick = onQuizClick
                    )
                }
            }
        }
    }
}

@Composable
private fun LessonItem(
    lesson: Lesson,
    isCurrent: Boolean,
    isEnrolled: Boolean,
    onClick: () -> Unit,
    onResourceClick: (ResourceFile) -> Unit,
    onQuizClick: (QuizItem) -> Unit
) {
    val isAccessible = isEnrolled || lesson.isFree
    var showResources by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)
    ) {
        // Lesson row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = isAccessible) {
                    if (lesson.resources != null && lesson.resources!!.lectureSheets.isNotEmpty() ||
                        lesson.resources?.pdfs?.isNotEmpty() == true ||
                        lesson.resources?.quizzes?.isNotEmpty() == true
                    ) {
                        showResources = !showResources
                    }
                    onClick()
                }
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status icon
            Icon(
                imageVector = when {
                    lesson.isCompleted -> Icons.Default.CheckCircle
                    isAccessible -> Icons.Default.PlayCircleFilled
                    else -> Icons.Default.Lock
                },
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = when {
                    lesson.isCompleted -> Green
                    isCurrent -> SkyBlue
                    isAccessible -> SkyBlue
                    else -> Neutral400
                }
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Label (e.g., "1.1", "1.2")
            if (!lesson.label.isNullOrBlank()) {
                Text(
                    text = lesson.label,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (isCurrent) Green else SkyBlue
                )
                Spacer(modifier = Modifier.width(6.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isAccessible) MaterialTheme.colorScheme.onSurface else Neutral400,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (lesson.isFree) {
                        Text("Free", style = MaterialTheme.typography.labelSmall, color = Green, fontWeight = FontWeight.Bold)
                    }
                    lesson.durationSeconds?.let {
                        Text(formatDuration(it), style = MaterialTheme.typography.labelSmall, color = Neutral400)
                    }
                }
            }

            // Resource indicators
            val res = lesson.resources
            if (res != null) {
                if (res.pdfs.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = "PDF available",
                        modifier = Modifier.size(16.dp),
                        tint = Neutral400
                    )
                }
                if (res.quizzes.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Quiz,
                        contentDescription = "Quiz available",
                        modifier = Modifier.size(16.dp),
                        tint = Neutral400
                    )
                }
            }
        }

        // Resources under the lesson
        val resources = lesson.resources
        if (resources != null && showResources) {
            LessonResourcesSection(
                resources = resources,
                isEnrolled = isEnrolled,
                onResourceClick = onResourceClick,
                onQuizClick = onQuizClick
            )
        }
    }
}

@Composable
private fun LessonResourcesSection(
    resources: LessonResources,
    isEnrolled: Boolean,
    onResourceClick: (ResourceFile) -> Unit,
    onQuizClick: (QuizItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 28.dp, top = 4.dp, bottom = 8.dp)
    ) {
        // Lecture Sheets
        if (resources.lectureSheets.isNotEmpty()) {
            Text(
                text = "Lecture Sheets",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = SkyBlue
            )
            resources.lectureSheets.forEach { sheet ->
                ResourceRow(file = sheet, onClick = { onResourceClick(sheet) })
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        // PDFs
        if (resources.pdfs.isNotEmpty()) {
            Text(
                text = "PDF Materials",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = SkyBlue
            )
            resources.pdfs.forEach { pdf ->
                ResourceRow(file = pdf, onClick = { onResourceClick(pdf) })
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        // Notes
        if (resources.notes.isNotEmpty()) {
            Text(
                text = "Notes",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = SkyBlue
            )
            resources.notes.forEach { note ->
                ResourceRow(file = note, onClick = { onResourceClick(note) })
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        // Quizzes / MCQ
        if (resources.quizzes.isNotEmpty()) {
            Text(
                text = "Quizzes (MCQ)",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = SkyBlue
            )
            resources.quizzes.forEach { quiz ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onQuizClick(quiz) }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Quiz,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = if (quiz.isCompleted) Green else SkyBlue
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = quiz.title,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "${quiz.questionCount} Qs",
                        style = MaterialTheme.typography.labelSmall,
                        color = Neutral400
                    )
                    if (quiz.isCompleted) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Completed",
                            modifier = Modifier.size(16.dp),
                            tint = Green
                        )
                    }
                }
            }
        }

        // Timestamp indicator
        if (resources.hasTimestamps) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Timestamps available",
                    style = MaterialTheme.typography.labelSmall,
                    color = Green
                )
            }
        }

        // Q&A indicator
        if (resources.hasQA) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Q&A available",
                style = MaterialTheme.typography.labelSmall,
                color = SkyBlue
            )
        }
    }
}

@Composable
private fun ResourceRow(
    file: ResourceFile,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Description,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = Neutral400
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = file.title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        file.fileType?.let {
            Text(
                text = it.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = Neutral400
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
