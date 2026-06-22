package com.dakkho.android.presentation.screens.semester

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dakkho.android.domain.model.RoutineEntry
import com.dakkho.android.domain.model.Semester
import com.dakkho.android.domain.model.Subject
import com.dakkho.android.presentation.components.EmptyState
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.components.ShimmerEffect
import com.dakkho.android.presentation.theme.DeepBlue
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.Green
import com.dakkho.android.presentation.theme.Neutral500
import com.dakkho.android.presentation.theme.SkyBlue
import com.dakkho.android.presentation.theme.SkyBlueLight
import kotlinx.coroutines.launch

/**
 * Semester Screen — template composable for any semester within a department.
 *
 * Bangladesh Diploma system:
 * - 7 regular semesters (Semester 1 – Semester 7)
 * - 8th semester = ইন্টার্নি (Internship)
 *
 * The screen shows:
 * 1. Semester hero with progress indicator
 * 2. Tab bar: Subjects | Routine | Syllabus
 * 3. Content for the selected tab
 */
@Composable
fun SemesterScreen(
    departmentSlug: String,
    semesterNumber: Int,
    onBackClick: () -> Unit,
    onCourseClick: (String) -> Unit,
    onSubjectClick: (String) -> Unit,
    viewModel: SemesterViewModel = hiltViewModel()
) {
    val semester by viewModel.semester.collectAsState()
    val subjects by viewModel.subjects.collectAsState()
    val routine by viewModel.routine.collectAsState()
    val progress by viewModel.progress.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val expandedSubjectId by viewModel.expandedSubjectId.collectAsState()

    LaunchedEffect(departmentSlug, semesterNumber) {
        viewModel.initialize(departmentSlug, semesterNumber)
    }

    val isInternship = semesterNumber == Semester.INTERNSHIP_SEMESTER
    val semesterName = Semester.semesterName(semesterNumber)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ── Top Bar ──
        SemesterTopBar(
            semesterName = semesterName,
            isInternship = isInternship,
            onBackClick = onBackClick
        )

        if (isLoading && semester == null) {
            SemesterLoadingSkeleton()
        } else if (error != null && semester == null) {
            EmptyState(
                title = "Something went wrong",
                subtitle = error ?: "Failed to load semester",
                actionText = "Retry",
                onAction = { viewModel.retry() }
            )
        } else {
            // ── Semester Hero ──
            SemesterHero(
                semesterName = semesterName,
                semesterNumber = semesterNumber,
                isInternship = isInternship,
                subjectCount = subjects.size,
                totalCredits = subjects.sumOf { it.creditHours },
                progress = progress
            )

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // ── Tab Bar ──
            val tabs = if (isInternship) {
                listOf("Overview")
            } else {
                listOf("Subjects", "Routine", "Syllabus")
            }
            val selectedTabIndex = if (isInternship) 0 else when (selectedTab) {
                "routine" -> 1
                "syllabus" -> 2
                else -> 0
            }

            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = SkyBlue,
                modifier = Modifier.padding(horizontal = DesignToken.Space.dp16)
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = {
                            val tabName = when (index) {
                                0 -> "subjects"
                                1 -> "routine"
                                2 -> "syllabus"
                                else -> "subjects"
                            }
                            viewModel.selectTab(tabName)
                        },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // ── Tab Content ──
            if (isInternship) {
                InternshipOverviewTab(
                    departmentSlug = departmentSlug,
                    onCourseClick = onCourseClick
                )
            } else {
                when (selectedTab) {
                    "subjects" -> {
                        SubjectsTab(
                            subjects = subjects,
                            expandedSubjectId = expandedSubjectId,
                            onSubjectExpand = { viewModel.toggleSubjectExpansion(it) },
                            onCourseClick = onCourseClick
                        )
                    }
                    "routine" -> {
                        RoutineTab(
                            routine = routine,
                            semesterNumber = semesterNumber
                        )
                    }
                    "syllabus" -> {
                        SyllabusTab(
                            subjects = subjects,
                            expandedSubjectId = expandedSubjectId,
                            onSubjectExpand = { viewModel.toggleSubjectExpansion(it) },
                            onCourseClick = onCourseClick
                        )
                    }
                }
            }
        }
    }
}

// ── Top Bar ──

