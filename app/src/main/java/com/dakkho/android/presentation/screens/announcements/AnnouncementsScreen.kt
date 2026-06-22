package com.dakkho.android.presentation.screens.announcements

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dakkho.android.domain.model.Announcement
import com.dakkho.android.presentation.components.EmptyState
import com.dakkho.android.presentation.theme.DeepBlue
import com.dakkho.android.presentation.theme.Green
import com.dakkho.android.presentation.theme.Neutral400
import com.dakkho.android.presentation.theme.SkyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementsScreen(
    courseId: String,
    courseTitle: String,
    onBackClick: () -> Unit,
    viewModel: AnnouncementsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(courseId) {
        viewModel.initialize(courseId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState.selectedAnnouncement != null) "Announcement" else "Announcements",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = {
                        if (uiState.selectedAnnouncement != null) {
                            viewModel.closeAnnouncement()
                        } else {
                            onBackClick()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            )
        }
    ) { innerPadding ->
        if (uiState.selectedAnnouncement != null) {
            AnnouncementDetailContent(
                announcement = uiState.selectedAnnouncement!!,
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            AnnouncementListContent(
                uiState = uiState,
                onAnnouncementClick = { viewModel.openAnnouncement(it) },
                onLoadMore = { viewModel.loadMoreAnnouncements() },
                onRefresh = { viewModel.refresh() },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun AnnouncementListContent(
    uiState: AnnouncementsUiState,
    onAnnouncementClick: (Announcement) -> Unit,
    onLoadMore: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState, uiState.announcements.size) {
        val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
        if (lastVisibleIndex >= uiState.announcements.size - 3 && uiState.hasMore) {
            onLoadMore()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = SkyBlue
            )
        } else if (uiState.announcements.isEmpty() && uiState.error != null) {
            EmptyState(
                title = "Could not load announcements",
                subtitle = uiState.error ?: "An error occurred",
                actionText = "Retry",
                onAction = onRefresh
            )
        } else if (uiState.announcements.isEmpty()) {
            EmptyState(
                title = "No announcements yet",
                subtitle = "Course announcements will appear here"
            )
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        text = "${uiState.announcements.size} announcements",
                        style = MaterialTheme.typography.bodySmall,
                        color = Neutral400,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                items(uiState.announcements, key = { it.id }) { announcement ->
                    AnnouncementCard(
                        announcement = announcement,
                        onClick = { onAnnouncementClick(announcement) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AnnouncementCard(
    announcement: Announcement,
    onClick: () -> Unit
) {
    val typeColor = when (announcement.type) {
        "urgent" -> Color(0xFFEF4444)
        "warning" -> Color(0xFFF59E0B)
        "update" -> Green
        else -> SkyBlue
    }

    val typeIcon = when (announcement.type) {
        "urgent" -> Icons.Default.Error
        "warning" -> Icons.Default.Warning
        "update" -> Icons.Default.Update
        else -> Icons.Default.Info
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Header row: type badge + pin + unread
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Type badge
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = typeColor.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = typeIcon,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = typeColor
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = announcement.type.replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.labelSmall,
                            color = typeColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Pin indicator
                if (announcement.isPinned) {
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = "Pinned",
                        modifier = Modifier.size(14.dp),
                        tint = SkyBlue
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }

                // Unread indicator
                if (!announcement.isRead) {
                    Surface(
                        modifier = Modifier.size(8.dp),
                        shape = RoundedCornerShape(4.dp),
                        color = SkyBlue
                    ) {}
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = announcement.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Body preview
            Text(
                text = announcement.body,
                style = MaterialTheme.typography.bodySmall,
                color = Neutral400,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Footer: instructor + time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = announcement.instructorName ?: "Instructor",
                    style = MaterialTheme.typography.labelSmall,
                    color = Neutral400,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = formatRelativeTime(announcement.createdAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = Neutral400
                )
            }
        }
    }
}

@Composable
private fun AnnouncementDetailContent(
    announcement: Announcement,
    modifier: Modifier = Modifier
) {
    val typeColor = when (announcement.type) {
        "urgent" -> Color(0xFFEF4444)
        "warning" -> Color(0xFFF59E0B)
        "update" -> Green
        else -> SkyBlue
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
    ) {
        item {
            // Type badge
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = typeColor.copy(alpha = 0.1f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when (announcement.type) {
                            "urgent" -> Icons.Default.Error
                            "warning" -> Icons.Default.Warning
                            "update" -> Icons.Default.Update
                            else -> Icons.Default.Info
                        },
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = typeColor
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = announcement.type.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.labelMedium,
                        color = typeColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Title
            Text(
                text = announcement.title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Meta: instructor + time
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(32.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = SkyBlue.copy(alpha = 0.2f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = (announcement.instructorName ?: "I").take(1).uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            color = SkyBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = announcement.instructorName ?: "Instructor",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = formatRelativeTime(announcement.createdAt),
                        style = MaterialTheme.typography.labelSmall,
                        color = Neutral400
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(16.dp))

            // Body
            Text(
                text = announcement.body,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.4
            )
        }
    }
}

private fun formatRelativeTime(dateString: String): String {
    return try {
        val date = java.time.Instant.parse(dateString)
        val now = java.time.Instant.now()
        val duration = java.time.Duration.between(date, now)
        when {
            duration.toMinutes() < 1 -> "just now"
            duration.toMinutes() < 60 -> "${duration.toMinutes()}m ago"
            duration.toHours() < 24 -> "${duration.toHours()}h ago"
            duration.toDays() < 30 -> "${duration.toDays()}d ago"
            else -> "${duration.toDays() / 30}mo ago"
        }
    } catch (e: Exception) {
        dateString
    }
}
