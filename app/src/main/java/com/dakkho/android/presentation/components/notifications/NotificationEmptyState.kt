package com.dakkho.android.presentation.components.notifications

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dakkho.android.presentation.components.EmptyState

@Composable
fun NotificationEmptyState(
    modifier: Modifier = Modifier
) {
    EmptyState(
        title = "All caught up!",
        subtitle = "You have no notifications right now. We'll let you know when something comes up.",
        iconRes = android.R.drawable.ic_menu_info_details,
        modifier = modifier
    )
}
