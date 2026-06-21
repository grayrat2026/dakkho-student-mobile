package com.dakkho.android.presentation.screens.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dakkho.android.data.db.entity.SearchSuggestionEntity
import com.dakkho.android.domain.model.SearchResult
import com.dakkho.android.presentation.components.AnimatedPage
import com.dakkho.android.presentation.components.EmptyState
import com.dakkho.android.presentation.components.ShimmerEffect
import com.dakkho.android.presentation.components.search.DakkhoSearchBar
import com.dakkho.android.presentation.components.search.RecentSearchesRow
import com.dakkho.android.presentation.components.search.SearchResultItem
import com.dakkho.android.presentation.components.search.SuggestionItem
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.Neutral400

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigateToCourse: (String) -> Unit,
    onNavigateToInstructor: (String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    AnimatedPage {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Search bar
                Box(
                    modifier = Modifier.padding(
                        horizontal = DesignToken.Space.dp16,
                        vertical = DesignToken.Space.dp12
                    )
                ) {
                    DakkhoSearchBar(
                        query = uiState.query,
                        onQueryChange = { viewModel.onQueryChanged(it) },
                        onSearch = {
                            keyboardController?.hide()
                            viewModel.performSearch()
                        },
                        onClear = { viewModel.onQueryChanged("") },
                        focusRequester = focusRequester
                    )
                }

                // Main content area
                when {
                    // Loading state
                    uiState.isSearching -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 3.dp
                            )
                        }
                    }

                    // Show suggestions when user is typing (before search)
                    uiState.suggestions.isNotEmpty() -> {
                        Text(
                            text = "Suggestions",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier.padding(
                                horizontal = DesignToken.Space.dp16,
                                vertical = DesignToken.Space.dp8
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                count = uiState.suggestions.size,
                                key = { index -> uiState.suggestions[index].rowId }
                            ) { index ->
                                SuggestionItem(
                                    suggestion = uiState.suggestions[index],
                                    onClick = { suggestion ->
                                        keyboardController?.hide()
                                        viewModel.selectSuggestion(suggestion)
                                    }
                                )
                            }
                        }
                    }

                    // Show search results after performing search
                    uiState.hasSearched -> {
                        val courseResults = uiState.courseResults
                        val instructorResults = uiState.instructorResults
                        val hasResults = courseResults.isNotEmpty() || instructorResults.isNotEmpty()

                        if (hasResults) {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                // Courses section header
                                if (courseResults.isNotEmpty()) {
                                    item {
                                        Text(
                                            text = "Courses (${courseResults.size})",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            modifier = Modifier.padding(
                                                horizontal = DesignToken.Space.dp16,
                                                vertical = DesignToken.Space.dp8
                                            ),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    items(
                                        count = courseResults.size,
                                        key = { index -> courseResults[index].id }
                                    ) { index ->
                                        val course = courseResults[index]
                                        SearchResultItem(
                                            result = SearchResult(
                                                id = course.id,
                                                title = course.title,
                                                description = course.instructorName,
                                                thumbnailUrl = course.thumbnailUrl,
                                                type = "course",
                                                rating = course.rating,
                                                price = course.discountedPrice ?: course.price
                                            ),
                                            onClick = { onNavigateToCourse(it.id) }
                                        )
                                    }
                                }

                                // Instructors section header
                                if (instructorResults.isNotEmpty()) {
                                    item {
                                        Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
                                        Text(
                                            text = "Instructors (${instructorResults.size})",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            modifier = Modifier.padding(
                                                horizontal = DesignToken.Space.dp16,
                                                vertical = DesignToken.Space.dp8
                                            ),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    items(
                                        count = instructorResults.size,
                                        key = { index -> instructorResults[index].id }
                                    ) { index ->
                                        val instructor = instructorResults[index]
                                        SearchResultItem(
                                            result = SearchResult(
                                                id = instructor.id,
                                                title = instructor.name,
                                                description = instructor.title,
                                                thumbnailUrl = instructor.avatarUrl,
                                                type = "instructor",
                                                rating = instructor.rating
                                            ),
                                            onClick = { onNavigateToInstructor(it.id) }
                                        )
                                    }
                                }

                                // Bottom spacing
                                item {
                                    Spacer(modifier = Modifier.height(DesignToken.Space.dp32))
                                }
                            }
                        } else {
                            // Empty state — no results
                            EmptyState(
                                title = "No results found",
                                subtitle = "No results for '${uiState.query}'. Try different keywords or browse our courses.",
                                actionText = "Explore Courses",
                                onAction = onBackClick
                            )
                        }
                    }

                    // Default state — show recent searches
                    else -> {
                        if (uiState.recentSearches.isNotEmpty()) {
                            RecentSearchesRow(
                                recentSearches = uiState.recentSearches,
                                onSearchClick = { query ->
                                    viewModel.selectRecentSearch(query)
                                },
                                onDeleteClick = { id ->
                                    viewModel.deleteSearchHistoryItem(id)
                                },
                                onClearAllClick = {
                                    viewModel.showClearHistoryConfirmation()
                                }
                            )
                        } else {
                            // No recent searches — show search prompt
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "What do you want to learn?",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
                                    Text(
                                        text = "Search for courses, instructors, or topics",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Neutral400,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Clear history confirmation dialog
        if (uiState.showClearConfirmation) {
            AlertDialog(
                onDismissRequest = { viewModel.dismissClearHistoryConfirmation() },
                title = {
                    Text(text = "Clear Search History")
                },
                text = {
                    Text(text = "Are you sure you want to clear all search history? This action cannot be undone.")
                },
                confirmButton = {
                    TextButton(onClick = { viewModel.clearAllSearchHistory() }) {
                        Text(
                            text = "Clear All",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.dismissClearHistoryConfirmation() }) {
                        Text(text = "Cancel")
                    }
                }
            )
        }
    }
}
