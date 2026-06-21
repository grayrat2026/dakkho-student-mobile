package com.dakkho.android.presentation.components

import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity

@Composable
fun ContentProtection(
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    DisposableEffect(enabled) {
        val activity = context as? FragmentActivity
        val window = activity?.window

        if (enabled && window != null) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }

        onDispose {
            if (enabled && window != null) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
            }
        }
    }

    content()
}
