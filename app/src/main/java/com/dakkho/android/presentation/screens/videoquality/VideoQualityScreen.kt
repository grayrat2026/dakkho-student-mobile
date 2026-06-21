package com.dakkho.android.presentation.screens.videoquality

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
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.NetworkCheck
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dakkho.android.domain.model.StreamingQuality
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.SkyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoQualityScreen(
    onBackClick: () -> Unit,
    viewModel: VideoQualityViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "ভিডিও মান",
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

            // ── Streaming Quality Radio Group ──
            Text(
                text = "স্ট্রিমিং মান",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = SkyBlue,
                modifier = Modifier.padding(vertical = DesignToken.Space.dp8)
            )

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(vertical = DesignToken.Space.dp8)) {
                    StreamingQuality.entries.forEach { quality ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = DesignToken.Space.dp16, vertical = DesignToken.Space.dp4),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = settings.streamingQuality == quality,
                                onClick = { viewModel.setStreamingQuality(quality) },
                                colors = RadioButtonDefaults.colors(selectedColor = SkyBlue)
                            )
                            Spacer(modifier = Modifier.width(DesignToken.Space.dp12))
                            Icon(
                                imageVector = Icons.Default.HighQuality,
                                contentDescription = null,
                                modifier = Modifier.size(DesignToken.IconSize.medium),
                                tint = if (settings.streamingQuality == quality) SkyBlue
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(DesignToken.Space.dp8))
                            Column {
                                Text(
                                    text = quality.label,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = if (settings.streamingQuality == quality) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (settings.streamingQuality == quality) SkyBlue
                                        else MaterialTheme.colorScheme.onSurface
                                )
                                if (quality != StreamingQuality.AUTO) {
                                    Text(
                                        text = "সর্বোচ্চ ${quality.maxBitrate / 1_000_000} Mbps",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                } else {
                                    Text(
                                        text = "নেটওয়ার্ক অনুযায়ী মান নির্বাচন",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // ── Bandwidth Saver ──
            Text(
                text = "ব্যান্ডউইথ সেভার",
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
                        imageVector = Icons.Default.NetworkCheck,
                        contentDescription = null,
                        modifier = Modifier.size(DesignToken.IconSize.medium),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(DesignToken.Space.dp16))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ব্যান্ডউইথ সেভার মোড",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                        )
                        Text(
                            text = "ডেটা ব্যবহার কমাতে ভিডিও মান স্বয়ংক্রিয়ভাবে কমান",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = settings.isBandwidthSaverEnabled,
                        onCheckedChange = viewModel::setBandwidthSaver,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = SkyBlue,
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // ── ExoPlayer Integration Info ──
            Text(
                text = "প্লেয়ার তথ্য",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = SkyBlue,
                modifier = Modifier.padding(vertical = DesignToken.Space.dp8)
            )

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(DesignToken.Space.dp16)) {
                    Text(
                        text = "নির্বাচিত মান: ${settings.streamingQuality.label}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (settings.streamingQuality == StreamingQuality.AUTO)
                            "ExoPlayer স্বয়ংক্রিয়ভাবে সর্বোত্তম মান নির্বাচন করবে"
                        else
                            "ExoPlayer DefaultTrackSelector সর্বোচ্চ ${settings.streamingQuality.maxBitrate / 1_000_000} Mbps ব্যবহার করবে",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (settings.isBandwidthSaverEnabled) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "ব্যান্ডউইথ সেভার সক্রিয় — মোবাইল নেটওয়ার্কে মান আরও কমানো হবে",
                            style = MaterialTheme.typography.bodySmall,
                            color = SkyBlue
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp32))
        }
    }
}
