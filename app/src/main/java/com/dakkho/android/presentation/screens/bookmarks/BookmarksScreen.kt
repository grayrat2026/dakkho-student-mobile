package com.dakkho.android.presentation.screens.bookmarks

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.dakkho.android.presentation.components.EmptyState
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.components.GradientButton
import com.dakkho.android.presentation.components.ShimmerEffect
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.Error
import com.dakkho.android.presentation.theme.Neutral500
import com.dakkho.android.presentation.theme.SkyBlue
import com.dakkho.android.presentation.theme.Warning

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(
    onBackClick: () -> Unit,
    onNavigateToCourse: (String) -> Unit = {},
    onNavigateToExplore: () -> Unit = {},
    viewModel: BookmarksViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "বুকমার্ক",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                BookmarksShimmer(modifier = Modifier.padding(innerPadding))
            }
            uiState.bookmarks.isEmpty() -> {
                EmptyState(
                    title = "কোনো বুকমার্ক নেই",
                    subtitle = "আপনি যে কোর্সগুলো বুকমার্ক করেছেন সেগুলো এখানে দেখা যাবে",
                    actionText = "কোর্স খুঁজুন",
                    onAction = onNavigateToExplore,
                    modifier = Modifier.padding(innerPadding)
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = DesignToken.Space.dp16),
                    verticalArrangement = Arrangement.spacedBy(DesignToken.Space.dp8)
                ) {
                    item {
                        Text(
                            text = "${uiState.bookmarks.size}টি কোর্স বুকমার্ক করা হয়েছে",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Neutral500,
                            modifier = Modifier.padding(vertical = DesignToken.Space.dp8)
                        )
                    }

                    items(
                        items = uiState.bookmarks,
                        key = { it.courseId }
                    ) { bookmark ->
                        BookmarkItemCard(
                            bookmark = bookmark,
                            onClick = { onNavigateToCourse(bookmark.courseId) },
                            onRemove = { viewModel.removeBookmark(bookmark.courseId) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(DesignToken.Space.dp16))
                    }
                }
            }
        }
    }
}

@Composable
private fun BookmarkItemCard(
    bookmark: BookmarkItem,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignToken.Space.dp12),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail
            if (!bookmark.thumbnailUrl.isNullOrBlank()) {
                AsyncImage(
                    model = bookmark.thumbnailUrl,
                    contentDescription = bookmark.title,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
            } else {
                GlassCard(
                    modifier = Modifier.size(80.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayCircleFilled,
                            contentDescription = null,
                            tint = SkyBlue,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(DesignToken.Space.dp12))

            // Course info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = bookmark.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (!bookmark.instructorName.isNullOrBlank()) {
                    Text(
                        text = bookmark.instructorName,
                        style = MaterialTheme.typography.bodySmall,
                        color = Neutral500
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (bookmark.rating != null && bookmark.rating > 0) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Warning,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = String.format("%.1f", bookmark.rating),
                            style = MaterialTheme.typography.labelSmall,
                            color = Warning,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }

                    if (!bookmark.technology.isNullOrBlank()) {
                        if (bookmark.rating != null && bookmark.rating > 0) {
                            Text(
                                text = " · ",
                                style = MaterialTheme.typography.labelSmall,
                                color = Neutral500
                            )
                        }
                        Text(
                            text = bookmark.technology,
                            style = MaterialTheme.typography.labelSmall,
                            color = SkyBlue
                        )
                    }
                }
            }

            // Remove button
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "বুকমার্ক সরান",
                    tint = Error.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun BookmarksShimmer(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(horizontal = DesignToken.Space.dp16)
    ) {
        repeat(5) {
            ShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
            )
            Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
        }
    }
}


