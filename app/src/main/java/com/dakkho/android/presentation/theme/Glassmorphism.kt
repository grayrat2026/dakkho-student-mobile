package com.dakkho.android.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

/**
 * Glassmorphism effect for DAKKHO UI components.
 *
 * Creates a frosted-glass appearance with:
 * - Semi-transparent background
 * - Backdrop blur
 * - Subtle border
 * - Rounded corners
 *
 * Usage:
 * ```
 * Card(modifier = Modifier.glassCard()) {
 *     // content
 * }
 * ```
 */

private val GlassBackgroundLight = Color(0xB3FFFFFF) // rgba(255,255,255,0.7)
private val GlassBackgroundDark = Color(0xB30F172A)   // rgba(15,23,42,0.7)
private val GlassBorderLight = Color(0x1A0F172A)      // rgba(15,23,42,0.1)
private val GlassBorderDark = Color(0x1AFFFFFF)       // rgba(255,255,255,0.1)

/**
 * Applies glassmorphism styling to a composable via drawBehind.
 * Note: True backdrop blur requires graphicsLayer with renderEffect on API 31+.
 * This provides the visual foundation with background and border.
 */
object GlassmorphismDefaults {
    const val BACKGROUND_ALPHA_LIGHT = 0.7f
    const val BACKGROUND_ALPHA_DARK = 0.7f
    val CORNER_RADIUS = 16.dp
    val BORDER_WIDTH = 1.dp
    val BLUR_RADIUS = 24.dp
}
