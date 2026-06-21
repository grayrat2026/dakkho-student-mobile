package com.dakkho.android.presentation.screens.downloadsettings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dakkho.android.domain.model.DownloadQuality
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.SkyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadSettingsScreen(
    onBackClick: () -> Unit,
    viewModel: DownloadSettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    // SAF directory picker
    val directoryPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        uri?.let {
            val label = it.lastPathSegment ?: it.path ?: "কাস্টম লোকেশন"
            viewModel.setStoragePath(it.toString(), label)
        }
    }

    // Clear cache dialog
    if (uiState.showClearCacheDialog) {
        AlertDialog(
            onDismissRequest = viewModel::dismissClearCacheDialog,
            title = { Text("ক্যাশে মুছুন") },
            text = { Text("সমস্ত ক্যাশে ডেটা মুছে যাবে। ডাউনলোড করা ভিডিও প্রভাবিত হবে না।") },
            confirmButton = {
                TextButton(onClick = viewModel::clearCache) {
                    Text("মুছুন", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissClearCacheDialog) {
                    Text("বাতিল")
                }
            }
        )
    }

    // Clear downloads dialog
    if (uiState.showClearDownloadsDialog) {
        AlertDialog(
            onDismissRequest = viewModel::dismissClearDownloadsDialog,
            title = { Text("ডাউনলোড মুছুন") },
            text = { Text("সমস্ত অফলাইন ডাউনলোড করা ভিডিও মুছে যাবে। এই কাজটি পূর্বাবস্থায় ফেরানো যাবে না।") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.dismissClearDownloadsDialog()
                }) {
                    Text("মুছুন", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissClearDownloadsDialog) {
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
                        text = "ডাউনলোড সেটিংস",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = DesignToken.Space.dp16)
        ) {
            val settings = uiState.settings

            // ── Download Quality ──
            Text(
                text = "ডাউনলোড মান",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = SkyBlue,
                modifier = Modifier.padding(vertical = DesignToken.Space.dp8)
            )

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(vertical = DesignToken.Space.dp8)) {
                    DownloadQuality.entries.forEach { quality ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = DesignToken.Space.dp16, vertical = DesignToken.Space.dp4),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = settings.downloadQuality == quality,
                                onClick = { viewModel.setDownloadQuality(quality) },
                                colors = RadioButtonDefaults.colors(selectedColor = SkyBlue)
                            )
                            Spacer(modifier = Modifier.width(DesignToken.Space.dp12))
                            Icon(
                                imageVector = Icons.Default.HighQuality,
                                contentDescription = null,
                                modifier = Modifier.size(DesignToken.IconSize.medium),
                                tint = if (settings.downloadQuality == quality) SkyBlue
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(DesignToken.Space.dp8))
                            Text(
                                text = quality.label,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = if (settings.downloadQuality == quality) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (settings.downloadQuality == quality) SkyBlue
                                    else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // ── Auto Download ──
            Text(
                text = "স্বয়ংক্রিয় ডাউনলোড",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = SkyBlue,
                modifier = Modifier.padding(vertical = DesignToken.Space.dp8)
            )

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = DesignToken.Space.dp16, vertical = DesignToken.Space.dp12),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        modifier = Modifier.size(DesignToken.IconSize.medium),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(DesignToken.Space.dp16))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Wi-Fi এ স্বয়ংক্রিয় ডাউনলোড",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                        )
                        Text(
                            text = "এনরোল করা কোর্স স্বয়ংক্রিয়ভাবে ডাউনলোড",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = settings.isAutoDownloadOnWifi,
                        onCheckedChange = viewModel::setAutoDownloadOnWifi,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = SkyBlue,
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // ── Storage Location ──
            Text(
                text = "স্টোরেজ লোকেশন",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = SkyBlue,
                modifier = Modifier.padding(vertical = DesignToken.Space.dp8)
            )

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = DesignToken.Space.dp16, vertical = DesignToken.Space.dp12),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = null,
                        modifier = Modifier.size(DesignToken.IconSize.medium),
                        tint = SkyBlue
                    )
                    Spacer(modifier = Modifier.width(DesignToken.Space.dp16))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ডাউনলোড লোকেশন",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                        )
                        Text(
                            text = settings.storagePathLabel ?: "ডিফল্ট (অভ্যন্তরীণ স্টোরেজ)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    OutlinedButton(
                        onClick = { directoryPicker.launch(null) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SkyBlue)
                    ) {
                        Text("পরিবর্তন")
                    }
                }
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // ── Storage Stats ──
            Text(
                text = "স্টোরেজ তথ্য",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = SkyBlue,
                modifier = Modifier.padding(vertical = DesignToken.Space.dp8)
            )

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(DesignToken.Space.dp16)) {
                    // Available storage
                    StorageStatRow(
                        label = "ফাঁকা স্টোরেজ",
                        value = "%.1f GB".format(settings.availableStorageGB)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Downloads
                    StorageStatRow(
                        label = "ডাউনলোড",
                        value = "%.2f GB".format(settings.usedByDownloadsGB)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Cache
                    StorageStatRow(
                        label = "ক্যাশে",
                        value = "%.2f GB".format(settings.usedByCacheGB)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Usage bar
                    val totalUsed = settings.usedByDownloadsGB + settings.usedByCacheGB + settings.usedByOtherGB
                    val maxStorage = settings.availableStorageGB + totalUsed
                    if (maxStorage > 0) {
                        LinearProgressIndicator(
                            progress = { (totalUsed / maxStorage).coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxWidth(),
                            color = SkyBlue,
                            trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )
                    }

                    Spacer(modifier = Modifier.height(DesignToken.Space.dp12))

                    // Clear buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(DesignToken.Space.dp8)
                    ) {
                        OutlinedButton(
                            onClick = viewModel::showClearCacheDialog,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ক্যাশে মুছুন")
                        }
                        OutlinedButton(
                            onClick = viewModel::showClearDownloadsDialog,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ডাউনলোড মুছুন")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp32))
        }
    }
}

@Composable
private fun StorageStatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
