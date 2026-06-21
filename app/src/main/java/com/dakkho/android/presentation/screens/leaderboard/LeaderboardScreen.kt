package com.dakkho.android.presentation.screens.leaderboard

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.dakkho.android.domain.model.LeaderboardEntry
import com.dakkho.android.domain.model.LeaderboardPeriod
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.SkyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    onBackClick: () -> Unit,
    viewModel: LeaderboardViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val entries by viewModel.entries
    val currentPeriod by viewModel.currentPeriod
    val myRank by viewModel.myRank

    val periods = remember {
        listOf(LeaderboardPeriod.WEEKLY, LeaderboardPeriod.MONTHLY, LeaderboardPeriod.ALL_TIME)
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "লিডারবোর্ড",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = DesignToken.Space.dp16)
        ) {
            // Period Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(DesignToken.Space.dp8)
            ) {
                periods.forEach { period ->
                    FilterChip(
                        selected = currentPeriod == period,
                        onClick = { viewModel.onPeriodChanged(period) },
                        label = { Text(text = period.label) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // Top 3 Podium
            if (entries.size >= 3) {
                PodiumSection(first = entries[0], second = entries[1], third = entries[2])
                Spacer(modifier = Modifier.height(DesignToken.Space.dp16))
            }

            // Remaining list
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(DesignToken.Space.dp8)
            ) {
                itemsIndexed(entries.drop(3)) { index, entry ->
                    LeaderboardRankCard(entry = entry, rank = index + 4)
                }
            }

            // My Rank Footer
            myRank?.let { rank ->
                Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
                MyRankCard(entry = rank)
            }
        }
    }
}

@Composable
private fun PodiumSection(first: LeaderboardEntry, second: LeaderboardEntry, third: LeaderboardEntry) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        PodiumCard(entry = second, rank = 2, height = 100.dp)
        Spacer(modifier = Modifier.width(DesignToken.Space.dp8))
        PodiumCard(entry = first, rank = 1, height = 130.dp)
        Spacer(modifier = Modifier.width(DesignToken.Space.dp8))
        PodiumCard(entry = third, rank = 3, height = 80.dp)
    }
}

@Composable
private fun PodiumCard(entry: LeaderboardEntry, rank: Int, height: androidx.compose.ui.unit.Dp) {
    val medalColor = when (rank) {
        1 -> Color(0xFFFFD700)
        2 -> Color(0xFFC0C0C0)
        else -> Color(0xFFCD7F32)
    }
    GlassCard(modifier = Modifier.width(100.dp)) {
        Column(
            modifier = Modifier.fillMaxWidth().height(height).padding(DesignToken.Space.dp8),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(medalColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                if (entry.avatarUrl != null) {
                    AsyncImage(model = entry.avatarUrl, contentDescription = null, modifier = Modifier.size(36.dp).clip(CircleShape))
                } else {
                    Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = null, tint = medalColor, modifier = Modifier.size(24.dp))
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = entry.name, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), maxLines = 1, textAlign = TextAlign.Center)
            Text(text = "${entry.xpPoints} XP", style = MaterialTheme.typography.labelSmall, color = SkyBlue)
        }
    }
}

@Composable
private fun LeaderboardRankCard(entry: LeaderboardEntry, rank: Int) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = DesignToken.Space.dp16, vertical = DesignToken.Space.dp12),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "#$rank", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = if (entry.isCurrentUser) SkyBlue else MaterialTheme.colorScheme.onSurface, modifier = Modifier.width(40.dp))
            Spacer(modifier = Modifier.width(DesignToken.Space.dp12))
            Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer)) {
                if (entry.avatarUrl != null) {
                    AsyncImage(model = entry.avatarUrl, contentDescription = null, modifier = Modifier.fillMaxSize().clip(CircleShape))
                }
            }
            Spacer(modifier = Modifier.width(DesignToken.Space.dp12))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = entry.name, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium))
                entry.technology?.let { Text(text = it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
            val change = entry.weeklyChange
            if (change != 0) {
                Icon(imageVector = if (change > 0) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = null, tint = if (change > 0) Color(0xFF22C55E) else MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                Text(text = "${kotlin.math.abs(change)}", style = MaterialTheme.typography.labelSmall, color = if (change > 0) Color(0xFF22C55E) else MaterialTheme.colorScheme.error)
            }
            Spacer(modifier = Modifier.width(DesignToken.Space.dp8))
            Text(text = "${entry.xpPoints} XP", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = SkyBlue)
        }
    }
}

@Composable
private fun MyRankCard(entry: LeaderboardEntry) {
    GlassCard(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(DesignToken.Radius.dp16)).background(SkyBlue.copy(alpha = 0.1f))) {
        Row(modifier = Modifier.fillMaxWidth().padding(DesignToken.Space.dp16), verticalAlignment = Alignment.CenterVertically) {
            Text(text = "আপনার র‍্যাংক", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium), modifier = Modifier.weight(1f))
            Text(text = "#${entry.rank}", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = SkyBlue)
            Spacer(modifier = Modifier.width(DesignToken.Space.dp8))
            Text(text = "${entry.xpPoints} XP", style = MaterialTheme.typography.titleMedium, color = SkyBlue)
        }
    }
}
