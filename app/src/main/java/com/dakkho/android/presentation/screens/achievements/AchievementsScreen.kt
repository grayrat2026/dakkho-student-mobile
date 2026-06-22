package com.dakkho.android.presentation.screens.achievements

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dakkho.android.domain.model.Achievement
import com.dakkho.android.domain.model.AchievementMilestone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(
    onBack: () -> Unit = {},
    viewModel: AchievementsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Achievements", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Stats header
            AchievementStatsHeader(
                totalXp = uiState.totalXp,
                unlockedCount = uiState.unlockedCount,
                totalCount = uiState.totalCount
            )

            // Streak counter
            if (uiState.streak.currentStreak > 0) {
                StreakCounter(streak = uiState.streak.currentStreak)
            }

            // Milestone progress bars
            if (uiState.milestones.isNotEmpty()) {
                MilestoneProgressSection(milestones = uiState.milestones)
            }

            // Category filter chips
            if (uiState.categories.isNotEmpty()) {
                CategoryFilterRow(
                    categories = uiState.categories,
                    selectedCategory = uiState.selectedCategory,
                    onCategorySelected = { viewModel.selectCategory(it) }
                )
            }

            // Badge grid
            if (uiState.isLoading) {
                // Show shimmer
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Loading achievements...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                val filteredAchievements = viewModel.getFilteredAchievements()
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 8.dp)
                ) {
                    items(filteredAchievements, key = { it.id }) { achievement ->
                        BadgeCard(achievement = achievement)
                    }
                }
            }
        }
    }

    // Newly unlocked badge celebration
    uiState.newlyUnlockedBadge?.let { badge ->
        BadgeUnlockOverlay(
            achievement = badge,
            onDismiss = { viewModel.clearNewlyUnlockedBadge() }
        )
    }
}

@Composable
private fun AchievementStatsHeader(
    totalXp: Int,
    unlockedCount: Int,
    totalCount: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Total XP
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${totalXp}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF59E0B)
                )
                Text(
                    text = "Total XP",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Progress circle
            Box(contentAlignment = Alignment.Center) {
                val progress = if (totalCount > 0) unlockedCount.toFloat() / totalCount else 0f
                CircularProgressBar(
                    progress = progress,
                    modifier = Modifier.size(64.dp),
                    strokeWidth = 6.dp
                )
                Text(
                    text = "$unlockedCount/$totalCount",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Unlocked count
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$unlockedCount",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF22C55E)
                )
                Text(
                    text = "Unlocked",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun CircularProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    strokeWidth: androidx.compose.ui.unit.Dp = 6.dp
) {
    val animatedProgress = rememberInfiniteTransition(label = "progress").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ), label = "pulse"
    ).value

    val displayProgress = progress.coerceIn(0f, 1f)

    Canvas(modifier = modifier) {
        val stroke = strokeWidth.toPx()
        val radius = (size.minDimension - stroke) / 2
        val center = Offset(size.width / 2, size.height / 2)

        // Background circle
        drawCircle(
            color = Color(0xFF374151),
            radius = radius,
            center = center,
            style = Stroke(width = stroke)
        )

        // Progress arc
        drawArc(
            color = Color(0xFF22C55E),
            startAngle = -90f,
            sweepAngle = 360f * displayProgress,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )
    }
}

@Composable
private fun StreakCounter(streak: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF7C2D12).copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.LocalFireDepartment,
                contentDescription = null,
                tint = Color(0xFFF97316),
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "$streak",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF97316)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Day Streak",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun MilestoneProgressSection(milestones: List<AchievementMilestone>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Milestones",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        milestones.forEach { milestone ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = milestone.label,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.width(100.dp),
                    color = if (milestone.isCompleted) Color(0xFF22C55E) else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                LinearProgressIndicator(
                    progress = { (milestone.percentage / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (milestone.isCompleted) Color(0xFF22C55E) else Color(0xFF3B82F6),
                    trackColor = Color(0xFF374151)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${milestone.percentage.toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun CategoryFilterRow(
    categories: List<String>,
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedCategory == null,
            onClick = { onCategorySelected(null) },
            label = { Text("All") }
        )
        categories.forEach { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                label = { Text(category.replaceFirstChar { it.uppercase() }) }
            )
        }
    }
}

@Composable
private fun BadgeCard(achievement: Achievement) {
    val isUnlocked = achievement.isUnlocked

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (isUnlocked) Modifier.clickable { } else Modifier),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isUnlocked) 4.dp else 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Badge icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        if (isUnlocked) {
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF22C55E), Color(0xFF3B82F6))
                            )
                        } else {
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF374151), Color(0xFF4B5563))
                            )
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isUnlocked) {
                    Text(
                        text = getAchievementEmoji(achievement.icon),
                        fontSize = 28.sp
                    )
                } else {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = Color(0xFF6B7280),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Badge name
            Text(
                text = achievement.title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = if (isUnlocked) MaterialTheme.colorScheme.onSurface else Color(0xFF6B7280)
            )

            // XP
            if (isUnlocked) {
                Text(
                    text = "+${achievement.xp} XP",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFF59E0B),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun BadgeUnlockOverlay(
    achievement: Achievement,
    onDismiss: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "celebration")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "badgePulse"
    )

    AnimatedVisibility(visible = true) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable(onClick = onDismiss)
            ) {
                Text("Achievement Unlocked!", style = MaterialTheme.typography.titleLarge, color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .size((120 * scale).dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color(0xFF22C55E), Color(0xFF3B82F6), Color(0xFF8B5CF6))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(getAchievementEmoji(achievement.icon), fontSize = 60.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(achievement.title, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                achievement.description?.let {
                    Text(it, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF9CA3AF), textAlign = TextAlign.Center)
                }
                Text("+${achievement.xp} XP", style = MaterialTheme.typography.titleMedium, color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Tap to continue", style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B7280))
            }
        }
    }
}

private fun getAchievementEmoji(icon: String): String = when (icon.lowercase()) {
    "trophy" -> "🏆"
    "star" -> "⭐"
    "fire" -> "🔥"
    "book" -> "📖"
    "rocket" -> "🚀"
    "medal" -> "🏅"
    "crown" -> "👑"
    "diamond" -> "💎"
    "lightning" -> "⚡"
    "brain" -> "🧠"
    "target" -> "🎯"
    "graduation_cap" -> "🎓"
    else -> "🏆"
}
