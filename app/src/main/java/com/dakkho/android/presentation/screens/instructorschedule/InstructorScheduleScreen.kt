package com.dakkho.android.presentation.screens.instructorschedule

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dakkho.android.domain.model.LiveClass
import com.dakkho.android.domain.model.LiveClassStatus
import com.dakkho.android.presentation.components.AnimatedPage
import com.dakkho.android.presentation.components.EmptyState
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.components.ShimmerEffect
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.Neutral500
import com.dakkho.android.presentation.theme.SkyBlue
import com.dakkho.android.presentation.theme.Warning
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstructorScheduleScreen(
    onBackClick: () -> Unit,
    viewModel: InstructorScheduleViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    AnimatedPage {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "${uiState.instructorName} Schedule",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
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
                    },
                    scrollBehavior = scrollBehavior
                )
            }
        ) { paddingValues ->
            when {
                uiState.isLoading -> {
                    ShimmerSchedule(modifier = Modifier.padding(paddingValues))
                }
                uiState.error != null -> {
                    val errorMsg = uiState.error
                    EmptyState(
                        title = "Could not load schedule",
                        subtitle = errorMsg ?: "Unknown error",
                        actionText = "Retry",
                        onAction = { viewModel.loadLiveClasses() },
                        iconRes = android.R.drawable.ic_dialog_alert,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(
                            horizontal = DesignToken.Space.dp16,
                            vertical = DesignToken.Space.dp8
                        ),
                        verticalArrangement = Arrangement.spacedBy(DesignToken.Space.dp8)
                    ) {
                        // ── Calendar ──
                        item {
                            MonthCalendar(
                                selectedMonth = uiState.selectedMonth,
                                selectedDate = uiState.selectedDate,
                                eventsByDate = uiState.eventsByDate,
                                onMonthChange = { viewModel.selectMonth(it) },
                                onDateSelect = { viewModel.selectDate(it) }
                            )
                        }

                        // ── Events for selected date or all upcoming ──
                        val displayClasses = if (uiState.selectedDate != null) {
                            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val dateKey = sdf.format(uiState.selectedDate!!.time)
                            uiState.eventsByDate[dateKey] ?: emptyList()
                        } else {
                            uiState.upcomingClasses
                        }

                        item {
                            Text(
                                text = if (uiState.selectedDate != null) {
                                    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                                    "Classes on ${sdf.format(uiState.selectedDate!!.time)}"
                                } else {
                                    "Upcoming Classes (${displayClasses.size})"
                                },
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (displayClasses.isEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(DesignToken.Space.dp16))
                                EmptyState(
                                    title = "No classes scheduled",
                                    subtitle = if (uiState.selectedDate != null)
                                        "No classes on this date"
                                    else
                                        "This instructor has no upcoming live classes",
                                    iconRes = android.R.drawable.ic_menu_today
                                )
                            }
                        } else {
                            items(displayClasses, key = { it.id }) { liveClass ->
                                LiveClassEventCard(liveClass = liveClass)
                            }
                        }

                        // ── Past classes section ──
                        if (uiState.selectedDate == null && uiState.pastClasses.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(DesignToken.Space.dp16))
                                Text(
                                    text = "Past Classes (${uiState.pastClasses.size})",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            items(uiState.pastClasses.take(5), key = { it.id }) { liveClass ->
                                LiveClassEventCard(liveClass = liveClass)
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(DesignToken.Space.dp24))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MonthCalendar(
    selectedMonth: Calendar,
    selectedDate: Calendar?,
    eventsByDate: Map<String, List<LiveClass>>,
    onMonthChange: (Calendar) -> Unit,
    onDateSelect: (Calendar) -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignToken.Space.dp12)
        ) {
            // Month navigation header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        val prev = (selectedMonth.clone() as Calendar).apply { add(Calendar.MONTH, -1) }
                        onMonthChange(prev)
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous month",
                        modifier = Modifier.size(18.dp)
                    )
                }

                val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
                Text(
                    text = monthFormat.format(selectedMonth.time),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                IconButton(
                    onClick = {
                        val next = (selectedMonth.clone() as Calendar).apply { add(Calendar.MONTH, 1) }
                        onMonthChange(next)
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Next month",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp8))

            // Day of week headers
            val dayNames = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
            Row(modifier = Modifier.fillMaxWidth()) {
                dayNames.forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = Neutral500
                    )
                }
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp4))

            // Calendar grid
            val cal = (selectedMonth.clone() as Calendar).apply {
                set(Calendar.DAY_OF_MONTH, 1)
            }
            val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1 // 0-indexed for Sun
            val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
            val today = Calendar.getInstance()
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            var dayCounter = 1
            val totalCells = firstDayOfWeek + daysInMonth
            val rows = ((totalCells + 6) / 7)

            for (row in 0 until rows) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (col in 0..6) {
                        val cellIndex = row * 7 + col
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            if (cellIndex >= firstDayOfWeek && dayCounter <= daysInMonth) {
                                val day = dayCounter
                                val dateCal = (selectedMonth.clone() as Calendar).apply {
                                    set(Calendar.DAY_OF_MONTH, day)
                                }
                                val dateKey = sdf.format(dateCal.time)
                                val hasEvents = eventsByDate.containsKey(dateKey)
                                val isToday = dateCal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                                        dateCal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
                                val isSelected = selectedDate != null &&
                                        dateCal.get(Calendar.YEAR) == selectedDate.get(Calendar.YEAR) &&
                                        dateCal.get(Calendar.DAY_OF_YEAR) == selectedDate.get(Calendar.DAY_OF_YEAR)

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clickable { onDateSelect(dateCal) }
                                        .padding(vertical = 2.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when {
                                                    isSelected -> SkyBlue
                                                    isToday -> SkyBlue.copy(alpha = 0.15f)
                                                    else -> Color.Transparent
                                                }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$day",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal
                                            ),
                                            color = when {
                                                isSelected -> Color.White
                                                isToday -> SkyBlue
                                                else -> MaterialTheme.colorScheme.onSurface
                                            }
                                        )
                                    }

                                    // Event dot indicator
                                    if (hasEvents) {
                                        Spacer(modifier = Modifier.height(1.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(4.dp)
                                                .clip(CircleShape)
                                                .background(SkyBlue)
                                        )
                                    } else {
                                        Spacer(modifier = Modifier.height(5.dp))
                                    }
                                }
                                dayCounter++
                            } else {
                                Spacer(modifier = Modifier.height(37.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LiveClassEventCard(
    liveClass: LiveClass,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignToken.Space.dp12),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Time column
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(56.dp)
            ) {
                val timeText = liveClass.scheduledAt?.let { parseTime(it) } ?: "--:--"
                Text(
                    text = timeText,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = when (liveClass.status) {
                        LiveClassStatus.LIVE -> Color(0xFFEF4444)
                        LiveClassStatus.SCHEDULED -> SkyBlue
                        else -> Neutral500
                    }
                )
                Text(
                    text = "${liveClass.durationMinutes}min",
                    style = MaterialTheme.typography.labelSmall,
                    color = Neutral500
                )
            }

            Spacer(modifier = Modifier.width(DesignToken.Space.dp12))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = liveClass.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (!liveClass.courseName.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = liveClass.courseName,
                        style = MaterialTheme.typography.labelSmall,
                        color = Neutral500,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Status badge
                Spacer(modifier = Modifier.height(DesignToken.Space.dp4))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Live indicator
                    if (liveClass.status == LiveClassStatus.LIVE) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEF4444))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = liveClass.status.displayName,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = when (liveClass.status) {
                            LiveClassStatus.LIVE -> Color(0xFFEF4444)
                            LiveClassStatus.SCHEDULED -> SkyBlue
                            LiveClassStatus.ENDED -> Neutral500
                            LiveClassStatus.CANCELLED -> Color(0xFFEF4444)
                        }
                    )
                }
            }

            // Join button for live classes
            if (liveClass.status == LiveClassStatus.LIVE || liveClass.status == LiveClassStatus.SCHEDULED) {
                if (liveClass.status == LiveClassStatus.LIVE) {
                    Button(
                        onClick = { /* TODO: Navigate to live class */ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEF4444)
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Join",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ShimmerSchedule(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(DesignToken.Space.dp16)
    ) {
        ShimmerEffect(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        )
        Spacer(modifier = Modifier.height(DesignToken.Space.dp12))
        repeat(3) {
            ShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(vertical = DesignToken.Space.dp4)
            )
        }
    }
}

private fun parseTime(dateStr: String): String {
    return try {
        val format = when {
            dateStr.contains("T") -> SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            dateStr.contains(" ") -> SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            else -> return "--:--"
        }
        val date = format.parse(dateStr) ?: return "--:--"
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        timeFormat.format(date)
    } catch (e: Exception) { "--:--" }
}
