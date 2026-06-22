package com.dakkho.android.presentation.screens.livesessions

import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dakkho.android.domain.model.LiveClass
import com.dakkho.android.domain.model.LiveClassStatus
import com.dakkho.android.presentation.components.EmptyState
import com.dakkho.android.presentation.components.ShimmerEffect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveSessionsScreen(
    onBack: () -> Unit = {},
    onNavigateToLiveClass: (String) -> Unit = {},
    viewModel: LiveSessionsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val pagerState = rememberPagerState(pageCount = { 3 })
    val tabs = listOf("Upcoming", "Active", "Recorded")

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    LaunchedEffect(uiState.joinResult) {
        uiState.joinResult?.let { result ->
            // Open LiveKit or meeting URL
            val url = result.livekitUrl ?: result.meetingUrl
            if (url != null) {
                try {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                } catch (_: Exception) { }
            }
            viewModel.clearJoinResult()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Live Sessions",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab Row
            TabRow(
                selectedTabIndex = uiState.selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = uiState.selectedTab == index,
                        onClick = {
                            viewModel.selectTab(index)
                        },
                        text = {
                            Text(
                                title,
                                fontWeight = if (uiState.selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        icon = {
                            when (index) {
                                0 -> Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(18.dp))
                                1 -> Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(18.dp))
                                2 -> Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                            }
                        }
                    )
                }
            }

            // Content
            when {
                uiState.isLoading -> {
                    ShimmerLoading()
                }
                else -> {
                    val sessions = when (uiState.selectedTab) {
                        0 -> uiState.upcomingSessions
                        1 -> uiState.activeSessions
                        2 -> uiState.recordedSessions
                        else -> emptyList()
                    }

                    if (sessions.isEmpty()) {
                        EmptyState(
                            title = when (uiState.selectedTab) {
                                0 -> "No Upcoming Sessions"
                                1 -> "No Active Sessions"
                                2 -> "No Recorded Sessions"
                                else -> "No Sessions"
                            },
                            subtitle = when (uiState.selectedTab) {
                                0 -> "Check back later for scheduled live classes"
                                1 -> "No classes are currently live"
                                2 -> "Recorded sessions will appear here"
                                else -> ""
                            }
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
                        ) {
                            items(sessions, key = { it.id }) { liveClass ->
                                LiveSessionCard(
                                    liveClass = liveClass,
                                    isJoining = uiState.joiningClassId == liveClass.id,
                                    isReminderToggling = uiState.reminderTogglingId == liveClass.id,
                                    onJoin = { viewModel.joinLiveClass(liveClass.id) },
                                    onReminder = { viewModel.toggleReminder(liveClass.id) },
                                    onAddToCalendar = { addToCalendar(context, liveClass) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LiveSessionCard(
    liveClass: LiveClass,
    isJoining: Boolean,
    isReminderToggling: Boolean,
    onJoin: () -> Unit,
    onReminder: () -> Unit,
    onAddToCalendar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Status badge + Category
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status badge
                StatusBadge(status = liveClass.status)

                // Course name
                liveClass.courseName?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = liveClass.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Instructor
            liveClass.instructorName?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "by $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Scheduled time / countdown
            liveClass.scheduledAt?.let { scheduledAt ->
                Spacer(modifier = Modifier.height(8.dp))
                CountdownTimer(
                    scheduledAt = scheduledAt,
                    status = liveClass.status,
                    durationMinutes = liveClass.durationMinutes
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Calendar button
                    IconButton(
                        onClick = onAddToCalendar,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = "Add to Calendar",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Reminder button
                    IconButton(
                        onClick = onReminder,
                        modifier = Modifier.size(36.dp),
                        enabled = !isReminderToggling
                    ) {
                        if (isReminderToggling) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.Default.NotificationsNone,
                                contentDescription = "Set Reminder",
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Join / Watch button
                if (liveClass.status == LiveClassStatus.LIVE) {
                    Button(
                        onClick = onJoin,
                        enabled = !isJoining,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF22C55E)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isJoining) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        } else {
                            Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Join Now", fontWeight = FontWeight.Bold)
                        }
                    }
                } else if (liveClass.status == LiveClassStatus.ENDED && liveClass.isRecorded) {
                    Button(
                        onClick = onJoin,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Watch Recording")
                    }
                } else if (liveClass.status == LiveClassStatus.SCHEDULED) {
                    Button(
                        onClick = onJoin,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Remind Me")
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: LiveClassStatus) {
    val (backgroundColor, text, textColor) = when (status) {
        LiveClassStatus.LIVE -> Triple(Color(0xFF22C55E), "LIVE", Color.White)
        LiveClassStatus.SCHEDULED -> Triple(Color(0xFF3B82F6), "UPCOMING", Color.White)
        LiveClassStatus.ENDED -> Triple(Color(0xFF6B7280), "ENDED", Color.White)
        LiveClassStatus.CANCELLED -> Triple(Color(0xFFEF4444), "CANCELLED", Color.White)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
        )
    }
}

@Composable
private fun CountdownTimer(
    scheduledAt: String,
    status: LiveClassStatus,
    durationMinutes: Int
) {
    val scheduledTime = remember(scheduledAt) {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
            sdf.parse(scheduledAt)?.time ?: 0L
        } catch (_: Exception) { 0L }
    }

    val formattedTime = remember(scheduledAt) {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
            val date = sdf.parse(scheduledAt) ?: Date()
            SimpleDateFormat("EEE, MMM d · h:mm a", Locale.US).format(date)
        } catch (_: Exception) { scheduledAt }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            Icons.Default.CalendarToday,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = formattedTime,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "· ${durationMinutes}min",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun addToCalendar(context: android.content.Context, liveClass: LiveClass) {
    val scheduledAt = liveClass.scheduledAt ?: return
    try {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        val date = sdf.parse(scheduledAt) ?: return
        val endTime = Date(date.time + liveClass.durationMinutes * 60_000L)

        val intent = Intent(Intent.ACTION_INSERT).apply {
            type = "vnd.android.cursor.item/event"
            putExtra(CalendarContract.Events.TITLE, liveClass.title)
            putExtra(CalendarContract.Events.DESCRIPTION, liveClass.description ?: "DAKKHO Live Session")
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, date.time)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.time)
        }
        context.startActivity(intent)
    } catch (_: Exception) { }
}

@Composable
private fun ShimmerLoading() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(4) {
            ShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
