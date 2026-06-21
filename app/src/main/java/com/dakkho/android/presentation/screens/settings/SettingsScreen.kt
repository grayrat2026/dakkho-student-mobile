package com.dakkho.android.presentation.screens.settings

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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FontDownload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.VideoSettings
import androidx.compose.material.icons.filled.DataSaverOn
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.dakkho.android.domain.model.AppLanguage
import com.dakkho.android.domain.model.FontSize
import com.dakkho.android.domain.model.VideoQuality
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.Error
import com.dakkho.android.presentation.theme.Neutral500
import com.dakkho.android.presentation.theme.SkyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onDeleteAccount: () -> Unit = {},
    onNavigateToStorageManagement: () -> Unit = {},
    onNavigateToNotificationPreferences: () -> Unit = {},
    onNavigateToDataSaver: () -> Unit = {},
    onNavigateToAccessibilitySettings: () -> Unit = {},
    onNavigateToAboutLegal: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    // Dialogs
    if (uiState.showLanguageDialog) {
        SingleChoiceDialog(
            title = "ভাষা নির্বাচন",
            options = AppLanguage.entries.map { it.label },
            selectedIndex = AppLanguage.entries.indexOf(uiState.settings.language),
            onSelect = { viewModel.setLanguage(AppLanguage.entries[it]) },
            onDismiss = viewModel::dismissLanguageDialog
        )
    }

    if (uiState.showVideoQualityDialog) {
        SingleChoiceDialog(
            title = "ভিডিও মান",
            options = VideoQuality.entries.map { it.label },
            selectedIndex = VideoQuality.entries.indexOf(uiState.settings.videoQuality),
            onSelect = { viewModel.setVideoQuality(VideoQuality.entries[it]) },
            onDismiss = viewModel::dismissVideoQualityDialog
        )
    }

    if (uiState.showFontSizeDialog) {
        SingleChoiceDialog(
            title = "ফন্ট সাইজ",
            options = FontSize.entries.map { it.label },
            selectedIndex = FontSize.entries.indexOf(uiState.settings.fontSize),
            onSelect = { viewModel.setFontSize(FontSize.entries[it]) },
            onDismiss = viewModel::dismissFontSizeDialog
        )
    }

    if (uiState.showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = viewModel::dismissDeleteAccountDialog,
            title = { Text("অ্যাকাউন্ট মুছুন") },
            text = { Text("আপনি কি নিশ্চিত? এই কাজটি পূর্বাবস্থায় ফেরানো যাবে না। আপনার সমস্ত ডেটা মুছে যাবে।") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.dismissDeleteAccountDialog()
                    onDeleteAccount()
                }) {
                    Text("মুছুন", color = Error)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissDeleteAccountDialog) {
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
                        text = "সেটিংস",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = DesignToken.Space.dp16)
        ) {
            val settings = uiState.settings

            // ── Appearance ──
            SettingsSectionHeader(title = "চেহারা")

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                SettingsToggleItem(
                    icon = Icons.Default.DarkMode,
                    title = "ডার্ক মোড",
                    subtitle = "অন্ধকার থিম ব্যবহার করুন",
                    checked = settings.isDarkMode,
                    onCheckedChange = viewModel::setDarkMode
                )
                SettingsDivider()
                SettingsClickItem(
                    icon = Icons.Default.FontDownload,
                    title = "ফন্ট সাইজ",
                    subtitle = settings.fontSize.label,
                    onClick = viewModel::showFontSizeDialog
                )
                SettingsDivider()
                SettingsClickItem(
                    icon = Icons.Default.Language,
                    title = "ভাষা",
                    subtitle = settings.language.label,
                    onClick = viewModel::showLanguageDialog
                )
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // ── Video & Playback ──
            SettingsSectionHeader(title = "ভিডিও ও প্লেব্যাক")

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                SettingsClickItem(
                    icon = Icons.Default.VideoSettings,
                    title = "ভিডিও মান",
                    subtitle = settings.videoQuality.label,
                    onClick = viewModel::showVideoQualityDialog
                )
                SettingsDivider()
                SettingsToggleItem(
                    icon = Icons.Default.PlayCircle,
                    title = "স্বয়ংক্রিয় প্লে",
                    subtitle = "পরবর্তী পাঠ স্বয়ংক্রিয়ভাবে চালান",
                    checked = settings.isAutoPlayNext,
                    onCheckedChange = viewModel::setAutoPlayNext
                )
                SettingsDivider()
                SettingsToggleItem(
                    icon = Icons.Default.PictureInPicture,
                    title = "পিকচার ইন পিকচার",
                    subtitle = "অন্য অ্যাপ ব্যবহারের সময় ভিডিও দেখুন",
                    checked = settings.isPictureInPictureEnabled,
                    onCheckedChange = viewModel::setPictureInPicture
                )
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // ── Notifications ──
            SettingsSectionHeader(title = "বিজ্ঞপ্তি")

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                SettingsNavigationItem(
                    icon = Icons.Default.Notifications,
                    title = "বিজ্ঞপ্তি পছন্দ",
                    subtitle = "বিভাগ, সাউন্ড, শান্ত সময়",
                    onClick = onNavigateToNotificationPreferences
                )
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // ── Downloads ──
            SettingsSectionHeader(title = "ডাউনলোড")

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                SettingsToggleItem(
                    icon = Icons.Default.Download,
                    title = "শুধু Wi-Fi এ ডাউনলোড",
                    subtitle = "মোবাইল ডেটায় ডাউনলোড বন্ধ রাখুন",
                    checked = settings.isDownloadWifiOnly,
                    onCheckedChange = viewModel::setDownloadWifiOnly
                )
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // ── Storage & Data ──
            SettingsSectionHeader(title = "স্টোরেজ ও ডেটা")

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                SettingsNavigationItem(
                    icon = Icons.Default.Storage,
                    title = "স্টোরেজ ব্যবস্থাপনা",
                    subtitle = "ক্যাশে, ডাউনলোড, স্টোরেজ তথ্য",
                    onClick = onNavigateToStorageManagement
                )
                SettingsDivider()
                SettingsNavigationItem(
                    icon = Icons.Default.DataSaverOn,
                    title = "ডেটা সেভার",
                    subtitle = "মোবাইল ডেটা ব্যবহার নিয়ন্ত্রণ",
                    onClick = onNavigateToDataSaver
                )
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // ── Accessibility ──
            SettingsSectionHeader(title = "প্রবেশযোগ্যতা")

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                SettingsNavigationItem(
                    icon = Icons.Default.Accessibility,
                    title = "প্রবেশযোগ্যতা সেটিংস",
                    subtitle = "উচ্চ বৈসাদৃশ্য, গতি কমান, টেক্সট",
                    onClick = onNavigateToAccessibilitySettings
                )
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // ── Privacy & Data ──
            SettingsSectionHeader(title = "গোপনীয়তা ও ডেটা")

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                SettingsToggleItem(
                    icon = Icons.Default.Security,
                    title = "ব্যবহার বিশ্লেষণ",
                    subtitle = "অ্যাপ উন্নতির জন্য ডেটা শেয়ার করুন",
                    checked = settings.isAnalyticsEnabled,
                    onCheckedChange = viewModel::setAnalyticsEnabled
                )
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // ── About ──
            SettingsSectionHeader(title = "অ্যাপ সম্পর্কে")

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                SettingsNavigationItem(
                    icon = Icons.Default.Info,
                    title = "অ্যাপ তথ্য ও আইনি",
                    subtitle = "ভার্সন, লাইসেন্স, গোপনীয়তা নীতি",
                    onClick = onNavigateToAboutLegal
                )
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp24))

            // ── Danger Zone ──
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                SettingsClickItem(
                    icon = Icons.Default.Delete,
                    title = "অ্যাকাউন্ট মুছুন",
                    subtitle = "এই কাজটি পূর্বাবস্থায় ফেরানো যাবে না",
                    onClick = viewModel::showDeleteAccountDialog,
                    tint = Error
                )
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp32))
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall.copy(
            fontWeight = FontWeight.Bold
        ),
        color = SkyBlue,
        modifier = Modifier.padding(vertical = DesignToken.Space.dp8, horizontal = DesignToken.Space.dp4)
    )
}

