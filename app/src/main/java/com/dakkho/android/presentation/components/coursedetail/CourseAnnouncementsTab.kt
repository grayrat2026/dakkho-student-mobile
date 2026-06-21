package com.dakkho.android.presentation.components.coursedetail

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dakkho.android.domain.model.Announcement
import com.dakkho.android.presentation.theme.Green
import com.dakkho.android.presentation.theme.Neutral400
import com.dakkho.android.presentation.theme.SkyBlue

@Composable
fun CourseAnnouncementsTab(
    announcements: List<Announcement> = emptyList(),
    onViewAllClick: () -> Unit = {},
    onAnnouncementClick: (Announcement) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Header with View All
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (announcements.isEmpty()) "Announcements" else "${announcements.size} Announcements",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            if (announcements.isNotEmpty()) {
                TextButton(onClick = onViewAllClick) {
                    Text(
                        text = "View All",
                        style = MaterialTheme.typography.labelMedium,
                        color = SkyBlue
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (announcements.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Campaign,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Neutral400
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "No announcements yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Neutral400
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Course announcements will appear here",
                    style = MaterialTheme.typography.bodySmall,
                    color = Neutral400
                )
            }
        } else {
            announcements.take(3).forEach { announcement ->
                CompactAnnouncementCard(
                    announcement = announcement,
                    onClick = { onAnnouncementClick(announcement) }
                )
                Spacer(modifier = Modifier.height(6.dp))
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun CompactAnnouncementCard(
    announcement: Announcement,
    onClick: () -> Unit
) {
    val typeColor = when (announcement.type) {
        "urgent" -> Color(0xFFEF4444)
        "warning" -> Color(0xFFF59E0B)
        "update" -> Green
        else -> SkyBlue
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Type indicator dot
            Surface(
                modifier = Modifier.size(8.dp).padding(top = 4.dp),
                shape = RoundedCornerShape(4.dp),
                color = typeColor
            ) {}
            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (announcement.isPinned) {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = "Pinned",
                            modifier = Modifier.size(12.dp),
                            tint = SkyBlue
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = announcement.title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = announcement.body,
                    style = MaterialTheme.typography.bodySmall,
                    color = Neutral400,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
