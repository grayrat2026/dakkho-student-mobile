package com.dakkho.android.presentation.screens.datasaver

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
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.DataSaverOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MobileScreenShare
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.VideoSettings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dakkho.android.domain.model.ImageDataQuality
import com.dakkho.android.domain.model.MobileVideoQuality
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.Green
import com.dakkho.android.presentation.theme.Neutral500
import com.dakkho.android.presentation.theme.SkyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataSaverScreen(
    onBackClick: () -> Unit,
    viewModel: DataSaverViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val config = uiState.config

    // Mobile quality dialog
    if (uiState.showMobileQualityDialog) {
        SingleChoiceDialog(
            title = "মোবাইলে ভিডিও মান",
            options = MobileVideoQuality.entries.map { it.label },
            selectedIndex = MobileVideoQuality.entries.indexOf(config.mobileVideoQuality),
            onSelect = { viewModel.setMobileVideoQuality(MobileVideoQuality.entries[it]) },
            onDismiss = viewModel::dismissMobileQualityDialog
        )
    }

    // Image quality dialog
    if (uiState.showImageQualityDialog) {
        SingleChoiceDialog(
            title = "ছবির মান",
            options = ImageDataQuality.entries.map { it.label },
            selectedIndex = ImageDataQuality.entries.indexOf(config.imageDataQuality),
            onSelect = { viewModel.setImageDataQuality(ImageDataQuality.entries[it]) },
            onDismiss = viewModel::dismissImageQualityDialog
        )
    }

    // Data limit dialog
    if (uiState.showDataLimitDialog) {
        DataLimitDialog(
            currentLimit = config.monthlyDataLimitMB,
            onSelect = viewModel::setMonthlyDataLimit,
            onDismiss = viewModel::dismissDataLimitDialog
        )
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "ডেটা সেভার",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
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
            // ── Data Saver Master Toggle ──
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.DataSaverOn,
                        contentDescription = null,
                        tint = if (config.isDataSaverEnabled) Green else Neutral500,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "ডেটা সেভার মোড",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            "মোবাইল ডেটায় ব্যবহার কমান",
                            style = MaterialTheme.typography.bodySmall,
                            color = Neutral500
                        )
                    }
                    Switch(
                        checked = config.isDataSaverEnabled,
                        onCheckedChange = viewModel::setDataSaverEnabled,
                        colors = SwitchDefaults.colors(checkedTrackColor = Green)
                    )
                }
            }

            if (config.isDataSaverEnabled) {
                Spacer(modifier = Modifier.height(8.dp))

                // Data usage card
                if (config.monthlyDataLimitMB > 0) {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "এই মাসের ডেটা ব্যবহার",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = {
                                    if (config.monthlyDataLimitMB > 0) {
                                        (config.dataUsedThisMonthMB / config.monthlyDataLimitMB).coerceIn(0f, 1f)
                                    } else 0f
                                },
                                modifier = Modifier.fillMaxWidth(),
                                color = if (config.dataUsedThisMonthMB > config.monthlyDataLimitMB * 0.8f)
                                    MaterialTheme.colorScheme.error else SkyBlue
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    "${String.format("%.0f", config.dataUsedThisMonthMB)} MB ব্যবহৃত",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Neutral500
                                )
                                Text(
                                    "${config.monthlyDataLimitMB} MB সীমা",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Neutral500,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // ── Video & Playback ──
            SectionHeader("ভিডিও ও প্লেব্যাক")

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                DataSaverClickItem(
                    icon = Icons.Default.VideoSettings,
                    title = "মোবাইলে ভিডিও মান",
                    subtitle = config.mobileVideoQuality.label,
                    onClick = viewModel::showMobileQualityDialog
                )
                SettingsDivider()
                DataSaverToggle(
                    icon = Icons.Default.PlayCircle,
                    title = "মোবাইলে স্বয়ংক্রিয় প্লে",
                    subtitle = "মোবাইল ডেটায় ভিডিও অটো-প্লে",
                    checked = config.isAutoplayOnMobileEnabled,
                    onCheckedChange = viewModel::setAutoplayOnMobile
                )
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // ── Images ──
            SectionHeader("ছবি ও মিডিয়া")

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                DataSaverToggle(
                    icon = Icons.Default.Image,
                    title = "ছবি সংকুচিত করুন",
                    subtitle = "মোবাইল ডেটায় ছবির মান কমান",
                    checked = config.isImageCompressionEnabled,
                    onCheckedChange = viewModel::setImageCompression
                )
                SettingsDivider()
                DataSaverClickItem(
                    icon = Icons.Default.Image,
                    title = "ছবির মান",
                    subtitle = config.imageDataQuality.label,
                    onClick = viewModel::showImageQualityDialog
                )
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // ── Data Control ──
            SectionHeader("ডেটা নিয়ন্ত্রণ")

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                DataSaverToggle(
                    icon = Icons.Default.Backup,
                    title = "ব্যাকগ্রাউন্ড ডেটা",
                    subtitle = "অ্যাপ ব্যাকগ্রাউন্ডে ডেটা ব্যবহার করুক",
                    checked = config.isBackgroundDataEnabled,
                    onCheckedChange = viewModel::setBackgroundData
                )
                SettingsDivider()
                DataSaverToggle(
                    icon = Icons.Default.CloudDownload,
                    title = "মোবাইলে ডাউনলোড",
                    subtitle = "মোবাইল ডেটায় ভিডিও ডাউনলোড",
                    checked = config.isDownloadOnMobileEnabled,
                    onCheckedChange = viewModel::setDownloadOnMobile
                )
                SettingsDivider()
                DataSaverClickItem(
                    icon = Icons.Default.NetworkCheck,
                    title = "মাসিক ডেটা সীমা",
                    subtitle = if (config.monthlyDataLimitMB > 0) "${config.monthlyDataLimitMB} MB" else "সীমাহীন",
                    onClick = viewModel::showDataLimitDialog
                )
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp32))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        color = SkyBlue,
        modifier = Modifier.padding(vertical = DesignToken.Space.dp8, horizontal = DesignToken.Space.dp4)
    )
}

@Composable
private fun DataSaverToggle(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium))
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Neutral500)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange, colors = SwitchDefaults.colors(checkedTrackColor = Green))
    }
}

@Composable
private fun DataSaverClickItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium))
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Neutral500)
        }
        Text(subtitle, style = MaterialTheme.typography.bodySmall, color = SkyBlue)
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
        thickness = 0.5.dp
    )
}

@Composable
private fun SingleChoiceDialog(
    title: String,
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                options.forEachIndexed { index, option ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            option,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = if (index == selectedIndex) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (index == selectedIndex) SkyBlue else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        if (index == selectedIndex) {
                            Text("✓", color = SkyBlue, fontWeight = FontWeight.Bold)
                        }
                    }
                    if (index < options.lastIndex) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("বন্ধ করুন") } }
    )
}

@Composable
private fun DataLimitDialog(
    currentLimit: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val options = listOf(0 to "সীমাহীন", 500 to "500 MB", 1000 to "1 GB", 2000 to "2 GB", 3000 to "3 GB", 5000 to "5 GB")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("মাসিক ডেটা সীমা") },
        text = {
            Column {
                options.forEach { (mb, label) ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            label,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = if (mb == currentLimit) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (mb == currentLimit) SkyBlue else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        if (mb == currentLimit) {
                            Text("✓", color = SkyBlue, fontWeight = FontWeight.Bold)
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("বন্ধ করুন") } }
    )
}
