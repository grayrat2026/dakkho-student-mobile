package com.dakkho.android.presentation.screens.watchhistory

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dakkho.android.domain.model.WatchHistoryItem
import com.dakkho.android.presentation.components.ShimmerEffect
import com.dakkho.android.presentation.components.watchhistory.WatchHistoryEmptyState
import com.dakkho.android.presentation.components.watchhistory.WatchHistoryItemCard
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.Error
import com.dakkho.android.presentation.theme.Neutral500

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchHistoryScreen(
    onBackClick: () -> Unit,
    onResumeVideo: (videoId: String, courseId: String) -> Unit,
    viewModel: WatchHistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val watchHistoryItems by viewModel.watchHistoryItems.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Watch History",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (watchHistoryItems.isNotEmpty()) {
                        IconButton(onClick = { viewModel.showClearAllDialog() }) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Clear All",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        when {
            uiState.isLoading && watchHistoryItems.isEmpty() -> {
                WatchHistoryShimmerContent(modifier = Modifier.padding(innerPadding))
            }
            watchHistoryItems.isEmpty() && !uiState.isLoading -> {
                WatchHistoryEmptyState(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = DesignToken.Space.dp16),
                    verticalArrangement = Arrangement.spacedBy(DesignToken.Space.dp12)
                ) {
                    item { Spacer(modifier = Modifier.height(DesignToken.Space.dp4)) }

                    items(
                        items = watchHistoryItems,
                        key = { it.id }
                    ) { item ->
                        SwipeToDismissWatchHistoryItem(
                            item = item,
                            onDelete = { viewModel.deleteWatchHistory(item.id) },
                            onResumeClick = { onResumeVideo(item.videoId, item.courseId) }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(DesignToken.Space.dp16)) }
                }
            }
        }
    }

    // Clear all confirmation dialog
    if (uiState.showClearAllDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissClearAllDialog() },
            title = {
                Text(
                    text = "Clear all watch history?",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            },
            text = {
                Text(
                    text = "This will permanently delete all your watch history. " +
                        "You won't be able to resume videos from where you left off.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Neutral500
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.clearAllWatchHistory() }) {
                    Text(
                        text = "Clear All",
                        color = Error,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissClearAllDialog() }) {
                    Text(
                        text = "Cancel",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        )
    }
}

@Composable
private fun SwipeToDismissWatchHistoryItem(
    item: WatchHistoryItem,
    onDelete: () -> Unit,
    onResumeClick: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState()

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    SwipeToDismissBoxValue.EndToStart -> Error.copy(alpha = 0.3f)
                    else -> Color.Transparent
                },
                label = "dismiss_bg"
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color, MaterialTheme.shapes.medium)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = "Delete",
                    tint = Error
                )
            }
        },
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        onDismiss = { onDelete() }
    ) {
        WatchHistoryItemCard(
            item = item,
            onResumeClick = onResumeClick
        )
    }
}

@Composable
private fun WatchHistoryShimmerContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = DesignToken.Space.dp16),
        verticalArrangement = Arrangement.spacedBy(DesignToken.Space.dp12)
    ) {
        Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
        repeat(5) {
            Row(modifier = Modifier.fillMaxWidth()) {
                ShimmerEffect(
                    modifier = Modifier
                        .size(width = 120.dp, height = 72.dp)
                        .clip(MaterialTheme.shapes.small)
                )
                Spacer(modifier = Modifier.width(DesignToken.Space.dp12))
                Column(modifier = Modifier.weight(1f)) {
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
                    ShimmerEffect(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                    )
                }
            }
        }
    }
}
