package com.dakkho.android.presentation.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.Neutral100
import com.dakkho.android.presentation.theme.Neutral200

@Composable
fun ShimmerEffect(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val shimmerColors = listOf(
        Neutral200,
        Neutral100,
        Neutral200
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 300f, translateAnim - 300f),
        end = Offset(translateAnim, translateAnim)
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(DesignToken.Space.dp8))
            .background(brush)
    )
}

@Composable
fun CourseCardSkeleton(modifier: Modifier = Modifier) {
    GlassCard(modifier = modifier) {
        ShimmerEffect(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
        )

        Spacer(modifier = Modifier.height(DesignToken.Space.dp12))

        ShimmerEffect(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
        )

        Spacer(modifier = Modifier.height(DesignToken.Space.dp8))

        ShimmerEffect(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(12.dp)
        )

        Spacer(modifier = Modifier.height(DesignToken.Space.dp8))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ShimmerEffect(
                modifier = Modifier
                    .width(80.dp)
                    .height(12.dp)
            )
            ShimmerEffect(
                modifier = Modifier
                    .width(40.dp)
                    .height(12.dp)
            )
        }
    }
}

@Composable
fun ListSkeleton(
    itemCount: Int = 3,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(DesignToken.Space.dp12)
    ) {
        repeat(itemCount) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ShimmerEffect(
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(DesignToken.Space.dp12))
                Column(modifier = Modifier.weight(1f)) {
                    ShimmerEffect(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(14.dp)
                    )
                    Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
                    ShimmerEffect(
                        modifier = Modifier
                            .fillMaxWidth(0.4f)
                            .height(10.dp)
                    )
                }
            }
        }
    }
}
