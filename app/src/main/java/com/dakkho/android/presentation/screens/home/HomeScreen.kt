package com.dakkho.android.presentation.screens.home

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dakkho.android.presentation.components.AnimatedPage
import com.dakkho.android.presentation.components.DakkhoTopBar
import com.dakkho.android.presentation.components.home.CategoryPills
import com.dakkho.android.presentation.components.home.ContinueWatching
import com.dakkho.android.presentation.components.home.FeaturedInstructors
import com.dakkho.android.presentation.components.home.HeroSection
import com.dakkho.android.presentation.components.home.TrendingCourses
import com.dakkho.android.presentation.theme.DesignToken

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToCourse: (String) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToVideo: (String, String) -> Unit,
    onNavigateToInstructor: (String) -> Unit,
    onNavigateToInstructorList: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedTechnology by viewModel.selectedTechnology.collectAsStateWithLifecycle()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    AnimatedPage {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                DakkhoTopBar(
                    showSearch = true,
                    showNotification = true,
                    showAvatar = true,
                    notificationCount = 0,
                    onSearchClick = onNavigateToSearch,
                    onNotificationClick = onNavigateToNotifications,
                    onAvatarClick = { /* Navigate to profile */ },
                    scrollBehavior = scrollBehavior
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                onRefresh = { viewModel.refresh() },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // 1. Hero Section
                    item {
                        Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
                        HeroSection(
                            hasEnrollments = uiState.hasEnrollments,
                            lastWatchedCourse = viewModel.getLastWatchedCourse(),
                            watchProgress = viewModel.getLastWatchProgress(),
                            onResumeClick = {
                                val history = uiState.continueWatching.firstOrNull()
                                if (history != null) {
                                    onNavigateToVideo(history.videoId, history.courseId)
                                }
                            },
                            onExploreClick = onNavigateToSearch
                        )
                    }

                    // 2. Category Pills
                    item {
                        Spacer(modifier = Modifier.height(DesignToken.Space.dp16))
                        CategoryPills(
                            technologies = uiState.technologies,
                            selectedTechnology = selectedTechnology,
                            onTechnologySelected = { viewModel.selectTechnology(it) }
                        )
                    }

                    // 3. Continue Watching (conditional)
                    if (uiState.continueWatching.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(DesignToken.Space.dp20))
                            ContinueWatching(
                                watchHistory = uiState.continueWatching,
                                courseMap = uiState.courseMap,
                                onVideoClick = onNavigateToVideo,
                                onSeeAllClick = { /* Navigate to WatchHistory */ }
                            )
                        }
                    }

                    // 4. Trending Courses
                    item {
                        Spacer(modifier = Modifier.height(DesignToken.Space.dp20))
                        TrendingCourses(
                            courses = uiState.trendingCourses,
                            onCourseClick = onNavigateToCourse,
                            onSeeAllClick = onNavigateToSearch,
                            isLoading = uiState.isLoading
                        )
                    }

                    // 5. Featured Instructors
                    item {
                        Spacer(modifier = Modifier.height(DesignToken.Space.dp20))
                        FeaturedInstructors(
                            instructors = uiState.featuredInstructors,
                            onInstructorClick = onNavigateToInstructor,
                            onSeeAllClick = onNavigateToInstructorList,
                            isLoading = uiState.isLoading
                        )
                    }

                    // 6. Bottom spacing
                    item {
                        Spacer(modifier = Modifier.height(DesignToken.Space.dp32))
                    }
                }
            }
        }
    }
}
