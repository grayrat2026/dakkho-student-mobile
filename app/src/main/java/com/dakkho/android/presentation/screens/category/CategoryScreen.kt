package com.dakkho.android.presentation.screens.category

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.dakkho.android.presentation.components.AnimatedPage
import com.dakkho.android.presentation.components.DakkhoTopBar
import com.dakkho.android.presentation.components.EmptyState
import com.dakkho.android.presentation.components.ShimmerEffect
import com.dakkho.android.presentation.components.explore.ExploreCourseCard
import com.dakkho.android.presentation.theme.DesignToken

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    onBackClick: () -> Unit,
    onNavigateToCourse: (String) -> Unit,
    viewModel: CategoryViewModel = hiltViewModel()
) {
    val pagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    AnimatedPage {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                DakkhoTopBar(
                    title = viewModel.technology,
                    showSearch = false,
                    showNotification = false,
                    showAvatar = false,
                    onBackClick = onBackClick,
                    scrollBehavior = scrollBehavior
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    when {
                        pagingItems.loadState.refresh is LoadState.Loading && pagingItems.itemCount == 0 -> {
                            CategoryShimmerGrid()
                        }
                        pagingItems.loadState.refresh is LoadState.Error && pagingItems.itemCount == 0 -> {
                            val error = (pagingItems.loadState.refresh as LoadState.Error).error
                            EmptyState(
                                title = "Something went wrong",
                                subtitle = error.message ?: "Failed to load courses",
                                actionText = "Retry",
                                onAction = { pagingItems.retry() }
                            )
                        }
                        pagingItems.itemCount == 0 -> {
                            EmptyState(
                                title = "No courses found",
                                subtitle = "No courses available in ${viewModel.technology}",
                                actionText = "Go Back",
                                onAction = onBackClick
                            )
                        }
                        else -> {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
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
                                    count = pagingItems.itemCount,
                                    key = { index -> pagingItems[index]?.id ?: index }
                                ) { index ->
                                    pagingItems[index]?.let { course ->
                                        ExploreCourseCard(
                                            course = course,
                                            onClick = onNavigateToCourse,
                                            onBookmarkClick = { }
                                        )
                                    }
                                }

                                // Loading more indicator
                                if (pagingItems.loadState.append is LoadState.Loading) {
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
                                if (pagingItems.loadState.append is LoadState.Error) {
                                    item(span = { GridItemSpan(2) }) {
                                        TextButton(
                                            onClick = { pagingItems.retry() },
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
private fun CategoryShimmerGrid() {
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
