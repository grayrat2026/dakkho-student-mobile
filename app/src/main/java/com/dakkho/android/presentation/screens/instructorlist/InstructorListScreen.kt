package com.dakkho.android.presentation.screens.instructorlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.dakkho.android.R
import com.dakkho.android.domain.model.Instructor
import com.dakkho.android.presentation.components.AnimatedPage
import com.dakkho.android.presentation.components.EmptyState
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.components.ShimmerEffect
import com.dakkho.android.presentation.components.DakkhoSearchBar
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.Neutral400
import com.dakkho.android.presentation.theme.Neutral500
import com.dakkho.android.presentation.theme.SkyBlue
import com.dakkho.android.presentation.theme.Warning

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstructorListScreen(
    onBackClick: () -> Unit,
    onInstructorClick: (String) -> Unit,
    viewModel: InstructorListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val listState = rememberLazyListState()

    // Auto-load more when near bottom
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = listState.layoutInfo.totalItemsCount
            lastVisibleItem >= totalItems - 3
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            viewModel.loadMore()
        }
    }

    AnimatedPage {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Instructors",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                painter = painterResource(id = android.R.drawable.ic_menu_revert),
                                contentDescription = "Back"
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Search bar
                DakkhoSearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = viewModel::onSearchQueryChanged,
                    onSearch = { viewModel.loadInstructors(reset = true) },
                    onClear = { viewModel.onSearchQueryChanged("") },
                    placeholder = "Search instructors...",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = DesignToken.Space.dp16, vertical = DesignToken.Space.dp8)
                )

                // Results count
                if (uiState.searchQuery.isNotBlank() && !uiState.isLoading) {
                    Text(
                        text = "${uiState.totalInstructors} result${if (uiState.totalInstructors != 1) "s" else ""}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Neutral500,
                        modifier = Modifier.padding(horizontal = DesignToken.Space.dp16)
                    )
                    Spacer(modifier = Modifier.height(DesignToken.Space.dp4))
                }

                // Instructor list
                when {
                    uiState.isLoading && uiState.instructors.isEmpty() -> {
                        ShimmerInstructorList()
                    }
                    uiState.error != null && uiState.instructors.isEmpty() -> {
                        EmptyState(
                            icon = painterResource(id = android.R.drawable.ic_dialog_alert),
                            title = "Could not load instructors",
                            subtitle = uiState.error,
                            onActionClick = { viewModel.loadInstructors(reset = true) },
                            actionLabel = "Retry"
                        )
                    }
                    uiState.instructors.isEmpty() && !uiState.isLoading -> {
                        EmptyState(
                            icon = painterResource(id = android.R.drawable.ic_menu_search),
                            title = if (uiState.searchQuery.isNotBlank()) "No instructors found" else "No instructors yet",
                            subtitle = if (uiState.searchQuery.isNotBlank()) "Try a different search term" else "Check back later for new instructors"
                        )
                    }
                    else -> {
                        LazyColumn(
                            state = listState,
                            contentPadding = PaddingValues(
                                horizontal = DesignToken.Space.dp16,
                                vertical = DesignToken.Space.dp8
                            ),
                            verticalArrangement = Arrangement.spacedBy(DesignToken.Space.dp12)
                        ) {
                            items(
                                items = uiState.instructors,
                                key = { it.id }
                            ) { instructor ->
                                InstructorListItemCard(
                                    instructor = instructor,
                                    onClick = { onInstructorClick(instructor.id) }
                                )
                            }

                            // Loading more indicator
                            if (uiState.isLoadingMore) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(DesignToken.Space.dp16),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            strokeWidth = 2.dp,
                                            color = SkyBlue
                                        )
                                    }
                                }
                            }

                            // Bottom spacer
                            item {
                                Spacer(modifier = Modifier.height(DesignToken.Space.dp16))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InstructorListItemCard(
    instructor: Instructor,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignToken.Space.dp12),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier.size(56.dp),
                contentAlignment = Alignment.Center
            ) {
                if (instructor.avatarUrl != null) {
                    AsyncImage(
                        model = instructor.avatarUrl,
                        contentDescription = instructor.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                    )
                } else {
                    // Fallback: initial circle
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(SkyBlue.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = instructor.name.take(1).uppercase(),
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = SkyBlue
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(DesignToken.Space.dp12))

            // Info column
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = instructor.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (instructor.title != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = instructor.title,
                        style = MaterialTheme.typography.bodySmall,
                        color = Neutral500,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(DesignToken.Space.dp4))

                // Stats row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(DesignToken.Space.dp12),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Course count
                    Text(
                        text = "${instructor.courseCount} courses",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = SkyBlue
                    )

                    // Student count
                    if (instructor.studentCount > 0) {
                        Text(
                            text = "${formatStudentCount(instructor.studentCount)} students",
                            style = MaterialTheme.typography.labelSmall,
                            color = Neutral500
                        )
                    }

                    // Rating
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.btn_star_big_on),
                            contentDescription = "Rating",
                            tint = Warning,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = String.format("%.1f", instructor.rating),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Chevron
            Icon(
                painter = painterResource(id = android.R.drawable.ic_media_play),
                contentDescription = "View",
                tint = Neutral400,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun ShimmerInstructorList() {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = DesignToken.Space.dp16, vertical = DesignToken.Space.dp8),
        verticalArrangement = Arrangement.spacedBy(DesignToken.Space.dp12)
    ) {
        items(6) {
            GlassCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(DesignToken.Space.dp12),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ShimmerEffect(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(DesignToken.Space.dp12))
                    Column {
                        ShimmerEffect(
                            modifier = Modifier
                                .width(160.dp)
                                .height(16.dp)
                        )
                        Spacer(modifier = Modifier.height(DesignToken.Space.dp4))
                        ShimmerEffect(
                            modifier = Modifier
                                .width(120.dp)
                                .height(12.dp)
                        )
                        Spacer(modifier = Modifier.height(DesignToken.Space.dp4))
                        ShimmerEffect(
                            modifier = Modifier
                                .width(200.dp)
                                .height(10.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun formatStudentCount(count: Int): String {
    return when {
        count >= 1000 -> String.format("%.1fk", count / 1000f)
        else -> count.toString()
    }
}
