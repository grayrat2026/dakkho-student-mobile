package com.dakkho.android.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

/**
 * Centralized design token object — single source of truth
 * for all DAKKHO UI constants, colors, typography, spacing,
 * shapes, and animations.
 */
object DesignToken {

    // ── Brand Colors ──
    object Colors {
        val skyBlue = SkyBlue
        val skyBlueLight = SkyBlueLight
        val skyBlueDark = SkyBlueDark
        val deepBlue = DeepBlue
        val deepBlueLight = DeepBlueLight
        val deepBlueDark = DeepBlueDark
        val green = Green
        val greenLight = GreenLight
        val greenDark = GreenDark
        val error = Error
        val warning = Warning
        val success = Success
        val info = Info
    }

    // ── Background Colors ──
    object Backgrounds {
        val light = BackgroundLight
        val dark = BackgroundDark
        val surfaceLight = SurfaceLight
        val surfaceDark = SurfaceDark
    }

    // ── Spacing ──
    object Space {
        val dp2 = Spacing.dp2
        val dp4 = Spacing.dp4
        val dp6 = Spacing.dp6
        val dp8 = Spacing.dp8
        val dp10 = Spacing.dp10
        val dp12 = Spacing.dp12
        val dp16 = Spacing.dp16
        val dp20 = Spacing.dp20
        val dp24 = Spacing.dp24
        val dp28 = Spacing.dp28
        val dp32 = Spacing.dp32
        val dp40 = Spacing.dp40
        val dp48 = Spacing.dp48
        val dp56 = Spacing.dp56
        val dp64 = Spacing.dp64
    }

    // ── Shapes ──
    object Shape {
        val small = DakkhoShapeTokens.small
        val medium = DakkhoShapeTokens.medium
        val large = DakkhoShapeTokens.large
        val xl = DakkhoShapeTokens.xl
        val xxl = DakkhoShapeTokens.xxl
        val full = DakkhoShapeTokens.full
        val none = DakkhoShapeTokens.none
    }

    // ── Animation ──
    object Anim {
        const val pageTransitionMs = AnimationConstants.PAGE_TRANSITION_MS
        const val buttonPressMs = AnimationConstants.BUTTON_PRESS_MS
        const val shimmerDelayMs = AnimationConstants.SHIMMER_DELAY_MS
        const val shimmerDurationMs = AnimationConstants.SHIMMER_DURATION_MS
        const val fadeInMs = AnimationConstants.FADE_IN_MS
        const val fadeOutMs = AnimationConstants.FADE_OUT_MS
        const val slideDurationMs = AnimationConstants.SLIDE_DURATION_MS
        const val pulseDurationMs = AnimationConstants.PULSE_DURATION_MS
        const val expandCollapseMs = AnimationConstants.EXPAND_COLLAPSE_MS
        val cardElevation = AnimationConstants.cardElevation
        val cardElevationHovered = AnimationConstants.cardElevationHovered
        val cardElevationPressed = AnimationConstants.cardElevationPressed
    }

    // ── Glassmorphism ──
    object Glass {
        const val backgroundAlphaLight = GlassmorphismDefaults.BACKGROUND_ALPHA_LIGHT
        const val backgroundAlphaDark = GlassmorphismDefaults.BACKGROUND_ALPHA_DARK
        val cornerRadius = GlassmorphismDefaults.CORNER_RADIUS
        val borderWidth = GlassmorphismDefaults.BORDER_WIDTH
        val blurRadius = GlassmorphismDefaults.BLUR_RADIUS
    }

    // ── Typography ──
    val typography: Typography = DakkhoTypography

    // ── Elevation ──
    object Elevation {
        val level0 = 0.dp
        val level1 = 1.dp
        val level2 = 2.dp
        val level3 = 3.dp
        val level4 = 4.dp
        val level5 = 6.dp
        val level6 = 8.dp
        val level7 = 12.dp
        val level8 = 16.dp
    }

    // ── Icon Sizes ──
    object IconSize {
        val small = 16.dp
        val medium = 24.dp
        val large = 32.dp
        val xl = 40.dp
        val xxl = 48.dp
    }

    // ── Component Sizes ──
    object ComponentSize {
        val buttonHeight = 48.dp
        val buttonHeightSmall = 36.dp
        val inputHeight = 56.dp
        val avatarSmall = 32.dp
        val avatarMedium = 48.dp
        val avatarLarge = 72.dp
        val thumbnailWidth = 160.dp
        val thumbnailHeight = 90.dp
        val progressBarHeight = 4.dp
        val dividerThickness = 1.dp
    }
}
