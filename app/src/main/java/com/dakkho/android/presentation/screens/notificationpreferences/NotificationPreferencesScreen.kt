package com.dakkho.android.presentation.screens.notificationpreferences

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
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dakkho.android.domain.model.NotificationSound
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.Neutral500
import com.dakkho.android.presentation.theme.SkyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationPreferencesScreen(
    onBackClick: () -> Unit,
    viewModel: NotificationPreferencesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val prefs = uiState.preferences

    // Sound dialog
    if (uiState.showSoundDialog) {
        AlertDialog(
            onDismissRequest = viewModel::dismissSoundDialog,
            title = { Text("নোটিফিকেশন সাউন্ড") },
            text = {
                Column {
                    NotificationSound.entries.forEachIndexed { index, sound ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = sound.label,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = if (sound == prefs.notificationSound) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (sound == prefs.notificationSound) SkyBlue else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            if (sound == prefs.notificationSound) {
                                Text("✓", color = SkyBlue, fontWeight = FontWeight.Bold)
                            }
                        }
                        if (index < NotificationSound.entries.lastIndex) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = viewModel::dismissSoundDialog) { Text("বন্ধ করুন") }
            }
        )
    }

    // Quiet hours dialog
    if (uiState.showQuietHoursDialog) {
        QuietHoursDialog(
            startTime = prefs.quietHoursStart,
            endTime = prefs.quietHoursEnd,
            onStartChange = viewModel::setQuietHoursStart,
            onEndChange = viewModel::setQuietHoursEnd,
            onDismiss = viewModel::dismissQuietHoursDialog
        )
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "বিজ্ঞপ্তি পছন্দ",
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
            // ── Channels ──
            SectionHeader("নোটিফিকেশন চ্যানেল")

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                NotifToggle(
                    icon = Icons.Default.NotificationsActive,
                    title = "পুশ বিজ্ঞপ্তি",
                    subtitle = "ফোনে বিজ্ঞপ্তি পান",
                    checked = prefs.isPushEnabled,
                    onCheckedChange = viewModel::setPushEnabled
                )
                SettingsDivider()
                NotifToggle(
                    icon = Icons.Default.Email,
                    title = "ইমেইল বিজ্ঞপ্তি",
                    subtitle = "ইমেইলে আপডেট পান",
                    checked = prefs.isEmailEnabled,
                    onCheckedChange = viewModel::setEmailEnabled
                )
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // ── Categories ──
            SectionHeader("বিভাগ")

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                NotifToggle(
                    icon = Icons.Default.School,
                    title = "কোর্স আপডেট",
                    subtitle = "নতুন লেসন, রিসোর্স ও পরিবর্তন",
                    checked = prefs.isCourseUpdatesEnabled,
                    onCheckedChange = viewModel::setCourseUpdatesEnabled
                )
                SettingsDivider()
                NotifToggle(
                    icon = Icons.Default.CalendarMonth,
                    title = "লাইভ ক্লাস রিমাইন্ডার",
                    subtitle = "আসন্ন লাইভ সেশনের স্মরণ",
                    checked = prefs.isLiveClassReminderEnabled,
                    onCheckedChange = viewModel::setLiveClassReminderEnabled
                )
                SettingsDivider()
                NotifToggle(
                    icon = Icons.Default.Assignment,
                    title = "অ্যাসাইনমেন্ট রিমাইন্ডার",
                    subtitle = "জমা দেওয়ার শেষ তারিখ",
                    checked = prefs.isAssignmentReminderEnabled,
                    onCheckedChange = viewModel::setAssignmentReminderEnabled
                )
                SettingsDivider()
                NotifToggle(
                    icon = Icons.Default.Chat,
                    title = "আলোচনা উত্তর",
                    subtitle = "আপনার প্রশ্নের উত্তর",
                    checked = prefs.isDiscussionReplyEnabled,
                    onCheckedChange = viewModel::setDiscussionReplyEnabled
                )
                SettingsDivider()
                NotifToggle(
                    icon = Icons.Default.EmojiEvents,
                    title = "অর্জন",
                    subtitle = "ব্যাজ ও মাইলফোন আনলক",
                    checked = prefs.isAchievementNotificationEnabled,
                    onCheckedChange = viewModel::setAchievementNotificationEnabled
                )
                SettingsDivider()
                NotifToggle(
                    icon = Icons.Default.Campaign,
                    title = "প্রচারমূলক",
                    subtitle = "ডিসকাউন্ট, অফার ও নতুন কোর্স",
                    checked = prefs.isPromotionalEnabled,
                    onCheckedChange = viewModel::setPromotionalEnabled
                )
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // ── Sound & Vibration ──
            SectionHeader("সাউন্ড ও ভাইব্রেশন")

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                NotifClickItem(
                    icon = Icons.Default.VolumeUp,
                    title = "নোটিফিকেশন সাউন্ড",
                    subtitle = prefs.notificationSound.label,
                    onClick = viewModel::showSoundDialog
                )
                SettingsDivider()
                NotifToggle(
                    icon = Icons.Default.Vibration,
                    title = "ভাইব্রেশন",
                    subtitle = "বিজ্ঞপ্তিতে ভাইব্রেট",
                    checked = prefs.isVibrationEnabled,
                    onCheckedChange = viewModel::setVibrationEnabled
                )
                SettingsDivider()
                NotifToggle(
                    icon = Icons.Default.Lightbulb,
                    title = "LED নোটিফিকেশন লাইট",
                    subtitle = "বিজ্ঞপ্তিতে LED জ্বলুক",
                    checked = prefs.isLedEnabled,
                    onCheckedChange = viewModel::setLedEnabled
                )
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // ── Quiet Hours ──
            SectionHeader("শান্ত সময়")

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                NotifToggle(
                    icon = Icons.Default.Schedule,
                    title = "শান্ত সময় সক্রিয়",
                    subtitle = "নির্দিষ্ট সময়ে বিজ্ঞপ্তি নীরব",
                    checked = prefs.quietHoursEnabled,
                    onCheckedChange = viewModel::setQuietHoursEnabled
                )
                if (prefs.quietHoursEnabled) {
                    SettingsDivider()
                    NotifClickItem(
                        icon = Icons.Default.Schedule,
                        title = "শান্ত সময় নির্ধারণ",
                        subtitle = "${prefs.quietHoursStart} - ${prefs.quietHoursEnd}",
                        onClick = viewModel::showQuietHoursDialog
                    )
                }
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
private fun NotifToggle(
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
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedTrackColor = SkyBlue)
        )
    }
}

