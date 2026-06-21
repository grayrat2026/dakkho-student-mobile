package com.dakkho.android.presentation.screens.examschedule

import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.SkyBlue
import com.dakkho.android.presentation.theme.Green
import com.dakkho.android.domain.model.ExamModels.ExamSchedule
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamScheduleScreen(
    onBackClick: () -> Unit,
    viewModel: ExamScheduleViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "পরীক্ষার রুটিন",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Semester Filter Chips
            if (uiState.availableSemesters.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = DesignToken.Spacing.md, vertical = DesignToken.Spacing.sm),
                    horizontalArrangement = Arrangement.spacedBy(DesignToken.Spacing.sm)
                ) {
                    FilterChip(
                        selected = uiState.selectedSemester == null,
                        onClick = { viewModel.filterBySemester(null) },
                        label = { Text("সব") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SkyBlue,
                            selectedLabelColor = Color.White
                        )
                    )
                    uiState.availableSemesters.forEach { semester ->
                        FilterChip(
                            selected = uiState.selectedSemester == semester,
                            onClick = { viewModel.filterBySemester(semester) },
                            label = { Text("${viewModel.toBengaliNumber(semester)} সেমিস্টার") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SkyBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

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
                            OutlinedButton(onClick = { viewModel.retry() }) {
                                Text("আবার চেষ্টা করুন")
                            }
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                            horizontal = DesignToken.Spacing.md,
                            vertical = DesignToken.Spacing.sm
                        ),
                        verticalArrangement = Arrangement.spacedBy(DesignToken.Spacing.md)
                    ) {
                        items(uiState.filteredExams, key = { it.id }) { exam ->
                            ExamScheduleCard(
                                exam = exam,
                                daysLeft = viewModel.getDaysLeft(exam.date),
                                isCalendarAdded = exam.id in uiState.calendarAddedExamIds,
                                isReminderSet = exam.id in uiState.reminderSetExamIds,
                                onAddToCalendar = {
                                    addToCalendar(context, exam)
                                    viewModel.markCalendarAdded(exam.id)
                                },
                                onSetReminder = {
                                    viewModel.markReminderSet(exam.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExamScheduleCard(
    exam: ExamSchedule,
    daysLeft: Long,
    isCalendarAdded: Boolean,
    isReminderSet: Boolean,
    onAddToCalendar: () -> Unit,
    onSetReminder: () -> Unit
) {
    val countdownColor = when {
        daysLeft < 0 -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        daysLeft < 3 -> Color(0xFFEF4444) // Red
        daysLeft < 7 -> Color(0xFFF97316) // Orange
        else -> Green
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignToken.Spacing.md)
        ) {
            // Top row: Subject + Countdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = exam.subject,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = exam.examType,
                        style = MaterialTheme.typography.labelMedium,
                        color = SkyBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Countdown badge
                if (daysLeft >= 0) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(countdownColor.copy(alpha = 0.15f))
                            .border(1.dp, countdownColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "$daysLeft",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = countdownColor
                        )
                        Text(
                            text = "দিন বাকি",
                            style = MaterialTheme.typography.labelSmall,
                            color = countdownColor,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "শেষ",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(DesignToken.Spacing.md))

            // Details grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DetailItem(label = "তারিখ", value = exam.date)
                DetailItem(label = "সময়", value = exam.time)
                DetailItem(label = "স্থিতিকাল", value = exam.duration)
                DetailItem(label = "কক্ষ", value = exam.room)
            }

            Spacer(modifier = Modifier.height(DesignToken.Spacing.md))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(DesignToken.Spacing.sm)
            ) {
                OutlinedButton(
                    onClick = onAddToCalendar,
                    modifier = Modifier.weight(1f),
                    enabled = !isCalendarAdded,
                    shape = RoundedCornerShape(DesignToken.Spacing.sm)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isCalendarAdded) "যোগ হয়েছে" else "ক্যালেন্ডারে যোগ করুন",
                        fontSize = 12.sp
                    )
                }

                OutlinedButton(
                    onClick = onSetReminder,
                    modifier = Modifier.weight(1f),
                    enabled = !isReminderSet,
                    shape = RoundedCornerShape(DesignToken.Spacing.sm)
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isReminderSet) "সেট হয়েছে" else "রিমাইন্ডার সেট করুন",
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun addToCalendar(context: Context, exam: ExamSchedule) {
    val intent = Intent(Intent.ACTION_INSERT).apply {
        data = CalendarContract.Events.CONTENT_URI
        putExtra(CalendarContract.Events.TITLE, "${exam.subject} - ${exam.examType}")
        putExtra(CalendarContract.Events.DESCRIPTION, "পরীক্ষা: ${exam.subject}\nকক্ষ: ${exam.room}\nস্থিতিকাল: ${exam.duration}")
        putExtra(CalendarContract.Events.EVENT_LOCATION, exam.room)
        putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, parseDateToMillis(exam.date))
        putExtra(CalendarContract.EXTRA_EVENT_END_TIME, parseDateToMillis(exam.date) + parseDurationToMillis(exam.duration))
    }
    context.startActivity(intent)
}

private fun parseDateToMillis(dateStr: String): Long {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val date = sdf.parse(dateStr) ?: return System.currentTimeMillis()
        date.time
    } catch (e: Exception) {
        System.currentTimeMillis()
    }
}

private fun parseDurationToMillis(duration: String): Long {
    val hours = duration.filter { it.isDigit() }.toIntOrNull() ?: 2
    return hours * 60 * 60 * 1000L
}
