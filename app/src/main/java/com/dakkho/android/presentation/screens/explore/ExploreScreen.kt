package com.dakkho.android.presentation.screens.explore

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.dakkho.android.domain.model.PriceType
import com.dakkho.android.presentation.components.AnimatedPage
import com.dakkho.android.presentation.components.DakkhoTopBar
import com.dakkho.android.presentation.components.EmptyState
import com.dakkho.android.presentation.components.ShimmerEffect
import com.dakkho.android.presentation.components.explore.ExploreCourseCard
import com.dakkho.android.presentation.components.explore.ExploreSearchBar
import com.dakkho.android.presentation.components.explore.FilterChipsRow
import com.dakkho.android.presentation.components.explore.SortDropdown
import com.dakkho.android.presentation.theme.DesignToken
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    onNavigateToCourse: (String) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onBookmarkClick: (String) -> Unit,
    viewModel: ExploreViewModel = hiltViewModel()
) {
    val filters by viewModel.filters.collectAsStateWithLifecycle()
    val technologies by viewModel.technologies.collectAsStateWithLifecycle()
    val isSearchExpanded by viewModel.isSearchExpanded.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    val pagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    val searchItems = viewModel.searchPagingFlow.collectAsLazyPagingItems()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val gridState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()

    val showScrollToTop by remember {
        derivedStateOf {
            gridState.firstVisibleItemIndex > 4
        }
    }

    // Determine which paging items to show
    val isSearching = searchQuery.isNotBlank()
    val activeItems = if (isSearching) searchItems else pagingItems

    AnimatedPage {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                DakkhoTopBar(
                    title = "Explore",
                    showSearch = false,
                    showNotification = true,
                    showAvatar = false,
                    notificationCount = 0,
                    onSearchClick = onNavigateToSearch,
                    onNotificationClick = onNavigateToNotifications,
                    onAvatarClick = {},
                    scrollBehavior = scrollBehavior
                )
            },
            floatingActionButton = {
                if (showScrollToTop) {
                    FloatingActionButton(
                        onClick = {
                            coroutineScope.launch {
                                gridState.animateScrollToItem(0)
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.large
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = "Scroll to top"
                        )
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Search bar
                ExploreSearchBar(
                    query = searchQuery,
                    isExpanded = isSearchExpanded,
                    onQueryChange = { viewModel.setSearchQuery(it) },
                    onToggleExpand = { viewModel.toggleSearchExpanded() },
                    onSearchClick = onNavigateToSearch
                )

                // Filter chips row + Sort dropdown
                Row(
                    modifier = Modifier.padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChipsRow(
                        technologies = technologies,
                        selectedTechnology = filters.technology,
                        selectedLevel = filters.level,
                        selectedPriceType = filters.priceType,
                        onTechnologySelected = { viewModel.setTechnology(it) },
                        onLevelSelected = { viewModel.setLevel(it) },
                        onPriceTypeSelected = { viewModel.setPriceType(it) },
                        modifier = Modifier.weight(1f)
                    )

                    SortDropdown(
                        currentSort = filters.sortBy,
                        onSortSelected = { viewModel.setSortBy(it) }
                    )
                }

                // Clear filters button (when any filter is active)
                val hasActiveFilters = filters.technology != null ||
                        filters.level != null ||
                        filters.priceType != PriceType.ALL

                if (hasActiveFilters) {
                    Row(
                        modifier = Modifier.padding(horizontal = DesignToken.Space.dp16),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { viewModel.clearFilters() }) {
                            Text(
                                text = "Clear Filters",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Course Grid
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    when {
                        activeItems.loadState.refresh is LoadState.Loading && activeItems.itemCount == 0 -> {
                            ShimmerGrid()
                        }
                        activeItems.loadState.refresh is LoadState.Error && activeItems.itemCount == 0 -> {
                            val error = (activeItems.loadState.refresh as LoadState.Error).error
                            EmptyState(
                                title = "Something went wrong",
                                subtitle = error.message ?: "Failed to load courses",
                                actionText = "Retry",
                                onAction = { activeItems.retry() }
                            )
                        }
                        activeItems.itemCount == 0 -> {
                            EmptyState(
                                title = "No courses found",
                                subtitle = "Try different filters or search terms",
                                actionText = "Clear Filters",
                                onAction = { viewModel.clearFilters() }
                            )
                        }
                        else -> {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                state = gridState,
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(
                                    start = DesignToken.Space.dp16,
                                    end = DesignToken.Space.dp16,
                                    top = DesignToken.Space.dp8,
                                    bottom = DesignToken.Space.dp32
                                )
                            ) {
                                items(
                                    count = activeItems.itemCount,
                                    key = { index -> activeItems[index]?.id ?: index }
                                ) { index ->
                                    activeItems[index]?.let { course ->
                                        ExploreCourseCard(
                                            course = course,
                                            onClick = onNavigateToCourse,
                                            onBookmarkClick = onBookmarkClick
                                        )
                                    }
                                }

                                // Loading more indicator
                                if (activeItems.loadState.append is LoadState.Loading) {
                                    item(span = { GridItemSpan(2) }) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.padding(16.dp),
                                                color = MaterialTheme.colorScheme.primary,
                                                strokeWidth = 2.dp
                                            )
                                        }
                                    }
                                }

                                // Append error
                                if (activeItems.loadState.append is LoadState.Error) {
                                    item(span = { GridItemSpan(2) }) {
                                        TextButton(
                                            onClick = { activeItems.retry() },
                                            modifier = Modifier.padding(16.dp)
                                        ) {
                                            Text(
                                                text = "Load more failed. Tap to retry.",
                                                color = MaterialTheme.colorScheme.error
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
    }
}

@Composable
private fun ShimmerGrid() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = DesignToken.Space.dp16, vertical = DesignToken.Space.dp8),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(6) {
            ShimmerEffect(
                modifier = Modifier
                    .fillMaxSize()
                    .height(200.dp)
            )
        }
    }
}
