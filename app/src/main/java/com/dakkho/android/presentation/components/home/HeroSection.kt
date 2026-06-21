package com.dakkho.android.presentation.components.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.dakkho.android.domain.model.Course
import com.dakkho.android.presentation.components.DakkhoProgressBar
import com.dakkho.android.presentation.components.GradientButton
import com.dakkho.android.presentation.theme.DeepBlue
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.SkyBlue
import com.dakkho.android.presentation.theme.SurfaceLight

@Composable
fun HeroSection(
    hasEnrollments: Boolean,
    lastWatchedCourse: Course?,
    watchProgress: Float,
    onResumeClick: () -> Unit,
    onExploreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (hasEnrollments && lastWatchedCourse != null) {
        ContinueLearningHero(
            course = lastWatchedCourse,
            progress = watchProgress,
            onResumeClick = onResumeClick,
            modifier = modifier
        )
    } else {
        WelcomeHero(
            onExploreClick = onExploreClick,
            modifier = modifier
        )
    }
}

@Composable
private fun ContinueLearningHero(
    course: Course,
    progress: Float,
    onResumeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryGradient = Brush.linearGradient(
        colors = listOf(SkyBlue, DeepBlue),
        start = Offset.Zero,
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = DesignToken.Space.dp16)
            .clip(RoundedCornerShape(DesignToken.Space.dp16))
            .drawBehind {
                drawRect(brush = primaryGradient)
            }
            .padding(DesignToken.Space.dp20)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Continue Learning",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = SurfaceLight.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(DesignToken.Space.dp4))

                Text(
                    text = course.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = SurfaceLight,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(DesignToken.Space.dp12))

                // Progress bar with percentage
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(SurfaceLight.copy(alpha = 0.3f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progress.coerceIn(0f, 1f))
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(SurfaceLight)
                        )
                    }

                    Spacer(modifier = Modifier.width(DesignToken.Space.dp8))

                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = SurfaceLight
                    )
                }

                Spacer(modifier = Modifier.height(DesignToken.Space.dp12))

                // Resume button
                GradientButton(
                    text = "Resume",
                    onClick = onResumeClick,
                    gradient = Brush.horizontalGradient(
                        colors = listOf(SurfaceLight.copy(alpha = 0.25f), SurfaceLight.copy(alpha = 0.15f))
                    ),
                    modifier = Modifier.height(36.dp),
                    shape = RoundedCornerShape(DesignToken.Space.dp8)
                )
            }

            Spacer(modifier = Modifier.width(DesignToken.Space.dp12))

            // Course thumbnail
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(72.dp)
                    .clip(RoundedCornerShape(DesignToken.Space.dp8))
            ) {
                if (course.thumbnailUrl != null) {
                    AsyncImage(
                        model = course.thumbnailUrl,
                        contentDescription = course.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp)
                            .background(SurfaceLight.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                            contentDescription = null,
                            tint = SurfaceLight.copy(alpha = 0.5f),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WelcomeHero(
    onExploreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val heroGradient = Brush.linearGradient(
        colors = listOf(DeepBlue, SkyBlue.copy(alpha = 0.8f)),
        start = Offset.Zero,
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = DesignToken.Space.dp16)
            .clip(RoundedCornerShape(DesignToken.Space.dp16))
            .drawBehind {
                drawRect(brush = heroGradient)
            }
            .padding(DesignToken.Space.dp24)
    ) {
        // Decorative geometric elements
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            // Large decorative circle (top-right)
            Canvas(
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.TopEnd)
            ) {
                drawCircle(
                    color = SurfaceLight.copy(alpha = 0.08f),
                    radius = size.minDimension / 2f,
                    center = Offset(size.width * 0.7f, -size.height * 0.2f)
                )
            }

            // Small decorative circle (bottom-left)
            Canvas(
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.BottomStart)
            ) {
                drawCircle(
                    color = SurfaceLight.copy(alpha = 0.06f),
                    radius = size.minDimension / 2f,
                    center = Offset(-size.width * 0.1f, size.height * 0.3f)
                )
            }

            // Tiny decorative circle
            Canvas(
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.CenterEnd)
            ) {
                drawCircle(
                    color = SurfaceLight.copy(alpha = 0.1f),
                    radius = size.minDimension / 2f
                )
            }

            // Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterStart)
            ) {
                Text(
                    text = "Welcome to DAKKHO!",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = SurfaceLight
                )

                Spacer(modifier = Modifier.height(DesignToken.Space.dp8))

                Text(
                    text = "Start your learning journey today",
                    style = MaterialTheme.typography.bodyLarge,
                    color = SurfaceLight.copy(alpha = 0.85f)
                )

                Spacer(modifier = Modifier.height(DesignToken.Space.dp20))

                GradientButton(
                    text = "Explore Courses",
                    onClick = onExploreClick,
                    gradient = Brush.horizontalGradient(
                        colors = listOf(SurfaceLight.copy(alpha = 0.25f), SurfaceLight.copy(alpha = 0.15f))
                    ),
                    modifier = Modifier
                        .width(180.dp)
                        .height(44.dp),
                    shape = RoundedCornerShape(DesignToken.Space.dp12)
                )
            }
        }
    }
}
