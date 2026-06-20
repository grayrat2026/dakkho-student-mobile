package com.dakkho.android.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.dakkho.android.domain.model.Course
import com.dakkho.android.presentation.theme.DeepBlue
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.Green
import com.dakkho.android.presentation.theme.Neutral500
import com.dakkho.android.presentation.theme.SkyBlue

@Composable
fun CourseCard(
    course: Course,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    showProgress: Boolean = false,
    progress: Float = 0f
) {
    GlassCard(
        modifier = modifier.clickable { onClick(course.id) }
    ) {
        // Thumbnail
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .clip(RoundedCornerShape(DesignToken.Space.dp8))
        ) {
            if (course.thumbnailUrl != null) {
                AsyncImage(
                    model = course.thumbnailUrl,
                    contentDescription = course.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                        contentDescription = null,
                        tint = Neutral500,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // Price badge
            if (course.discountedPrice != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(DesignToken.Space.dp4)
                ) {
                    Text(
                        text = if (course.discountedPrice == 0.0) "Free" else "৳${course.discountedPrice.toInt()}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = com.dakkho.android.presentation.theme.SurfaceLight,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .drawBehind { drawRect(Green) }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            } else if (course.price != null && course.price > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(DesignToken.Space.dp4)
                ) {
                    Text(
                        text = "৳${course.price.toInt()}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = com.dakkho.android.presentation.theme.SurfaceLight,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .drawBehind { drawRect(DeepBlue) }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(DesignToken.Space.dp8))

        // Title
        Text(
            text = course.title,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(DesignToken.Space.dp4))

        // Instructor
        if (course.instructorName != null) {
            Text(
                text = course.instructorName,
                style = MaterialTheme.typography.bodySmall,
                color = Neutral500,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(DesignToken.Space.dp4))

        // Rating + Students row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = android.R.drawable.btn_star_big_on),
                    contentDescription = "Rating",
                    tint = SkyBlue,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = String.format("%.1f", course.rating ?: 0f),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (course.enrollmentCount != null) {
                Text(
                    text = "${course.enrollmentCount} students",
                    style = MaterialTheme.typography.labelSmall,
                    color = Neutral500
                )
            }
        }

        if (showProgress && progress > 0f) {
            Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
            DakkhoProgressBar(progress = progress)
        }
    }
}

@Composable
fun CourseCardGrid(
    courses: List<Course>,
    onCourseClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(DesignToken.Space.dp12),
        verticalArrangement = Arrangement.spacedBy(DesignToken.Space.dp12)
    ) {
        items(courses, key = { it.id }) { course ->
            CourseCard(
                course = course,
                onClick = onCourseClick
            )
        }
    }
}
