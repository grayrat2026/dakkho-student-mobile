package com.dakkho.android.presentation.screens.about

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.dakkho.android.domain.model.AboutData
import com.dakkho.android.domain.model.TeamMember
import com.dakkho.android.presentation.components.DakkhoTopBar
import com.dakkho.android.presentation.components.EmptyState
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.components.ShimmerEffect
import com.dakkho.android.presentation.components.profile.StatsCard
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.Neutral500

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBackClick: () -> Unit,
    viewModel: AboutViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            DakkhoTopBar(
                title = "About DAKKHO",
                showSearch = false,
                showNotification = false,
                showAvatar = false,
                onBackClick = onBackClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                AboutShimmerContent(modifier = Modifier.padding(innerPadding))
            }
            uiState.error != null -> {
                EmptyState(
                    title = "Failed to load about data",
                    subtitle = uiState.error ?: "An unexpected error occurred",
                    actionText = "Retry",
                    onAction = { viewModel.loadAbout() },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            uiState.aboutData != null -> {
                AboutContent(
                    aboutData = uiState.aboutData!!,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun AboutContent(
    aboutData: AboutData,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = DesignToken.Space.dp16)
    ) {
        Spacer(modifier = Modifier.height(DesignToken.Space.dp8))

        // Mission section
        aboutData.mission?.let { mission ->
            GlassCard {
                Text(
                    text = "Our Mission",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
                Text(
                    text = mission,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Neutral500
                )
            }
        }

        Spacer(modifier = Modifier.height(DesignToken.Space.dp12))

        // Vision section
        aboutData.vision?.let { vision ->
            GlassCard {
                Text(
                    text = "Our Vision",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
                Text(
                    text = vision,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Neutral500
                )
            }
        }

        Spacer(modifier = Modifier.height(DesignToken.Space.dp12))

        // Stats row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(DesignToken.Space.dp8)
        ) {
            StatsCard(
                label = "Students",
                value = aboutData.totalStudents ?: 0,
                icon = Icons.Default.School,
                modifier = Modifier.weight(1f)
            )
            StatsCard(
                label = "Courses",
                value = aboutData.totalCourses ?: 0,
                icon = Icons.Default.MenuBook,
                modifier = Modifier.weight(1f)
            )
            StatsCard(
                label = "Since",
                value = aboutData.foundedYear ?: 0,
                icon = Icons.Default.CalendarToday,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

        // Team section
        aboutData.team?.let { team ->
            if (team.isNotEmpty()) {
                Text(
                    text = "Our Team",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = DesignToken.Space.dp12)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(DesignToken.Space.dp12),
                    contentPadding = PaddingValues(end = DesignToken.Space.dp16)
                ) {
                    items(items = team, key = { it.name }) { member ->
                        TeamMemberCard(member = member)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(DesignToken.Space.dp32))
    }
}

@Composable
private fun TeamMemberCard(member: TeamMember) {
    GlassCard(modifier = Modifier.width(160.dp)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            AsyncImage(
                model = member.avatarUrl,
                contentDescription = member.name,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(DesignToken.Space.dp8))

            // Name
            Text(
                text = member.name,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            // Role
            member.role?.let { role ->
                Spacer(modifier = Modifier.height(DesignToken.Space.dp2))
                Text(
                    text = role,
                    style = MaterialTheme.typography.bodySmall,
                    color = Neutral500
                )
            }
        }
    }
}

@Composable
private fun AboutShimmerContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = DesignToken.Space.dp16)
    ) {
        Spacer(modifier = Modifier.height(DesignToken.Space.dp8))

        // Mission shimmer
        GlassCard {
            ShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
            )
            Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
            ShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            )
        }

        Spacer(modifier = Modifier.height(DesignToken.Space.dp12))

        // Vision shimmer
        GlassCard {
            ShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
            )
            Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
            ShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            )
        }

        Spacer(modifier = Modifier.height(DesignToken.Space.dp12))

        // Stats shimmer
        Row(
            modifier = Modifier.fillMaxWidth(),
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

        Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

        // Team shimmer
        ShimmerEffect(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .height(20.dp)
        )
        Spacer(modifier = Modifier.height(DesignToken.Space.dp12))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(DesignToken.Space.dp12)
        ) {
            items(4) {
                ShimmerEffect(
                    modifier = Modifier
                        .width(160.dp)
                        .height(140.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(DesignToken.Space.dp32))
    }
}