@Composable
private fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    tint: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DesignToken.Space.dp16, vertical = DesignToken.Space.dp12),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(DesignToken.IconSize.medium)
        )
        Spacer(modifier = Modifier.width(DesignToken.Space.dp16))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = tint
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Neutral500
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = SkyBlue,
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary
            )
        )
    }
}

@Composable
private fun SettingsClickItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    tint: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DesignToken.Space.dp16, vertical = DesignToken.Space.dp12),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(DesignToken.IconSize.medium)
        )
        Spacer(modifier = Modifier.width(DesignToken.Space.dp16))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = tint
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Neutral500
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = tint.copy(alpha = 0.5f),
            modifier = Modifier.size(DesignToken.IconSize.medium)
        )
    }
}

@Composable
private fun SettingsNavigationItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    tint: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DesignToken.Space.dp16, vertical = DesignToken.Space.dp12),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(DesignToken.IconSize.medium)
        )
        Spacer(modifier = Modifier.width(DesignToken.Space.dp16))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = tint
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Neutral500
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = tint.copy(alpha = 0.5f),
            modifier = Modifier.size(DesignToken.IconSize.medium)
        )
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = DesignToken.Space.dp16),
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = if (index == selectedIndex) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (index == selectedIndex) SkyBlue else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        if (index == selectedIndex) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = SkyBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    if (index < options.lastIndex) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("বন্ধ করুন")
            }
        }
    )
}
