package com.dakkho.android.presentation.theme

import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.dp

object AnimationConstants {
    const val PAGE_TRANSITION_MS = 400
    const val BUTTON_PRESS_MS = 150
    const val SHIMMER_DELAY_MS = 300
    const val SHIMMER_DURATION_MS = 1500
    const val FADE_IN_MS = 300
    const val FADE_OUT_MS = 200
    const val SLIDE_DURATION_MS = 350
    const val TOAST_DURATION_MS = 3000
    const val PULSE_DURATION_MS = 800
    const val EXPAND_COLLAPSE_MS = 300

    val cardElevation = 2.dp
    val cardElevationHovered = 8.dp
    val cardElevationPressed = 1.dp
}

fun <T> pageTransitionTween(): TweenSpec<T> = tween(
    durationMillis = AnimationConstants.PAGE_TRANSITION_MS
)

fun <T> buttonPressTween(): TweenSpec<T> = tween(
    durationMillis = AnimationConstants.BUTTON_PRESS_MS
)

fun <T> fadeInTween(): TweenSpec<T> = tween(
    durationMillis = AnimationConstants.FADE_IN_MS
)

fun <T> fadeOutTween(): TweenSpec<T> = tween(
    durationMillis = AnimationConstants.FADE_OUT_MS
)

fun <T> slideTween(): TweenSpec<T> = tween(
    durationMillis = AnimationConstants.SLIDE_DURATION_MS
)
