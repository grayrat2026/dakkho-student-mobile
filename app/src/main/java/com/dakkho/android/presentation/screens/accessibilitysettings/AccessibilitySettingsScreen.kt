package com.dakkho.android.presentation.screens.accessibilitysettings

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
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.MotionPhotosOff
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.SpaceBar
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Visibility
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
import com.dakkho.android.domain.model.ContentScale
import com.dakkho.android.domain.model.TouchTargetSize
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.Neutral500
import com.dakkho.android.presentation.theme.SkyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccessibilitySettingsScreen(
    onBackClick: () -> Unit,
    viewModel: AccessibilitySettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val settings = uiState.settings

    // Content scale dialog
    if (uiState.showContentScaleDialog) {
        SingleChoiceDialog(
            title = "কন্টেন্ট স্কেল",
            options = ContentScale.entries.map { "${it.label} (${(it.scale * 100).toInt()}%)" },
            selectedIndex = ContentScale.entries.indexOf(settings.contentScale),
            onSelect = { viewModel.setContentScale(ContentScale.entries[it]) },
            onDismiss = viewModel::dismissContentScaleDialog
        )
    }

    // Touch target dialog
    if (uiState.showTouchTargetDialog) {
        SingleChoiceDialog(
            title = "টাচ টার্গেট সাইজ",
            options = TouchTargetSize.entries.map { it.label },
            selectedIndex = TouchTargetSize.entries.indexOf(settings.minTouchTargetSize),
            onSelect = { viewModel.setTouchTargetSize(TouchTargetSize.entries[it]) },
            onDismiss = viewModel::dismissTouchTargetDialog
        )
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "প্রবেশযোগ্যতা",
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
            // ── Vision ──
            SectionHeader("দৃশ্যমানতা")

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                A11yToggle(
                    icon = Icons.Default.Visibility,
                    title = "উচ্চ বৈসাদৃশ্য",
                    subtitle = "টেক্সট ও ব্যাকগ্রাউন্ডের বৈসাদৃশ্য বাড়ান",
                    checked = settings.isHighContrastEnabled,
                    onCheckedChange = viewModel::setHighContrast
                )
                SettingsDivider()
                A11yToggle(
                    icon = Icons.Default.ColorLens,
                    title = "রঙ বিপরীতকরণ",
                    subtitle = "রঙ উল্টে দেখুন",
                    checked = settings.isColorInversionEnabled,
                    onCheckedChange = viewModel::setColorInversion
                )
                SettingsDivider()
                A11yClickItem(
                    icon = Icons.Default.FormatSize,
                    title = "কন্টেন্ট স্কেল",
                    subtitle = "${settings.contentScale.label} (${(settings.contentScale.scale * 100).toInt()}%)",
                    onClick = viewModel::showContentScaleDialog
                )
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // ── Motion & Animation ──
            SectionHeader("গতি ও অ্যানিমেশন")

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                A11yToggle(
                    icon = Icons.Default.MotionPhotosOff,
                    title = "গতি কমান",
                    subtitle = "অ্যানিমেশন ও ট্রানজিশন কমান",
                    checked = settings.isReduceMotionEnabled,
                    onCheckedChange = viewModel::setReduceMotion
                )
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // ── Text & Reading ──
            SectionHeader("টেক্সট ও পড়া")

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                A11yToggle(
                    icon = Icons.Default.FormatBold,
                    title = "গাঢ় টেক্সট",
                    subtitle = "সমস্ত টেক্সট গাঢ় করুন",
                    checked = settings.isBoldTextEnabled,
                    onCheckedChange = viewModel::setBoldText
                )
                SettingsDivider()
                A11yToggle(
                    icon = Icons.Default.SpaceBar,
                    title = "স্পেসিং সমন্বয়",
                    subtitle = "টেক্সট ও উপাদানের মধ্যে ব্যবধান বাড়ান",
                    checked = settings.isSpacingAdjustmentEnabled,
                    onCheckedChange = viewModel::setSpacingAdjustment
                )
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // ── Interaction ──
            SectionHeader("ইন্টারঅ্যাকশন")

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                A11yToggle(
                    icon = Icons.Default.RecordVoiceOver,
                    title = "স্ক্রিন রিডার অপ্টিমাইজড",
                    subtitle = "TalkBack এর জন্য উন্নত লেবেল",
                    checked = settings.isScreenReaderOptimized,
                    onCheckedChange = viewModel::setScreenReaderOptimized
                )
                SettingsDivider()
                A11yToggle(
                    icon = Icons.Default.NearMe,
                    title = "বড় পয়েন্টার",
                    subtitle = "বড় ও স্পষ্ট টাচ পয়েন্টার",
                    checked = settings.isLargePointerEnabled,
                    onCheckedChange = viewModel::setLargePointer
                )
                SettingsDivider()
                A11yClickItem(
                    icon = Icons.Default.TouchApp,
                    title = "টাচ টার্গেট সাইজ",
                    subtitle = settings.minTouchTargetSize.label,
                    onClick = viewModel::showTouchTargetDialog
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
private fun A11yToggle(
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
        Switch(checked = checked, onCheckedChange = onCheckedChange, colors = SwitchDefaults.colors(checkedTrackColor = SkyBlue))
    }
}

@Composable
private fun A11yClickItem(
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
