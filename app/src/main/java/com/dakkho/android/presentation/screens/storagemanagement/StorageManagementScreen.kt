package com.dakkho.android.presentation.screens.storagemanagement

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dakkho.android.domain.model.StorageBreakdown
import com.dakkho.android.domain.model.StorageCategory
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.Error
import com.dakkho.android.presentation.theme.Green
import com.dakkho.android.presentation.theme.Neutral500
import com.dakkho.android.presentation.theme.SkyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorageManagementScreen(
    onBackClick: () -> Unit,
    viewModel: StorageManagementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    // Confirm dialogs
    if (uiState.showClearCacheConfirm) {
        AlertDialog(
            onDismissRequest = viewModel::dismissClearCacheConfirm,
            title = { Text("ক্যাশে মুছুন") },
            text = { Text("আপনি কি নিশ্চিত? ক্যাশে ডেটা মুছে যাবে কিন্তু আপনার ডাউনলোড ও অগ্রগতি নিরাপদ থাকবে।") },
            confirmButton = {
                TextButton(onClick = viewModel::clearCache) {
                    Text("মুছুন", color = Error)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissClearCacheConfirm) {
                    Text("বাতিল")
                }
            }
        )
    }

    if (uiState.showClearDownloadsConfirm) {
        AlertDialog(
            onDismissRequest = viewModel::dismissClearDownloadsConfirm,
            title = { Text("ডাউনলোড মুছুন") },
            text = { Text("আপনি কি নিশ্চিত? অফলাইনে দেখার জন্য ডাউনলোড করা সমস্ত ভিডিও মুছে যাবে। আপনাকে পুনরায় ডাউনলোড করতে হবে।") },
            confirmButton = {
                TextButton(onClick = viewModel::clearDownloads) {
                    Text("মুছুন", color = Error)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissClearDownloadsConfirm) {
                    Text("বাতিল")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "স্টোরেজ ব্যবস্থাপনা",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = DesignToken.Space.dp16)
        ) {
            val info = uiState.storageInfo

            // ── Storage Ring Chart ──
            StorageRingSection(
                usedMB = info.usedStorageMB,
                totalGB = info.totalStorageGB,
                usagePercent = info.usagePercent,
                breakdown = uiState.breakdown
            )

            Spacer(modifier = Modifier.height(DesignToken.Space.dp24))

            // ── Storage Breakdown ──
            Text(
                text = "স্টোরেজ বিবরণ",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = SkyBlue
            )

            Spacer(modifier = Modifier.height(DesignToken.Space.dp12))

            StorageBreakdownRow(
                icon = Icons.Default.Cached,
                label = StorageCategory.CACHE.label,
                sizeMB = info.cacheSizeMB,
                color = SkyBlue
            )
            StorageBreakdownRow(
                icon = Icons.Default.CloudDownload,
                label = StorageCategory.DOWNLOADS.label,
                sizeMB = info.downloadSizeMB,
                color = Green
            )
            StorageBreakdownRow(
                icon = Icons.Default.Image,
                label = StorageCategory.IMAGES.label,
                sizeMB = info.imageDataSizeMB,
                color = Color(0xFFF59E0B)
            )
            StorageBreakdownRow(
                icon = Icons.Default.DataUsage,
                label = StorageCategory.DATABASE.label,
                sizeMB = info.databaseSizeMB,
                color = Color(0xFF8B5CF6)
            )
            StorageBreakdownRow(
                icon = Icons.Default.Storage,
                label = StorageCategory.OTHER.label,
                sizeMB = info.otherDataSizeMB,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(DesignToken.Space.dp24))

            // ── Actions ──
            Text(
                text = "পদক্ষেপ",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = SkyBlue
            )

            Spacer(modifier = Modifier.height(DesignToken.Space.dp12))

            // Clear Cache
            if (uiState.isClearingCache) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ক্যাশে মুছছে...", color = Neutral500)
                }
            } else {
                OutlinedButton(
                    onClick = viewModel::showClearCacheConfirm,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SkyBlue)
                ) {
                    Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ক্যাশে মুছুন (${String.format("%.1f", info.cacheSizeMB)} MB)")
                }
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp12))

            // Clear Downloads
            if (uiState.isClearingDownloads) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ডাউনলোড মুছছে...", color = Neutral500)
                }
            } else {
                OutlinedButton(
                    onClick = viewModel::showClearDownloadsConfirm,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Error)
                ) {
                    Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ডাউনলোড মুছুন (${String.format("%.1f", info.downloadSizeMB)} MB)")
                }
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp32))
        }
    }
}

@Composable
private fun StorageRingSection(
    usedMB: Float,
    totalGB: Float,
    usagePercent: Float,
    breakdown: List<StorageBreakdown>
) {
    com.dakkho.android.presentation.components.GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Ring chart
            Box(
                modifier = Modifier.size(160.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(160.dp)) {
                    val strokeWidth = 18.dp.toPx()
                    val radius = (size.minDimension - strokeWidth) / 2
                    val center = Offset(size.width / 2, size.height / 2)

                    // Background circle
                    drawCircle(
                        color = Color(0xFFE2E8F0),
                        radius = radius,
                        center = center,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // Usage arc
                    if (usagePercent > 0) {
                        val sweepAngle = (usagePercent / 100f) * 360f
                        drawArc(
                            color = SkyBlue,
                            startAngle = -90f,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            topLeft = Offset(
                                center.x - radius,
                                center.y - radius
                            ),
                            size = Size(radius * 2, radius * 2),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = String.format("%.0f", usagePercent) + "%",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "ব্যবহৃত",
                        style = MaterialTheme.typography.bodySmall,
                        color = Neutral500
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StorageStat(label = "ব্যবহৃত", value = formatMB(usedMB))
                StorageStat(label = "মোট", value = String.format("%.1f GB", totalGB))
            }
        }
    }
}

@Composable
private fun StorageStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Neutral500
        )
    }
}

@Composable
private fun StorageBreakdownRow(
    icon: ImageVector,
    label: String,
    sizeMB: Float,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = DesignToken.Space.dp8),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = formatMB(sizeMB),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = color
        )
    }
    HorizontalDivider(
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
        thickness = 0.5.dp
    )
}

private fun formatMB(mb: Float): String {
    return when {
        mb < 1f -> String.format("%.0f KB", mb * 1024)
        mb > 1024f -> String.format("%.1f GB", mb / 1024)
        else -> String.format("%.1f MB", mb)
    }
}
