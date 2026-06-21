package com.dakkho.android.presentation.components.assignment

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dakkho.android.presentation.components.EmptyState

@Composable
fun AssignmentEmptyState(
    modifier: Modifier = Modifier
) {
    EmptyState(
        title = "No assignments",
        subtitle = "Assignments for this course will appear here once they are available.",
        modifier = modifier
    )
}
