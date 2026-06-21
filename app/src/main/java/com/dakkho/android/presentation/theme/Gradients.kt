package com.dakkho.android.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ── Primary Gradient: Sky Blue → Deep Blue (135°) ──
val primaryGradient: Brush
    @Composable get() = Brush.linearGradient(
        colors = listOf(SkyBlue, DeepBlue),
        start = Offset.Zero,
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

// ── Success Gradient: Green tones ──
val successGradient: Brush
    @Composable get() = Brush.linearGradient(
        colors = listOf(GreenLight, Green),
        start = Offset.Zero,
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

// ── Hero Gradient: Deep → Sky with alpha ──
val heroGradient: Brush
    @Composable get() = Brush.linearGradient(
        colors = listOf(DeepBlue, SkyBlue.copy(alpha = 0.8f)),
        start = Offset.Zero,
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

// ── Dark Overlay Gradient ──
val darkOverlayGradient: Brush
    @Composable get() = Brush.verticalGradient(
        colors = listOf(Color.Transparent, Neutral900.copy(alpha = 0.8f))
    )

// ── Card Shimmer Gradient ──
val shimmerGradient: Brush
    @Composable get() = Brush.linearGradient(
        colors = listOf(
            Neutral200,
            Neutral100,
            Neutral200
        ),
        start = Offset.Zero,
        end = Offset(Float.POSITIVE_INFINITY, 0f)
    )

// ── Warm Gradient for featured items ──
val warmGradient: Brush
    @Composable get() = Brush.linearGradient(
        colors = listOf(SkyBlueLight, DeepBlueLight),
        start = Offset.Zero,
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

// ── Error Gradient ──
val errorGradient: Brush
    @Composable get() = Brush.linearGradient(
        colors = listOf(Error, ErrorDark),
        start = Offset.Zero,
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

// ── Utility functions for creating custom gradients ──

fun createDiagonalGradient(
    startColor: Color,
    endColor: Color
): Brush = Brush.linearGradient(
    colors = listOf(startColor, endColor),
    start = Offset.Zero,
    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
)

fun createVerticalGradient(
    startColor: Color,
    endColor: Color
): Brush = Brush.verticalGradient(
    colors = listOf(startColor, endColor)
)

fun createHorizontalGradient(
    startColor: Color,
    endColor: Color
): Brush = Brush.horizontalGradient(
    colors = listOf(startColor, endColor)
)
