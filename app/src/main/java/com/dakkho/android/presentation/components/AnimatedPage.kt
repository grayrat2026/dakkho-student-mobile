package com.dakkho.android.presentation.components

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.AnimationConstants

@Composable
fun AnimatedPage(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(
            animationSpec = tween(durationMillis = 400)
        ) + slideInVertically(
            initialOffsetY = { it / 10 }, // ~12dp equivalent proportional offset
            animationSpec = tween(durationMillis = 400)
        ),
        exit = ExitTransition.None,
        modifier = modifier.animateContentSize()
    ) {
        content()
    }
}
