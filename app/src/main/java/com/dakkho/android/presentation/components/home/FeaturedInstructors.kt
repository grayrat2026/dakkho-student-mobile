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
import com.dakkho.android.domain.model.Instructor
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.components.ShimmerEffect
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.Neutral500
import com.dakkho.android.presentation.theme.SkyBlue
import com.dakkho.android.presentation.theme.Warning

@Composable
fun FeaturedInstructors(
    instructors: List<Instructor>,
    onInstructorClick: (String) -> Unit,
    onSeeAllClick: () -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    // Skip section if not loading and no instructors
    if (!isLoading && instructors.isEmpty()) return

    Column(modifier = modifier) {
        SectionHeader(
            title = "Featured Instructors",
            showSeeAll = true,
            onSeeAllClick = onSeeAllClick
        )

        Spacer(modifier = Modifier.height(DesignToken.Space.dp8))

        LazyRow(
            contentPadding = PaddingValues(horizontal = DesignToken.Space.dp16),
            horizontalArrangement = Arrangement.spacedBy(DesignToken.Space.dp12)
        ) {
            if (isLoading && instructors.isEmpty()) {
                // Show skeleton items while loading
                items(3) {
                    InstructorCardSkeleton(
                        modifier = Modifier.width(140.dp)
                    )
                }
            } else {
                items(instructors, key = { it.id }) { instructor ->
                    InstructorCard(
                        instructor = instructor,
                        onClick = { onInstructorClick(instructor.id) },
                        modifier = Modifier.width(140.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun InstructorCard(
    instructor: Instructor,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Circular avatar
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (instructor.avatarUrl != null) {
                    AsyncImage(
                        model = instructor.avatarUrl,
                        contentDescription = instructor.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                    )
                } else {
                    // Fallback avatar with initial
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .drawBehind {
                                drawRect(color = SkyBlue.copy(alpha = 0.15f))
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = instructor.name.take(1).uppercase(),
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = SkyBlue
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp8))

            // Name
            Text(
                text = instructor.name,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Title / Specialization
            if (instructor.title != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = instructor.title,
                    style = MaterialTheme.typography.bodySmall,
                    color = Neutral500,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp6))

            // Course count badge + Rating stars
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Course count
                Text(
                    text = "${instructor.courseCount} courses",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = SkyBlue
                )

                Spacer(modifier = Modifier.width(DesignToken.Space.dp8))

                // Rating stars
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.btn_star_big_on),
                        contentDescription = "Rating",
                        tint = Warning,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = String.format("%.1f", instructor.rating),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun InstructorCardSkeleton(
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = DesignToken.Space.dp8)
        ) {
            ShimmerEffect(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(DesignToken.Space.dp8))

            ShimmerEffect(
                modifier = Modifier
                    .width(80.dp)
                    .height(14.dp)
            )

            Spacer(modifier = Modifier.height(DesignToken.Space.dp4))

            ShimmerEffect(
                modifier = Modifier
                    .width(60.dp)
                    .height(10.dp)
            )

            Spacer(modifier = Modifier.height(DesignToken.Space.dp6))

            ShimmerEffect(
                modifier = Modifier
                    .width(90.dp)
                    .height(10.dp)
            )
        }
    }
}


