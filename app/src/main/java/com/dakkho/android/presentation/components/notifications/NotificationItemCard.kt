package com.dakkho.android.presentation.components.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dakkho.android.domain.model.NotificationItem
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.Neutral400
import com.dakkho.android.presentation.theme.Neutral500
import com.dakkho.android.presentation.theme.SkyBlue

@Composable
fun NotificationItemCard(
    notification: NotificationItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val glassBackground = if (isDark) {
        Color(0xB30F172A)
    } else {
        if (!notification.isRead) Color(0xCCFFFFFF) else Color(0xB3FFFFFF)
    }

    GlassCard(
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(DesignToken.Space.dp12)
        ) {
            // Icon circle
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(DesignToken.Shape.full)
                    .background(SkyBlue.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getNotificationIcon(notification.type),
                    contentDescription = null,
                    tint = SkyBlue,
                    modifier = Modifier.size(DesignToken.IconSize.medium)
                )
            }

            // Content column
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.size(DesignToken.Space.dp4))

                notification.body?.let { body ->
                    Text(
                        text = body,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Neutral500,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                notification.createdAt?.let { timestamp ->
                    Spacer(modifier = Modifier.size(DesignToken.Space.dp4))
                    Text(
                        text = timestamp,
                        style = MaterialTheme.typography.labelSmall,
                        color = Neutral400
                    )
                }
            }

            // Unread dot
            if (!notification.isRead) {
                Spacer(modifier = Modifier.width(DesignToken.Space.dp4))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(DesignToken.Shape.full)
                        .background(SkyBlue)
                )
            }
        }
    }
}

private fun getNotificationIcon(type: String?): ImageVector {
    return when (type) {
        "courses", "course" -> Icons.Default.School
        "payments", "payment" -> Icons.Default.Payment
        "system" -> Icons.Default.Info
        "live-classes", "live_classes", "live-class" -> Icons.Default.Videocam
        else -> Icons.Default.Notifications
    }
}
