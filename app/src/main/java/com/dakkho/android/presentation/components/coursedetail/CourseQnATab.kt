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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.ThumbUp
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dakkho.android.domain.model.Discussion
import com.dakkho.android.presentation.theme.Neutral400
import com.dakkho.android.presentation.theme.SkyBlue

@Composable
fun CourseQnATab(
    discussions: List<Discussion> = emptyList(),
    isEnrolled: Boolean = false,
    onViewAllClick: () -> Unit = {},
    onDiscussionClick: (Discussion) -> Unit = {},
    onAskQuestionClick: () -> Unit = {},
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
                text = if (discussions.isEmpty()) "Q&A Forum" else "${discussions.size} Questions",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            TextButton(onClick = onViewAllClick) {
                Text(
                    text = "View All",
                    style = MaterialTheme.typography.labelMedium,
                    color = SkyBlue
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (discussions.isEmpty()) {
            // Empty state
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Forum,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Neutral400
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "No questions yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Neutral400
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Be the first to ask a question",
                    style = MaterialTheme.typography.bodySmall,
                    color = Neutral400
                )
            }
        } else {
            // Show top 3 discussions
            discussions.take(3).forEach { discussion ->
                CompactDiscussionCard(
                    discussion = discussion,
                    onClick = { onDiscussionClick(discussion) }
                )
                Spacer(modifier = Modifier.height(6.dp))
            }
        }

        // Ask question button (for enrolled students)
        if (isEnrolled) {
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAskQuestionClick() },
                shape = RoundedCornerShape(10.dp),
                color = SkyBlue.copy(alpha = 0.08f)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = SkyBlue
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ask a Question",
                        style = MaterialTheme.typography.labelLarge,
                        color = SkyBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun CompactDiscussionCard(
    discussion: Discussion,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (discussion.isPinned) {
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = "Pinned",
                        modifier = Modifier.size(12.dp),
                        tint = SkyBlue
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                    text = discussion.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = discussion.userName,
                    style = MaterialTheme.typography.labelSmall,
                    color = Neutral400,
                    maxLines = 1
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.ThumbUp,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Neutral400
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${discussion.likes}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Neutral400
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Neutral400
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${discussion.replyCount}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Neutral400
                    )
                }
            }
        }
    }
}
