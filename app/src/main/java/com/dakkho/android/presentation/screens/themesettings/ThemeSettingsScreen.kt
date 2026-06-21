package com.dakkho.android.presentation.screens.themesettings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dakkho.android.domain.model.AccentColor
import com.dakkho.android.domain.model.ThemeMode
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.SkyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSettingsScreen(
    onBackClick: () -> Unit,
    viewModel: ThemeSettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "থিম সেটিংস",
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
                .padding(horizontal = DesignToken.Space.dp16)
        ) {
            val settings = uiState.settings

            // ── Theme Mode ──
            Text(
                text = "মোড নির্বাচন",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = SkyBlue,
                modifier = Modifier.padding(vertical = DesignToken.Space.dp8)
            )

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(vertical = DesignToken.Space.dp8)) {
                    ThemeMode.entries.forEach { mode ->
                        ThemeModeOption(
                            mode = mode,
                            isSelected = settings.themeMode == mode,
                            onSelect = { viewModel.setThemeMode(mode) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // ── Accent Color Picker ──
            Text(
                text = "অ্যাকসেন্ট রং",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = SkyBlue,
                modifier = Modifier.padding(vertical = DesignToken.Space.dp8)
            )

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(DesignToken.Space.dp16),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    AccentColor.entries.forEach { color ->
                        AccentColorSwatch(
                            accentColor = color,
                            isSelected = settings.accentColor == color,
                            onSelect = { viewModel.setAccentColor(color) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // ── Material You Dynamic Color ──
            Text(
                text = "ডায়নামিক রং",
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ম্যাটেরিয়াল ইউ",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                        )
                        Text(
                            text = if (viewModel.isDynamicColorAvailable)
                                "ওয়ালপেপার রং থেকে থিম তৈরি করুন"
                            else
                                "Android 12+ প্রয়োজন",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (viewModel.isDynamicColorAvailable)
                                MaterialTheme.colorScheme.onSurfaceVariant
                            else MaterialTheme.colorScheme.error
                        )
                    }
                    Switch(
                        checked = settings.isDynamicColorEnabled && viewModel.isDynamicColorAvailable,
                        onCheckedChange = { viewModel.setDynamicColorEnabled(it) },
                        enabled = viewModel.isDynamicColorAvailable,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = SkyBlue,
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // ── Preview ──
            Text(
                text = "প্রিভিউ",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = SkyBlue,
                modifier = Modifier.padding(vertical = DesignToken.Space.dp8)
            )

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(DesignToken.Space.dp16),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Preview swatch
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(parseHexColor(settings.accentColor.hex))
                    )
                    Spacer(modifier = Modifier.width(DesignToken.Space.dp16))
                    Column {
                        Text(
                            text = "বর্তমান থিম: ${settings.themeMode.label}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                        )
                        Text(
                            text = "অ্যাকসেন্ট: ${settings.accentColor.label}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (settings.isDynamicColorEnabled && viewModel.isDynamicColorAvailable) {
                            Text(
                                text = "ম্যাটেরিয়াল ইউ সক্রিয়",
                                style = MaterialTheme.typography.bodySmall,
                                color = SkyBlue
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp32))
        }
    }
}

@Composable
private fun ThemeModeOption(
    mode: ThemeMode,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .padding(horizontal = DesignToken.Space.dp16, vertical = DesignToken.Space.dp8),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onSelect,
            colors = RadioButtonDefaults.colors(
                selectedColor = SkyBlue,
                unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        Spacer(modifier = Modifier.width(DesignToken.Space.dp12))
        Column {
            Text(
                text = mode.label,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
                color = if (isSelected) SkyBlue else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun AccentColorSwatch(
    accentColor: AccentColor,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val color = parseHexColor(accentColor.hex)
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(color)
            .then(
                if (isSelected) Modifier.border(3.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                else Modifier
            )
            .clickable(onClick = onSelect),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

private fun parseHexColor(hex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        SkyBlue
    }
}
