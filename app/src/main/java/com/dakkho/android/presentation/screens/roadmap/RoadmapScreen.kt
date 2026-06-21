package com.dakkho.android.presentation.screens.roadmap

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dakkho.android.domain.model.FeatureStatus
import com.dakkho.android.domain.model.RoadmapFeature
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.SkyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoadmapScreen(
    onBackClick: () -> Unit,
    viewModel: RoadmapViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val selectedStatus by viewModel.selectedStatus

    val statuses = remember { listOf(null, FeatureStatus.PLANNED, FeatureStatus.IN_PROGRESS, FeatureStatus.COMPLETED) }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(text = "রোডম্যাপ", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = DesignToken.Space.dp16)) {
            // Status filters
            Row(horizontalArrangement = Arrangement.spacedBy(DesignToken.Space.dp8)) {
                statuses.forEach { status ->
                    FilterChip(
                        selected = selectedStatus == status,
                        onClick = { viewModel.setStatusFilter(status) },
                        label = { Text(text = status?.label ?: "সব") }
                    )
                }
            }
            Spacer(modifier = Modifier.height(DesignToken.Space.dp12))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(DesignToken.Space.dp12)) {
                items(viewModel.filteredFeatures) { feature ->
                    RoadmapFeatureCard(
                        feature = feature,
                        onUpvote = { viewModel.upvoteFeature(feature.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RoadmapFeatureCard(
    feature: RoadmapFeature,
    onUpvote: () -> Unit
) {
    val statusColor = when (feature.status) {
        FeatureStatus.PLANNED -> Color(0xFF94A3B8)
        FeatureStatus.IN_PROGRESS -> Color(0xFFF59E0B)
        FeatureStatus.COMPLETED -> Color(0xFF22C55E)
    }
    val statusIcon = when (feature.status) {
        FeatureStatus.PLANNED -> Icons.Default.Schedule
        FeatureStatus.IN_PROGRESS -> Icons.Default.Construction
        FeatureStatus.COMPLETED -> Icons.Default.CheckCircle
    }

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth().padding(DesignToken.Space.dp16)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = statusIcon, contentDescription = null, tint = statusColor, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(DesignToken.Space.dp8))
                Text(text = feature.status.label, style = MaterialTheme.typography.labelMedium, color = statusColor)
                Spacer(modifier = Modifier.weight(1f))
                // Upvote button
                IconButton(onClick = onUpvote) {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = null,
                        tint = if (feature.isUpvoted) SkyBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(text = "${feature.upvotes}", style = MaterialTheme.typography.labelMedium, color = if (feature.isUpvoted) SkyBlue else MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = feature.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Text(text = feature.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            // Progress bar for in-progress features
            if (feature.status == FeatureStatus.IN_PROGRESS) {
                Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
                LinearProgressIndicator(
                    progress = { 0.6f },
                    modifier = Modifier.fillMaxWidth().height(4.dp),
                    color = statusColor,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeCap = StrokeCap.Round
                )
            }
        }
    }
}
