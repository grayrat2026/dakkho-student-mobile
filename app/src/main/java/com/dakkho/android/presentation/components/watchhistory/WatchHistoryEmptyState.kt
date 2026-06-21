package com.dakkho.android.presentation.components.watchhistory

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dakkho.android.presentation.components.EmptyState

@Composable
fun WatchHistoryEmptyState(
    modifier: Modifier = Modifier
) {
    EmptyState(
        title = "No watch history",
        subtitle = "Videos you watch will appear here so you can easily resume them later.",
        modifier = modifier
    )
}