@Composable
private fun SemesterTopBar(
    semesterName: String,
    isInternship: Boolean,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = DesignToken.Space.dp4, vertical = DesignToken.Space.dp4),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.Center
        ) {
            if (isInternship) {
                Icon(
                    imageVector = Icons.Filled.School,
                    contentDescription = null,
                    tint = Green,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = semesterName,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.width(48.dp)) // Balance the back button
    }
}

// ── Semester Hero Section ──

@Composable
private fun SemesterHero(
    semesterName: String,
    semesterNumber: Int,
    isInternship: Boolean,
    subjectCount: Int,
    totalCredits: Int,
    progress: com.dakkho.android.domain.model.SemesterProgress?
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DesignToken.Space.dp16)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignToken.Space.dp20)
        ) {
            // Semester badge
            if (isInternship) {
                Text(
                    text = "ইন্টার্নি",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Green,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Green.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
                Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
            }

            // Semester name
            Text(
                text = semesterName,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(DesignToken.Space.dp12))

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "Subjects",
                    value = "$subjectCount",
                    color = SkyBlue
                )
                StatItem(
                    label = "Credits",
                    value = "$totalCredits",
                    color = Green
                )
                StatItem(
                    label = "Semester",
                    value = "$semesterNumber",
                    color = DeepBlue
                )
            }

            // Progress indicator
            if (progress != null) {
                Spacer(modifier = Modifier.height(DesignToken.Space.dp16))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Progress",
                        style = MaterialTheme.typography.labelMedium,
                        color = Neutral500
                    )
                    Spacer(modifier = Modifier.width(DesignToken.Space.dp8))
                    LinearProgressIndicator(
                        progress = { progress.progressPercent },
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = SkyBlue,
                        trackColor = SkyBlueLight.copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.width(DesignToken.Space.dp8))
                    Text(
                        text = "${(progress.progressPercent * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = SkyBlue
                    )
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = color
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Neutral500
        )
    }
}

// ── Subjects Tab ──

@Composable
private fun SubjectsTab(
    subjects: List<Subject>,
    expandedSubjectId: String?,
    onSubjectExpand: (String) -> Unit,
    onCourseClick: (String) -> Unit
) {
    if (subjects.isEmpty()) {
        EmptyState(
            title = "No subjects yet",
            subtitle = "Content coming soon for this semester"
        )
        return
    }

    LazyColumn(
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            horizontal = DesignToken.Space.dp16,
            vertical = DesignToken.Space.dp8
        ),
        verticalArrangement = Arrangement.spacedBy(DesignToken.Space.dp12)
    ) {
        items(subjects, key = { it.id }) { subject ->
            SubjectCard(
                subject = subject,
                isExpanded = expandedSubjectId == subject.id,
                onExpand = { onSubjectExpand(subject.id) },
                onCourseClick = onCourseClick
            )
        }
    }
}

@Composable
private fun SubjectCard(
    subject: Subject,
    isExpanded: Boolean,
    onExpand: () -> Unit,
    onCourseClick: (String) -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onExpand() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignToken.Space.dp16)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Subject color dot
                    val dotColor = subject.color?.let {
                        try { Color(android.graphics.Color.parseColor(it)) } catch (_: Exception) { SkyBlue }
                    } ?: SkyBlue

                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(dotColor)
                    )
                    Spacer(modifier = Modifier.width(DesignToken.Space.dp12))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = subject.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (subject.code.isNotBlank()) {
                            Text(
                                text = subject.code,
                                style = MaterialTheme.typography.bodySmall,
                                color = Neutral500
                            )
                        }
                    }
                }

                // Credits badge
                if (subject.creditHours > 0) {
                    Text(
                        text = "${subject.creditHours} cr",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = SkyBlue,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(SkyBlue.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.width(DesignToken.Space.dp8))

                Icon(
                    imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = Neutral500,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Instructor name
            if (subject.instructorName != null) {
                Spacer(modifier = Modifier.height(DesignToken.Space.dp4))
                Text(
                    text = "Instructor: ${subject.instructorName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Neutral500
                )
            }

            // Expanded content: description + course link
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    if (subject.description != null) {
                        Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
                        Text(
                            text = subject.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Syllabus topics preview
                    if (subject.syllabusTopics.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
                        Text(
                            text = "Topics:",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        subject.syllabusTopics.take(5).forEach { topic ->
                            Text(
                                text = "  • $topic",
                                style = MaterialTheme.typography.bodySmall,
                                color = Neutral500
                            )
                        }
                        if (subject.syllabusTopics.size > 5) {
                            Text(
                                text = "  +${subject.syllabusTopics.size - 5} more topics",
                                style = MaterialTheme.typography.bodySmall,
                                color = SkyBlue
                            )
                        }
                    }

                    // Course link
                    if (subject.courseId != null) {
                        Spacer(modifier = Modifier.height(DesignToken.Space.dp12))
                        Text(
                            text = "View Course →",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = SkyBlue,
                            modifier = Modifier.clickable { onCourseClick(subject.courseId) }
                        )
                    }
                }
            }
        }
    }
}

// ── Routine Tab ──

@Composable
private fun RoutineTab(
    routine: List<RoutineEntry>,
    semesterNumber: Int
) {
    if (routine.isEmpty()) {
        EmptyState(
            title = "No routine available",
            subtitle = "Routine for this semester will be added soon"
        )
        return
    }

    // Group routine by day
    val routineByDay = routine.groupBy { it.dayOfWeek }
        .toSortedMap()

    LazyColumn(
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            horizontal = DesignToken.Space.dp16,
            vertical = DesignToken.Space.dp8
        ),
        verticalArrangement = Arrangement.spacedBy(DesignToken.Space.dp16)
    ) {
        routineByDay.forEach { (dayOfWeek, entries) ->
            item {
                DaySection(
                    dayOfWeek = dayOfWeek,
                    entries = entries.sortedBy { it.startTime }
                )
            }
        }
    }
}

