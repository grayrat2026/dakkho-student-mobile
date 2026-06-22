package com.dakkho.android.presentation.screens.changelog

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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dakkho.android.domain.model.ChangelogEntry
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.SkyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangelogScreen(
    onBackClick: () -> Unit,
    viewModel: ChangelogViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val entries by viewModel.entries.collectAsState()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(text = "পরিবর্তন সূচি", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = DesignToken.Space.dp16),
            verticalArrangement = Arrangement.spacedBy(DesignToken.Space.dp12)
        ) {
            items(entries) { entry ->
                ChangelogEntryCard(entry = entry)
            }
        }
    }
}

@Composable
private fun ChangelogEntryCard(entry: ChangelogEntry) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth().padding(DesignToken.Space.dp16)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (entry.isCurrentVersion) Icons.Default.Star else Icons.Default.NewReleases,
                    contentDescription = null,
                    tint = if (entry.isCurrentVersion) SkyBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(DesignToken.Space.dp8))
                Text(text = "v${entry.version}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                if (entry.isCurrentVersion) {
                    Spacer(modifier = Modifier.width(DesignToken.Space.dp8))
                    Text(text = "বর্তমান", style = MaterialTheme.typography.labelSmall, color = SkyBlue)
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(text = entry.releaseDate, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // Features
            if (entry.features.isNotEmpty()) {
                Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
                Text(text = "নতুন ফিচার", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = SkyBlue)
                entry.features.forEach { feature ->
                    Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.NewReleases, contentDescription = null, modifier = Modifier.size(14.dp), tint = SkyBlue)
                        Spacer(modifier = Modifier.width(DesignToken.Space.dp8))
                        Text(text = feature, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            // Bug Fixes
            if (entry.bugFixes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
                Text(text = "বাগ ফিক্স", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.error)
                entry.bugFixes.forEach { fix ->
                    Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Build, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(DesignToken.Space.dp8))
                        Text(text = fix, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            // Improvements
            if (entry.improvements.isNotEmpty()) {
                Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
                Text(text = "উন্নতি", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.tertiary)
                entry.improvements.forEach { imp ->
                    Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.tertiary)
                        Spacer(modifier = Modifier.width(DesignToken.Space.dp8))
                        Text(text = imp, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
