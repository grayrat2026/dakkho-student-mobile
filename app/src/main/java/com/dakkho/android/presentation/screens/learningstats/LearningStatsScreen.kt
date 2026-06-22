package com.dakkho.android.presentation.screens.learningstats

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timeline
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dakkho.android.presentation.components.EmptyState
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.components.ShimmerEffect
import com.dakkho.android.presentation.components.profile.StatsCard
import com.dakkho.android.presentation.theme.DeepBlue
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.SkyBlue
import com.dakkho.android.presentation.theme.Success

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningStatsScreen(
    onBackClick: () -> Unit,
    viewModel: LearningStatsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "শিক্ষার পরিসংখ্যান",
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
                LearningStatsShimmer(modifier = Modifier.padding(innerPadding))
            }
            uiState.error != null -> {
                EmptyState(
                    title = "পরিসংখ্যান লোড ব্যর্থ",
                    subtitle = uiState.error ?: "অপ্রত্যাশিত ত্রুটি",
                    actionText = "আবার চেষ্টা করুন",
                    onAction = { viewModel.loadStats() },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            else -> {
                LearningStatsContent(
                    stats = uiState.stats,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun LearningStatsContent(
    stats: com.dakkho.android.domain.model.LearningStats,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = DesignToken.Space.dp16)
    ) {
        // Hero stats header with gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(DesignToken.Space.dp16))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(SkyBlue, DeepBlue)
                    )
                )
                .padding(DesignToken.Space.dp20)
        ) {
            Column {
                Text(
                    text = "আপনার শিক্ষার সারসংক্ষেপ",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "মোট ${stats.totalHoursWatched.toInt()} ঘণ্টা শিক্ষা গ্রহণ করেছেন",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

        // Stats grid - Row 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(DesignToken.Space.dp8)
        ) {
            StatsCard(
                label = "কোর্স",
                value = stats.coursesEnrolled,
                icon = Icons.Default.MenuBook,
                modifier = Modifier.weight(1f)
            )
            StatsCard(
                label = "সম্পন্ন",
                value = stats.coursesCompleted,
                icon = Icons.Default.Book,
                modifier = Modifier.weight(1f)
            )
            StatsCard(
                label = "ঘণ্টা",
                value = stats.totalHoursWatched.toInt(),
                icon = Icons.Default.PlayCircle,
                modifier = Modifier.weight(1f)
            )
        }

        // Stats grid - Row 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(DesignToken.Space.dp8)
        ) {
            StatsCard(
                label = "স্ট্রিক",
                value = stats.currentStreak,
                icon = Icons.Default.LocalFireDepartment,
                modifier = Modifier.weight(1f)
            )
            StatsCard(
                label = "XP",
                value = stats.totalXp,
                icon = Icons.Default.Star,
                modifier = Modifier.weight(1f)
            )
            StatsCard(
                label = "সনদ",
                value = stats.certificatesEarned,
                icon = Icons.Default.CardMembership,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

        // Weekly Activity Chart
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(DesignToken.Space.dp16)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Timeline,
                        contentDescription = null,
                        tint = SkyBlue,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "সাপ্তাহিক কার্যকলাপ",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

                // Simple bar chart
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    val maxHours = stats.weeklyActivity.maxOfOrNull { it.hoursWatched }?.coerceAtLeast(1f) ?: 1f
                    stats.weeklyActivity.forEach { day ->
                        val barHeight = (day.hoursWatched / maxHours).coerceIn(0f, 1f)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Bar
                            Box(
                                modifier = Modifier
                                    .width(28.dp)
                                    .height((barHeight * 120 + 8).dp)
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .background(
                                        if (day.hoursWatched > 0f)
                                            Brush.verticalGradient(
                                                colors = listOf(SkyBlue, DeepBlue)
                                            )
                                        else
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                                                )
                                            )
                                    )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = day.day,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

        // Streak Info Card
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(DesignToken.Space.dp16)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = Success,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "শিক্ষা স্ট্রিক",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(DesignToken.Space.dp12))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "${stats.currentStreak} দিন",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = Success
                        )
                        Text(
                            text = "বর্তমান স্ট্রিক",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${stats.longestStreak} দিন",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = SkyBlue
                        )
                        Text(
                            text = "সর্বোচ্চ স্ট্রিক",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

        // Lesson completion stats
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(DesignToken.Space.dp16)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = null,
                        tint = SkyBlue,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "পাঠ সম্পন্ন",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(DesignToken.Space.dp12))

                Text(
                    text = "${stats.totalLessonsCompleted}",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = SkyBlue
                )
                Text(
                    text = "মোট সম্পন্ন পাঠ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                if (stats.achievementsUnlocked > 0) {
                    Spacer(modifier = Modifier.height(DesignToken.Space.dp12))
                    Text(
                        text = "${stats.achievementsUnlocked} অর্জন আনলক করা হয়েছে",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Success
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(DesignToken.Space.dp32))
    }
}

@Composable
private fun LearningStatsShimmer(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = DesignToken.Space.dp16)
    ) {
        ShimmerEffect(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        )
        Spacer(modifier = Modifier.height(DesignToken.Space.dp16))
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
        ShimmerEffect(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
    }
}