@Composable
private fun DaySection(
    dayOfWeek: Int,
    entries: List<RoutineEntry>
) {
    val dayName = RoutineEntry.DAY_NAMES.getOrElse(dayOfWeek - 1) { "Day $dayOfWeek" }
    val dayNameBn = RoutineEntry.DAY_NAMES_BN.getOrElse(dayOfWeek - 1) { "" }

    Column {
        // Day header
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = dayName,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            if (dayNameBn.isNotBlank()) {
                Spacer(modifier = Modifier.width(DesignToken.Space.dp8))
                Text(
                    text = dayNameBn,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Neutral500
                )
            }
        }

        Spacer(modifier = Modifier.height(DesignToken.Space.dp8))

        // Time slots
        entries.forEach { entry ->
            RoutineSlotCard(entry = entry)
            Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
        }
    }
}

@Composable
private fun RoutineSlotCard(entry: RoutineEntry) {
    val slotColor = entry.color?.let {
        try { Color(android.graphics.Color.parseColor(it)) } catch (_: Exception) { SkyBlue }
    } ?: SkyBlue

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(DesignToken.Space.dp12))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .border(1.dp, slotColor.copy(alpha = 0.3f), RoundedCornerShape(DesignToken.Space.dp12))
            .padding(DesignToken.Space.dp12),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Time column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(60.dp)
        ) {
            Text(
                text = entry.startTime,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = entry.endTime,
                style = MaterialTheme.typography.labelSmall,
                color = Neutral500
            )
        }

        // Color indicator
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(40.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(slotColor)
        )

        Spacer(modifier = Modifier.width(DesignToken.Space.dp12))

        // Subject info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entry.subjectName,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (entry.subjectCode.isNotBlank()) {
                Text(
                    text = entry.subjectCode,
                    style = MaterialTheme.typography.bodySmall,
                    color = Neutral500
                )
            }
            if (entry.instructorName != null) {
                Text(
                    text = entry.instructorName,
                    style = MaterialTheme.typography.bodySmall,
                    color = Neutral500
                )
            }
        }

        // Room number
        if (entry.roomNumber != null) {
            Text(
                text = entry.roomNumber,
                style = MaterialTheme.typography.labelSmall,
                color = SkyBlue,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(SkyBlue.copy(alpha = 0.1f))
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            )
        }
    }
}

// ── Syllabus Tab ──

@Composable
private fun SyllabusTab(
    subjects: List<Subject>,
    expandedSubjectId: String?,
    onSubjectExpand: (String) -> Unit,
    onCourseClick: (String) -> Unit
) {
    if (subjects.isEmpty()) {
        EmptyState(
            title = "No syllabus available",
            subtitle = "Syllabus for this semester will be added soon"
        )
        return
    }

    LazyColumn(
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            horizontal = DesignToken.Space.dp16,
            vertical = DesignToken.Space.dp8
        ),
        verticalArrangement = Arrangement.spacedBy(DesignToken.Space.dp8)
    ) {
        items(subjects, key = { it.id }) { subject ->
            SyllabusCard(
                subject = subject,
                isExpanded = expandedSubjectId == subject.id,
                onExpand = { onSubjectExpand(subject.id) },
                onCourseClick = onCourseClick
            )
        }
    }
}

