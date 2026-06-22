package com.dakkho.android.presentation.screens.downloads

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.dakkho.android.domain.model.DownloadItem
import com.dakkho.android.presentation.components.EmptyState
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.components.ShimmerEffect
import com.dakkho.android.presentation.theme.DeepBlue
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.Error
import com.dakkho.android.presentation.theme.Neutral400
import com.dakkho.android.presentation.theme.Neutral500
import com.dakkho.android.presentation.theme.SkyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadsScreen(
    onBackClick: () -> Unit,
    onPlayVideo: (videoId: String, courseId: String) -> Unit = { _, _ -> },
    onBrowseCourses: () -> Unit = {},
    viewModel: DownloadsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val downloadItems by viewModel.downloadItems.collectAsStateWithLifecycle()
    val storageUsed by viewModel.storageUsed.collectAsStateWithLifecycle()
    val storagePercent by viewModel.storagePercent.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Downloads",
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
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        when {
            uiState.isLoading && downloadItems.isEmpty() -> {
                DownloadsShimmerContent(modifier = Modifier.padding(innerPadding))
            }
            downloadItems.isEmpty() && !uiState.isLoading -> {
                EmptyState(
                    title = "No downloads yet",
                    subtitle = "Download videos to watch them offline, even without internet.",
                    iconRes = android.R.drawable.stat_sys_download_done,
                    actionText = "Browse Courses",
                    onAction = onBrowseCourses,
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
                    // Storage usage bar
                    item {
                        StorageUsageBar(
                            usedBytes = storageUsed,
                            totalBytes = uiState.totalStorageAvailable,
                            percent = storagePercent
                        )
                    }

                    item { Spacer(modifier = Modifier.height(DesignToken.Space.dp4)) }

                    // Active downloads section
                    val activeDownloads = downloadItems.filter { it.isDownloading || it.isPending }
                    if (activeDownloads.isNotEmpty()) {
                        item {
                            Text(
                                text = "Active Downloads",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = SkyBlue,
                                modifier = Modifier.padding(bottom = DesignToken.Space.dp4)
                            )
                        }
                        items(
                            items = activeDownloads,
                            key = { it.id }
                        ) { item ->
                            ActiveDownloadCard(
                                item = item,
                                onPlayClick = {
                                    if (item.isCompleted) {
                                        onPlayVideo(item.videoId, item.courseId)
                                    }
                                },
                                onCancelClick = { viewModel.cancelDownload(item.id) }
                            )
                        }
                    }

                    // Completed downloads section
                    val completedDownloads = downloadItems.filter { it.isCompleted }
                    if (completedDownloads.isNotEmpty()) {
                        item {
                            Text(
                                text = "Completed",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(
                                    top = if (activeDownloads.isNotEmpty()) DesignToken.Space.dp8 else 0.dp,
                                    bottom = DesignToken.Space.dp4
                                )
                            )
                        }
                        items(
                            items = completedDownloads,
                            key = { it.id }
                        ) { item ->
                            SwipeToDismissDownloadItem(
                                item = item,
                                onDelete = { viewModel.showDeleteDialog(item.id) },
                                onPlayClick = { onPlayVideo(item.videoId, item.courseId) }
                            )
                        }
                    }

                    // Failed downloads section
                    val failedDownloads = downloadItems.filter { it.isFailed }
                    if (failedDownloads.isNotEmpty()) {
                        item {
                            Text(
                                text = "Failed",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = Error,
                                modifier = Modifier.padding(
                                    top = if (activeDownloads.isNotEmpty() || completedDownloads.isNotEmpty())
                                        DesignToken.Space.dp8 else 0.dp,
                                    bottom = DesignToken.Space.dp4
                                )
                            )
                        }
                        items(
                            items = failedDownloads,
                            key = { it.id }
                        ) { item ->
                            SwipeToDismissDownloadItem(
                                item = item,
                                onDelete = { viewModel.showDeleteDialog(item.id) },
                                onPlayClick = {}
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(DesignToken.Space.dp16)) }
                }
            }
        }
    }

    // Delete confirmation dialog
    if (uiState.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissDeleteDialog() },
            title = {
                Text(
                    text = "Delete download?",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            },
            text = {
                Text(
                    text = "This will permanently delete the downloaded video file from your device. " +
                        "You'll need to download it again to watch offline.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Neutral500
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.confirmDelete() }) {
                    Text(
                        text = "Delete",
                        color = Error,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissDeleteDialog() }) {
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
private fun StorageUsageBar(
    usedBytes: Long,
    totalBytes: Long,
    percent: Int,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Storage",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(DesignToken.Space.dp4))
                Text(
                    text = "${DownloadItem.formatFileSize(usedBytes)} used of ${DownloadItem.formatFileSize(totalBytes)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Neutral500
                )
            }

            Text(
                text = "$percent%",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = if (percent > 80) Error else SkyBlue
            )
        }

        Spacer(modifier = Modifier.height(DesignToken.Space.dp12))

        // Canvas-drawn storage bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .drawBehind {
                    // Background
                    drawRect(
                        color = Neutral400.copy(alpha = 0.3f)
                    )
                    // Fill
                    val fillWidth = size.width * (percent / 100f)
                    val fillColor = when {
                        percent > 80 -> Error
                        percent > 50 -> Color(0xFFF59E0B) // Warning
                        else -> SkyBlue
                    }
                    drawRect(
                        color = fillColor,
                        topLeft = Offset.Zero,
                        size = Size(fillWidth, size.height)
                    )
                }
        )
    }
}

