package com.dakkho.android.presentation.screens.profile

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dakkho.android.presentation.components.EmptyState
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.components.ShimmerEffect
import com.dakkho.android.presentation.components.profile.ProfileHeader
import com.dakkho.android.presentation.components.profile.ProfileMenuItem
import com.dakkho.android.presentation.components.profile.StatsCard
import com.dakkho.android.presentation.theme.DeepBlue
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.Error
import com.dakkho.android.presentation.theme.SkyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToEditProfile: () -> Unit,
    onNavigateToChangePassword: () -> Unit,
    onNavigateToLearningStats: () -> Unit,
    onNavigateToSubscription: () -> Unit,
    onNavigateToReferral: () -> Unit,
    onNavigateToDownloads: () -> Unit,
    onNavigateToBookmarks: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
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
                ProfileShimmerContent(modifier = Modifier.padding(innerPadding))
            }
            uiState.error != null -> {
                EmptyState(
                    title = "Failed to load profile",
                    subtitle = uiState.error ?: "An unexpected error occurred",
                    actionText = "Retry",
                    onAction = { viewModel.loadProfile() },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            uiState.user != null -> {
                ProfileContent(
                    uiState = uiState,
                    onNavigateToEditProfile = onNavigateToEditProfile,
                    onNavigateToChangePassword = onNavigateToChangePassword,
                    onNavigateToLearningStats = onNavigateToLearningStats,
                    onNavigateToSubscription = onNavigateToSubscription,
                    onNavigateToReferral = onNavigateToReferral,
                    onNavigateToDownloads = onNavigateToDownloads,
                    onNavigateToBookmarks = onNavigateToBookmarks,
                    onNavigateToSettings = onNavigateToSettings,
                    onNavigateToAbout = onNavigateToAbout,
                    onLogout = {
                        viewModel.logout()
                        onNavigateToLogin()
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun ProfileContent(
    uiState: ProfileUiState,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToChangePassword: () -> Unit,
    onNavigateToLearningStats: () -> Unit,
    onNavigateToSubscription: () -> Unit,
    onNavigateToReferral: () -> Unit,
    onNavigateToDownloads: () -> Unit,
    onNavigateToBookmarks: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val user = uiState.user ?: return
    val stats = uiState.stats

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Parallax header area with gradient background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(SkyBlue, DeepBlue)
                    )
                )
                .padding(vertical = DesignToken.Space.dp24),
            contentAlignment = Alignment.Center
        ) {
            ProfileHeader(
                name = user.fullName,
                technology = user.technology,
                instituteName = null,
                avatarUrl = user.avatarUrl
            )
        }

        // Quick stats row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = DesignToken.Space.dp16, vertical = DesignToken.Space.dp12),
            horizontalArrangement = Arrangement.spacedBy(DesignToken.Space.dp8)
        ) {
            StatsCard(
                label = "Courses",
                value = stats.coursesEnrolled,
                icon = Icons.Default.MenuBook,
                modifier = Modifier.weight(1f)
            )
            StatsCard(
                label = "Hours",
                value = stats.hoursWatched,
                icon = Icons.Default.Schedule,
                modifier = Modifier.weight(1f)
            )
            StatsCard(
                label = "Streak",
                value = stats.streakDays,
                icon = Icons.Default.Whatshot,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(DesignToken.Space.dp8))

        // Menu items section
        GlassCard(modifier = Modifier.padding(horizontal = DesignToken.Space.dp16)) {
            ProfileMenuItem(
                icon = Icons.Default.Edit,
                label = "Edit Profile",
                onClick = onNavigateToEditProfile
            )
            ProfileMenuItem(
                icon = Icons.Default.Lock,
                label = "Change Password",
                onClick = onNavigateToChangePassword
            )
            ProfileMenuItem(
                icon = Icons.Default.BarChart,
                label = "Learning Stats",
                onClick = onNavigateToLearningStats
            )
            ProfileMenuItem(
                icon = Icons.Default.CardMembership,
                label = "Subscription",
                onClick = onNavigateToSubscription
            )
            ProfileMenuItem(
                icon = Icons.Default.Share,
                label = "Referral",
                onClick = onNavigateToReferral
            )
            ProfileMenuItem(
                icon = Icons.Default.Download,
                label = "Downloads",
                onClick = onNavigateToDownloads
            )
            ProfileMenuItem(
                icon = Icons.Default.Bookmark,
                label = "Bookmarks",
                onClick = onNavigateToBookmarks
            )
            ProfileMenuItem(
                icon = Icons.Default.Settings,
                label = "Settings",
                onClick = onNavigateToSettings
            )
            ProfileMenuItem(
                icon = Icons.Default.Info,
                label = "About",
                onClick = onNavigateToAbout
            )
            ProfileMenuItem(
                icon = Icons.Default.Logout,
                label = "Logout",
                onClick = onLogout,
                tint = Error
            )
        }

        Spacer(modifier = Modifier.height(DesignToken.Space.dp32))
    }
}

@Composable
private fun ProfileShimmerContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Shimmer header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(SkyBlue.copy(alpha = 0.3f), DeepBlue.copy(alpha = 0.3f))
                    )
                )
                .padding(vertical = DesignToken.Space.dp24),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ShimmerEffect(
                    modifier = Modifier
                        .size(DesignToken.ComponentSize.avatarLarge)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.height(DesignToken.Space.dp12))
                ShimmerEffect(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(24.dp)
                )
                Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
                ShimmerEffect(
                    modifier = Modifier
                        .fillMaxWidth(0.3f)
                        .height(16.dp)
                )
            }
        }

        // Shimmer stats
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = DesignToken.Space.dp16, vertical = DesignToken.Space.dp12),
            horizontalArrangement = Arrangement.spacedBy(DesignToken.Space.dp8)
        ) {
            repeat(3) {
                ShimmerEffect(
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                )
            }
        }

        // Shimmer menu items
        GlassCard(modifier = Modifier.padding(horizontal = DesignToken.Space.dp16)) {
            repeat(8) {
                ShimmerEffect(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                )
                Spacer(modifier = Modifier.height(DesignToken.Space.dp4))
            }
        }

        Spacer(modifier = Modifier.height(DesignToken.Space.dp32))
    }
}