@Composable
private fun SyllabusCard(
    subject: Subject,
    isExpanded: Boolean,
    onExpand: () -> Unit,
    onCourseClick: (String) -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onExpand() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignToken.Space.dp16)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Filled.MenuBook,
                        contentDescription = null,
                        tint = SkyBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(DesignToken.Space.dp8))
                    Text(
                        text = subject.name,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = Neutral500,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Expanded syllabus content
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = DesignToken.Space.dp12)) {
                    if (subject.description != null) {
                        Text(
                            text = subject.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
                    }

                    if (subject.syllabusTopics.isNotEmpty()) {
                        subject.syllabusTopics.forEach { topic ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 2.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .clip(CircleShape)
                                        .background(SkyBlue)
                                )
                                Spacer(modifier = Modifier.width(DesignToken.Space.dp8))
                                Text(
                                    text = topic,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "Topics will be added soon",
                            style = MaterialTheme.typography.bodySmall,
                            color = Neutral500
                        )
                    }

                    // Course link
                    if (subject.courseId != null) {
                        Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
                        Text(
                            text = "View Course →",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = SkyBlue,
                            modifier = Modifier.clickable { onCourseClick(subject.courseId) }
                        )
                    }
                }
            }
        }
    }
}

// ── Internship Overview Tab ──

@Composable
private fun InternshipOverviewTab(
    departmentSlug: String,
    onCourseClick: (String) -> Unit
) {
    LazyColumn(
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            horizontal = DesignToken.Space.dp16,
            vertical = DesignToken.Space.dp16
        ),
        verticalArrangement = Arrangement.spacedBy(DesignToken.Space.dp16)
    ) {
        // Internship info card
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(DesignToken.Space.dp20)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.School,
                            contentDescription = null,
                            tint = Green,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(DesignToken.Space.dp12))
                        Text(
                            text = "ইন্টার্নি (Internship)",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(DesignToken.Space.dp12))

                    Text(
                        text = "The 8th semester is dedicated to industrial internship. " +
                            "Students gain real-world experience by working in their respective " +
                            "fields under professional supervision. This practical training is " +
                            "essential for bridging academic knowledge with industry requirements.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

                    // Key requirements
                    Text(
                        text = "Key Requirements",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(DesignToken.Space.dp8))

                    val requirements = listOf(
                        "Complete minimum 16 weeks of industrial training",
                        "Submit weekly progress reports to supervisor",
                        "Maintain a detailed internship logbook",
                        "Present final internship report and presentation"
                    )
                    requirements.forEach { req ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 3.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Green)
                            )
                            Spacer(modifier = Modifier.width(DesignToken.Space.dp8))
                            Text(
                                text = req,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Important dates card
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(DesignToken.Space.dp20)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Schedule,
                            contentDescription = null,
                            tint = SkyBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(DesignToken.Space.dp8))
                        Text(
                            text = "Important Information",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(DesignToken.Space.dp12))

                    val info = listOf(
                        "Duration: Minimum 16 weeks" to SkyBlue,
                        "Assessment: Supervisor evaluation + Institute viva" to Green,
                        "Report submission: Within 2 weeks of completion" to DeepBlue
                    )
                    info.forEach { (text, color) ->
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(color)
                            )
                            Spacer(modifier = Modifier.width(DesignToken.Space.dp8))
                            Text(
                                text = text,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Loading Skeleton ──

@Composable
private fun SemesterLoadingSkeleton() {
    Column(
        modifier = Modifier.padding(DesignToken.Space.dp16)
    ) {
        // Hero skeleton
        ShimmerEffect(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(DesignToken.Space.dp16))
        )
        Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

        // Tab bar skeleton
        Row(
            horizontalArrangement = Arrangement.spacedBy(DesignToken.Space.dp24)
        ) {
            repeat(3) {
                ShimmerEffect(
                    modifier = Modifier
                        .width(80.dp)
                        .height(24.dp)
                        .clip(RoundedCornerShape(DesignToken.Space.dp4))
                )
            }
        }
        Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

        // Subject cards skeleton
        repeat(4) {
            ShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(DesignToken.Space.dp12))
            )
            Spacer(modifier = Modifier.height(DesignToken.Space.dp12))
        }
    }
}