@Composable
private fun NotifClickItem(
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
private fun QuietHoursDialog(
    startTime: String,
    endTime: String,
    onStartChange: (String) -> Unit,
    onEndChange: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val timeOptions = (0..23).flatMap { hour ->
        listOf(0, 30).map { minute ->
            String.format("%02d:%02d", hour, minute)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("শান্ত সময় নির্ধারণ") },
        text = {
            Column {
                Text("শুরুর সময়", style = MaterialTheme.typography.labelMedium, color = SkyBlue)
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    listOf("21:00", "22:00", "23:00").forEach { time ->
                        TextButton(
                            onClick = { onStartChange(time) },
                            colors = if (time == startTime) androidx.compose.material3.ButtonDefaults.textButtonColors(
                                contentColor = SkyBlue
                            ) else androidx.compose.material3.ButtonDefaults.textButtonColors()
                        ) {
                            Text(time, fontWeight = if (time == startTime) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
                Text("শেষের সময়", style = MaterialTheme.typography.labelMedium, color = SkyBlue)
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    listOf("06:00", "07:00", "08:00").forEach { time ->
                        TextButton(
                            onClick = { onEndChange(time) },
                            colors = if (time == endTime) androidx.compose.material3.ButtonDefaults.textButtonColors(
                                contentColor = SkyBlue
                            ) else androidx.compose.material3.ButtonDefaults.textButtonColors()
                        ) {
                            Text(time, fontWeight = if (time == endTime) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("সংরক্ষণ") }
        }
    )
}
