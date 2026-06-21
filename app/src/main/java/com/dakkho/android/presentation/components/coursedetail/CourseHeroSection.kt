package com.dakkho.android.presentation.components.coursedetail

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
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.dakkho.android.presentation.theme.DeepBlue
import com.dakkho.android.presentation.theme.Green
import com.dakkho.android.presentation.theme.Neutral400
import com.dakkho.android.presentation.theme.SkyBlue

@Composable
fun CourseHeroSection(
    title: String,
    thumbnailUrl: String?,
    rating: Float?,
    reviewCount: Int?,
    enrollmentCount: Int?,
    durationHours: Float?,
    level: String?,
    instructorName: String?,
    instructorAvatar: String?,
    technology: String?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Thumbnail with gradient overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
        ) {
            AsyncImage(
                model = thumbnailUrl,
                contentDescription = "Course thumbnail",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )

            // Gradient overlay at bottom
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                androidx.compose.ui.graphics.Color.Transparent,
                                androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
            )

            // Technology badge
            if (!technology.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .background(
                            color = SkyBlue.copy(alpha = 0.9f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = technology,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = androidx.compose.ui.graphics.Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Rating + Review count row
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (rating != null) {
                RatingStars(rating = rating, starSize = 14)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = String.format("%.1f", rating),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            if (reviewCount != null) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "($reviewCount reviews)",
                    style = MaterialTheme.typography.bodySmall,
                    color = Neutral400
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Stats row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (enrollmentCount != null) {
                StatItem(
                    icon = Icons.Default.Group,
                    label = "$enrollmentCount students"
                )
            }
            if (durationHours != null) {
                StatItem(
                    icon = Icons.Default.PlayCircle,
                    label = "${durationHours}h content"
                )
            }
            if (!level.isNullOrBlank()) {
                StatItem(
                    icon = Icons.Default.SignalCellularAlt,
                    label = level
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Instructor card
        InstructorCard(
            instructorName = instructorName,
            instructorAvatar = instructorAvatar,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = Neutral400
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Neutral400
        )
    }
}
