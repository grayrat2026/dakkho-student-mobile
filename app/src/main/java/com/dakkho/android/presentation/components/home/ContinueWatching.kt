package com.dakkho.android.presentation.components.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.dakkho.android.domain.model.Course
import com.dakkho.android.domain.model.WatchHistoryItem
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.components.ShimmerEffect
import com.dakkho.android.presentation.theme.DeepBlue
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.Neutral400
import com.dakkho.android.presentation.theme.Neutral500
import com.dakkho.android.presentation.theme.SkyBlue

@Composable
fun ContinueWatching(
    watchHistory: List<WatchHistoryItem>,
    courseMap: Map<String, Course>,
    onVideoClick: (String, String) -> Unit,
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SectionHeader(
            title = "Continue Watching",
            showSeeAll = true,
            onSeeAllClick = onSeeAllClick
        )

        Spacer(modifier = Modifier.height(DesignToken.Space.dp8))

        LazyRow(
            contentPadding = PaddingValues(horizontal = DesignToken.Space.dp16),
            horizontalArrangement = Arrangement.spacedBy(DesignToken.Space.dp12)
        ) {
            items(watchHistory, key = { it.id }) { historyItem ->
                val course = courseMap[historyItem.courseId]
                ContinueWatchingCard(
                    historyItem = historyItem,
                    courseTitle = course?.title,
                    thumbnailUrl = course?.thumbnailUrl,
                    onClick = {
                        onVideoClick(historyItem.videoId, historyItem.courseId)
                    }
                )
            }
        }
    }
}

@Composable
fun ContinueWatchingCard(
    historyItem: WatchHistoryItem,
    courseTitle: String?,
    thumbnailUrl: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier
            .width(200.dp)
            .clickable(onClick = onClick)
    ) {
        Column {
            // Thumbnail with play button overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(DesignToken.Space.dp8))
            ) {
                if (thumbnailUrl != null) {
                    AsyncImage(
                        model = thumbnailUrl,
                        contentDescription = courseTitle,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        ShimmerEffect(
                            modifier = Modifier.matchParentSize()
                        )
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                            contentDescription = null,
                            tint = Neutral400,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // Play button overlay
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .align(Alignment.Center)
                        .clip(CircleShape)
                        .drawBehind {
                            drawRect(color = SkyBlue.copy(alpha = 0.9f))
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_media_play),
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Duration badge
                if (historyItem.totalSeconds > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(DesignToken.Space.dp4)
                    ) {
                        Text(
                            text = formatDuration(historyItem.totalSeconds),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .drawBehind { drawRect(color = Color.Black.copy(alpha = 0.7f)) }
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp8))

            // Video title
            Text(
                text = courseTitle ?: "Untitled Video",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(DesignToken.Space.dp6))

            // Progress bar
            val progress = if (historyItem.totalSeconds > 0) {
                historyItem.progressSeconds.toFloat() / historyItem.totalSeconds.toFloat()
            } else {
                0f
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                ) {
                    // Background track
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .drawBehind {
                                drawRect(color = MaterialTheme.colorScheme.surfaceVariant)
                            }
                    )
                    // Progress fill
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress.coerceIn(0f, 1f))
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .drawBehind {
                                drawRect(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(SkyBlue, DeepBlue)
                                    )
                                )
                            }
                    )
                }

                Spacer(modifier = Modifier.width(DesignToken.Space.dp6))

                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = SkyBlue
                )
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp4))

            // Last watched timestamp
            if (historyItem.lastWatchedAt != null) {
                Text(
                    text = formatRelativeTime(historyItem.lastWatchedAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = Neutral500
                )
            }
        }
    }
}

private fun formatDuration(totalSeconds: Int): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%d:%02d", minutes, seconds)
    }
}

fun formatRelativeTime(timestamp: String): String {
    return try {
        // Attempt to parse ISO 8601 format
        val instant = java.time.Instant.parse(timestamp)
        val now = java.time.Instant.now()
        val duration = java.time.Duration.between(instant, now)

        val minutes = duration.toMinutes()
        val hours = duration.toHours()
        val days = duration.toDays()

        when {
            minutes < 1 -> "Just now"
            minutes < 60 -> "${minutes} minutes ago"
            hours < 24 -> if (hours == 1L) "1 hour ago" else "$hours hours ago"
            days == 1L -> "Yesterday"
            days < 7 -> "$days days ago"
            days < 30 -> "${days / 7} week${if (days / 7 > 1) "s" else ""} ago"
            days < 365 -> "${days / 30} month${if (days / 30 > 1) "s" else ""} ago"
            else -> "${days / 365} year${if (days / 365 > 1) "s" else ""} ago"
        }
    } catch (e: Exception) {
        // Fallback for non-ISO formats
        try {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault())
            val date = sdf.parse(timestamp)
            if (date != null) {
                val diffMs = System.currentTimeMillis() - date.time
                val minutes = diffMs / (60 * 1000)
                val hours = minutes / 60
                val days = hours / 24

                when {
                    minutes < 1 -> "Just now"
                    minutes < 60 -> "${minutes} minutes ago"
                    hours < 24 -> if (hours == 1L) "1 hour ago" else "$hours hours ago"
                    days == 1L -> "Yesterday"
                    days < 7 -> "$days days ago"
                    days < 30 -> "${days / 7} week${if (days / 7 > 1) "s" else ""} ago"
                    else -> "${days / 30} month${if (days / 30 > 1) "s" else ""} ago"
                }
            } else {
                "Recently"
            }
        } catch (e2: Exception) {
            "Recently"
        }
    }
}
