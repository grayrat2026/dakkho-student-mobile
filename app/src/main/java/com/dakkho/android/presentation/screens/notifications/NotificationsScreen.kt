package com.dakkho.android.presentation.screens.notifications

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.dakkho.android.domain.model.NotificationItem
import com.dakkho.android.presentation.components.DakkhoTopBar
import com.dakkho.android.presentation.components.ListSkeleton
import com.dakkho.android.presentation.components.notifications.NotificationEmptyState
import com.dakkho.android.presentation.components.notifications.NotificationItemCard
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.Error
import com.dakkho.android.presentation.theme.Green
import com.dakkho.android.presentation.theme.Neutral500

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onBackClick: () -> Unit,
    onNotificationClick: (NotificationItem) -> Unit,
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val pagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top bar
        DakkhoTopBar(
            title = "Notifications",
            showSearch = false,
            showNotification = false,
            showAvatar = false,
            onBackClick = onBackClick
        )

        // Mark all read action row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = DesignToken.Space.dp16),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = { viewModel.markAllAsRead() }
            ) {
                Text(
                    text = "Mark all read",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Content
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.fillMaxSize()
        ) {
            when {
                pagingItems.loadState.refresh is LoadState.Loading && pagingItems.itemCount == 0 -> {
                    // Shimmer loading state
                    ListSkeleton(
                        itemCount = 5,
                        modifier = Modifier.padding(DesignToken.Space.dp16)
                    )
                }
                pagingItems.loadState.refresh is LoadState.Error -> {
                    // Error state
                    NotificationEmptyState(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(DesignToken.Space.dp16)
                    )
                }
                pagingItems.itemCount == 0 -> {
                    // Empty state
                    NotificationEmptyState(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(DesignToken.Space.dp16)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(DesignToken.Space.dp8),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                            start = DesignToken.Space.dp16,
                            end = DesignToken.Space.dp16,
                            top = DesignToken.Space.dp8,
                            bottom = DesignToken.Space.dp16
                        )
                    ) {
                        items(
                            count = pagingItems.itemCount,
                            key = { index -> pagingItems[index]?.id ?: index }
                        ) { index ->
                            val notification = pagingItems[index]
                            if (notification != null) {
                                SwipeToDismissNotificationItem(
                                    notification = notification,
                                    onMarkRead = { viewModel.markAsRead(notification.id) },
                                    onDelete = { viewModel.deleteNotification(notification.id) },
                                    onClick = { onNotificationClick(notification) }
                                )
                            }
                        }

                        // Loading more indicator
                        if (pagingItems.loadState.append is LoadState.Loading) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(DesignToken.Space.dp16),
                                    contentAlignment = Alignment.Center
                                ) {
                                    androidx.compose.material3.CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        strokeWidth = 2.dp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDismissNotificationItem(
    notification: NotificationItem,
    onMarkRead: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            when (dismissValue) {
                SwipeToDismissBoxValue.EndToStart -> {
                    // Swipe left → delete
                    onDelete()
                    true
                }
                SwipeToDismissBoxValue.StartToEnd -> {
                    // Swipe right → mark as read (if unread), or delete (if already read)
                    if (!notification.isRead) {
                        onMarkRead()
                    } else {
                        onDelete()
                    }
                    true
                }
                SwipeToDismissBoxValue.Settled -> false
            }
        },
        positionalThreshold = { totalDistance -> totalDistance * 0.4f }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    SwipeToDismissBoxValue.EndToStart -> Error.copy(alpha = 0.3f)
                    SwipeToDismissBoxValue.StartToEnd -> if (!notification.isRead) Green.copy(alpha = 0.3f) else Error.copy(alpha = 0.3f)
                    SwipeToDismissBoxValue.Settled -> Color.Transparent
                },
                label = "dismiss_bg_color"
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(DesignToken.Shape.medium)
                    .background(color)
                    .padding(horizontal = DesignToken.Space.dp20),
                contentAlignment = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                    Alignment.CenterEnd
                } else {
                    Alignment.CenterStart
                }
            ) {
                Icon(
                    imageVector = if (dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd && !notification.isRead) {
                        Icons.Default.Done
                    } else {
                        Icons.Default.Delete
                    },
                    contentDescription = if (dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd && !notification.isRead) {
                        "Mark as read"
                    } else {
                        "Delete"
                    },
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = true
    ) {
        NotificationItemCard(
            notification = notification,
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
        )
    }
}
