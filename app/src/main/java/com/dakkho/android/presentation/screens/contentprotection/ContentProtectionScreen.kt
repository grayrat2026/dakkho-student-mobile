package com.dakkho.android.presentation.screens.contentprotection

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
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
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
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.SkyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentProtectionScreen(
    onBackClick: () -> Unit,
    viewModel: ContentProtectionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    // Device admin warning dialog
    if (uiState.showDeviceAdminWarning) {
        AlertDialog(
            onDismissRequest = viewModel::dismissDeviceAdminWarning,
            title = { Text("FLAG_SECURE সতর্কতা") },
            text = {
                Text(
                    "FLAG_SECURE সক্রিয় করলে কিছু ডিভাইস অ্যাডমিন অ্যাপের সাথে সংঘাত হতে পারে। " +
                        "যদি আপনার ডিভাইসে অ্যাডমিন অ্যাপ থাকে, তাহলে কিছু ফিচার সঠিকভাবে কাজ নাও করতে পারে।"
                )
            },
            confirmButton = {
                TextButton(onClick = viewModel::confirmFlagSecure) {
                    Text("সক্রিয় করুন", color = SkyBlue)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissDeviceAdminWarning) {
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
                        text = "কন্টেন্ট সুরক্ষা",
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
            val config = uiState.config

            // ── Screenshot Block ──
            Text(
                text = "স্ক্রিনশট সুরক্ষা",
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
                        imageVector = Icons.Default.VisibilityOff,
                        contentDescription = null,
                        modifier = Modifier.size(DesignToken.IconSize.medium),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(DesignToken.Space.dp16))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "স্ক্রিনশট ব্লক",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                        )
                        Text(
                            text = "স্ক্রিনশট ও স্ক্রিন রেকর্ড প্রতিরোধ",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = config.isScreenshotBlockEnabled,
                        onCheckedChange = viewModel::setScreenshotBlock,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = SkyBlue,
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // ── Download Restriction ──
            Text(
                text = "ডাউনলোড সুরক্ষা",
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
                            text = "ডাউনলোড সীমাবদ্ধতা",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                        )
                        Text(
                            text = "ভিডিও ডাউনলোডে সীমাবদ্ধতা আরোপ",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = config.isDownloadRestrictionEnabled,
                        onCheckedChange = viewModel::setDownloadRestriction,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = SkyBlue,
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // ── Secure Player Mode ──
            Text(
                text = "সুরক্ষিত প্লেয়ার",
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
                        imageVector = Icons.Default.PlayCircle,
                        contentDescription = null,
                        modifier = Modifier.size(DesignToken.IconSize.medium),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(DesignToken.Space.dp16))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "সুরক্ষিত প্লেয়ার মোড",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                        )
                        Text(
                            text = "DRM ও সুরক্ষিত প্লেব্যাক ব্যবস্থা",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = config.isSecurePlayerModeEnabled,
                        onCheckedChange = viewModel::setSecurePlayerMode,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = SkyBlue,
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // ── FLAG_SECURE Toggle ──
            Text(
                text = "উন্নত সুরক্ষা",
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
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        modifier = Modifier.size(DesignToken.IconSize.medium),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(DesignToken.Space.dp16))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "FLAG_SECURE",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                        )
                        Text(
                            text = "উইন্ডো কন্টেন্ট সুরক্ষিত করুন",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (config.hasDeviceAdminWarning) {
                            Text(
                                text = "ডিভাইস অ্যাডমিন অ্যাপের সাথে সংঘাত হতে পারে",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    Switch(
                        checked = config.isFlagSecureEnabled,
                        onCheckedChange = viewModel::setFlagSecure,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = SkyBlue,
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // ── Status Card ──
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(DesignToken.Space.dp16)) {
                    Text(
                        text = "সুরক্ষা অবস্থা",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ProtectionStatusRow(
                        label = "স্ক্রিনশট ব্লক",
                        isEnabled = config.isScreenshotBlockEnabled
                    )
                    ProtectionStatusRow(
                        label = "ডাউনলোড সীমাবদ্ধতা",
                        isEnabled = config.isDownloadRestrictionEnabled
                    )
                    ProtectionStatusRow(
                        label = "সুরক্ষিত প্লেয়ার",
                        isEnabled = config.isSecurePlayerModeEnabled
                    )
                    ProtectionStatusRow(
                        label = "FLAG_SECURE",
                        isEnabled = config.isFlagSecureEnabled
                    )
                }
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp32))
        }
    }
}

@Composable
private fun ProtectionStatusRow(label: String, isEnabled: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isEnabled) Icons.Default.Block else Icons.Default.Block,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = if (isEnabled) SkyBlue else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = if (isEnabled) "সক্রিয়" else "নিষ্ক্রিয়",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = if (isEnabled) SkyBlue else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
