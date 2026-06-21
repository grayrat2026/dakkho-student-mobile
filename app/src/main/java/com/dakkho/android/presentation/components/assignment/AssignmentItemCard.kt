package com.dakkho.android.presentation.components.assignment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dakkho.android.domain.model.AssignmentItem
import com.dakkho.android.domain.model.AssignmentStatus
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.theme.DeepBlue
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.Green
import com.dakkho.android.presentation.theme.Neutral500
import com.dakkho.android.presentation.theme.SkyBlue
import com.dakkho.android.presentation.theme.Warning

@Composable
fun AssignmentItemCard(
    item: AssignmentItem,
    isUploading: Boolean = false,
    uploadProgress: Float = 0f,
    onUploadClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Document icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(DesignToken.Space.dp8))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(DesignToken.Space.dp12))

            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(DesignToken.Space.dp4)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (!item.description.isNullOrBlank()) {
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Neutral500,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(DesignToken.Space.dp8),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Status badge
                    StatusBadge(status = item.status)

                    // Due date
                    if (!item.dueDate.isNullOrBlank()) {
                        Text(
                            text = "Due: ${item.dueDate}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Neutral500
                        )
                    }

                    // Score (if graded)
                    if (item.status == AssignmentStatus.GRADED && item.score != null) {
                        Text(
                            text = "Score: ${item.score}/${item.maxScore ?: ""}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = Green
                        )
                    }
                }
            }

            // Upload button (only for pending assignments)
            if (item.status == AssignmentStatus.PENDING) {
                if (isUploading) {
                    CircularProgressIndicator(
                        progress = { uploadProgress },
                        modifier = Modifier.size(32.dp),
                        color = SkyBlue,
                        strokeWidth = 3.dp
                    )
                } else {
                    IconButton(onClick = onUploadClick) {
                        Icon(
                            imageVector = Icons.Default.CloudUpload,
                            contentDescription = "Upload",
                            tint = SkyBlue,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }

        // Upload progress bar
        if (isUploading) {
            Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
            LinearProgressIndicator(
                progress = { uploadProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = SkyBlue,
                trackColor = Neutral500.copy(alpha = 0.2f)
            )
        }
    }
}

@Composable
private fun StatusBadge(status: AssignmentStatus) {
    val (text, bgColor, textColor) = when (status) {
        AssignmentStatus.PENDING -> Triple("Pending", Warning.copy(alpha = 0.15f), Warning)
        AssignmentStatus.SUBMITTED -> Triple("Submitted", SkyBlue.copy(alpha = 0.15f), DeepBlue)
        AssignmentStatus.GRADED -> Triple("Graded", Green.copy(alpha = 0.15f), Green)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(DesignToken.Space.dp4))
            .background(bgColor)
            .padding(horizontal = DesignToken.Space.dp8, vertical = DesignToken.Space.dp2)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = textColor
        )
    }
}