@Composable
private fun ActiveDownloadCard(
    item: DownloadItem,
    onPlayClick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail
            AsyncImage(
                model = item.thumbnailUrl,
                contentDescription = item.title,
                modifier = Modifier
                    .size(width = 100.dp, height = 56.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(DesignToken.Space.dp12))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(DesignToken.Space.dp4))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(DesignToken.Space.dp8)
                ) {
                    if (item.isPending) {
                        Text(
                            text = "Queued",
                            style = MaterialTheme.typography.bodySmall,
                            color = Neutral500
                        )
                    } else {
                        LinearProgressIndicator(
                            progress = { item.progressPercent / 100f },
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = SkyBlue,
                            trackColor = Neutral400.copy(alpha = 0.3f)
                        )
                        Text(
                            text = "${item.progressPercent}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = SkyBlue,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(DesignToken.Space.dp2))
                Text(
                    text = "${item.fileSizeDisplay} • ${item.qualityBadge}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Neutral500
                )
            }

            Spacer(modifier = Modifier.width(DesignToken.Space.dp8))

            if (item.isDownloading) {
                IconButton(onClick = onCancelClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Cancel",
                        tint = Error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DownloadedVideoCard(
    item: DownloadItem,
    onPlayClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onPlayClick),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail with play overlay
            Box(
                modifier = Modifier.size(width = 100.dp, height = 56.dp)
            ) {
                AsyncImage(
                    model = item.thumbnailUrl,
                    contentDescription = item.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp))
                )
                if (item.isCompleted) {
                    Icon(
                        imageVector = Icons.Default.PlayCircleOutline,
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.Center)
                    )
                }
            }

            Spacer(modifier = Modifier.width(DesignToken.Space.dp12))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(DesignToken.Space.dp4))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(DesignToken.Space.dp8)
                ) {
                    Text(
                        text = item.fileSizeDisplay,
                        style = MaterialTheme.typography.bodySmall,
                        color = Neutral500
                    )

                    // Quality badge
                    Box(
                        modifier = Modifier
                            .background(
                                color = when (item.qualityBadge) {
                                    "1080p", "4K" -> DeepBlue.copy(alpha = 0.8f)
                                    "720p" -> SkyBlue.copy(alpha = 0.2f)
                                    else -> Neutral400.copy(alpha = 0.2f)
                                },
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = item.qualityBadge,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = when (item.qualityBadge) {
                                "1080p", "4K" -> Color.White
                                "720p" -> SkyBlue
                                else -> Neutral500
                            }
                        )
                    }

                    if (item.isFailed) {
                        Text(
                            text = "Failed",
                            style = MaterialTheme.typography.labelSmall,
                            color = Error,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            if (item.isCompleted) {
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Neutral400,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SwipeToDismissDownloadItem(
    item: DownloadItem,
    onDelete: () -> Unit,
    onPlayClick: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            when (dismissValue) {
                SwipeToDismissBoxValue.EndToStart -> {
                    onDelete()
                    true
                }
                else -> false
            }
        }
    )

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
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Error
                )
            }
        },
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true
    ) {
        DownloadedVideoCard(
            item = item,
            onPlayClick = onPlayClick,
            onDeleteClick = onDelete
        )
    }
}

@Composable
private fun DownloadsShimmerContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = DesignToken.Space.dp16),
        verticalArrangement = Arrangement.spacedBy(DesignToken.Space.dp12)
    ) {
        Spacer(modifier = Modifier.height(DesignToken.Space.dp8))

        // Storage bar shimmer
        GlassCard {
            ShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            )
        }

        repeat(5) {
            Row(modifier = Modifier.fillMaxWidth()) {
                ShimmerEffect(
                    modifier = Modifier
                        .size(width = 100.dp, height = 56.dp)
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
