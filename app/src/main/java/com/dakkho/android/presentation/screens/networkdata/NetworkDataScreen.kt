package com.dakkho.android.presentation.screens.networkdata

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
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.Green
import com.dakkho.android.presentation.theme.SkyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkDataScreen(
    onBackClick: () -> Unit,
    viewModel: NetworkDataViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val bandwidthInput = remember { mutableStateOf("") }

    // Bandwidth limit dialog
    if (uiState.showBandwidthLimitDialog) {
        AlertDialog(
            onDismissRequest = viewModel::dismissBandwidthLimitDialog,
            title = { Text("ব্যান্ডউইথ সীমা") },
            text = {
                Column {
                    Text("মাসিক ডেটা সীমা নির্ধারণ করুন (MB তে)")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = bandwidthInput.value,
                        onValueChange = { bandwidthInput.value = it },
                        label = { Text("MB") },
                        placeholder = { Text("0 = সীমাহীন") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val limit = bandwidthInput.value.toIntOrNull() ?: 0
                    viewModel.setBandwidthLimit(limit)
                    viewModel.dismissBandwidthLimitDialog()
                }) {
                    Text("সেভ", color = SkyBlue)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissBandwidthLimitDialog) {
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
                        text = "নেটওয়ার্ক ও ডেটা",
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

            // ── Network Status Card ──
            Text(
                text = "নেটওয়ার্ক অবস্থা",
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
                    Icon(
                        imageVector = Icons.Default.Wifi,
                        contentDescription = null,
                        modifier = Modifier.size(DesignToken.IconSize.large),
                        tint = if (config.isWifiConnected) Green else MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(DesignToken.Space.dp12))
                    Column {
                        Text(
                            text = if (config.isWifiConnected) "Wi-Fi সংযুক্ত" else "মোবাইল ডেটা",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = if (config.isOnMeteredNetwork)
                                "মিটারযুক্ত নেটওয়ার্ক — ডেটা সেভার সুপারিশকৃত"
                            else
                                "আনমিটারযুক্ত নেটওয়ার্ক",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (config.isOnMeteredNetwork) MaterialTheme.colorScheme.error
                                else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // ── Wi-Fi Only Toggle ──
            Text(
                text = "নেটওয়ার্ক পছন্দ",
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
                        imageVector = Icons.Default.Wifi,
                        contentDescription = null,
                        modifier = Modifier.size(DesignToken.IconSize.medium),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(DesignToken.Space.dp16))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "শুধু Wi-Fi তে স্ট্রিম",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                        )
                        Text(
                            text = "মোবাইল ডেটায় ভিডিও প্লে বন্ধ",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = config.isWifiOnlyEnabled,
                        onCheckedChange = viewModel::setWifiOnlyEnabled,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = SkyBlue,
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // ── Data Saver Mode ──
            Text(
                text = "ডেটা সেভার",
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
                            text = "ডেটা সেভার মোড",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                        )
                        Text(
                            text = "ভিডিও মান কমিয়ে ডেটা ব্যবহার কমান",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = config.isDataSaverModeEnabled,
                        onCheckedChange = viewModel::setDataSaverMode,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = SkyBlue,
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // ── Bandwidth Limit ──
            Text(
                text = "ব্যান্ডউইথ সীমা",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = SkyBlue,
                modifier = Modifier.padding(vertical = DesignToken.Space.dp8)
            )

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(DesignToken.Space.dp16)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DataUsage,
                            contentDescription = null,
                            modifier = Modifier.size(DesignToken.IconSize.medium),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(DesignToken.Space.dp16))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "মাসিক ডেটা সীমা",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                            )
                            Text(
                                text = if (config.isBandwidthLimitSet)
                                    "${config.bandwidthLimitMB} MB"
                                else
                                    "সীমাহীন",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        TextButton(onClick = {
                            bandwidthInput.value = if (config.isBandwidthLimitSet)
                                config.bandwidthLimitMB.toString() else ""
                            viewModel.showBandwidthLimitDialog()
                        }) {
                            Text("সেট", color = SkyBlue)
                        }
                    }

                    // Usage progress bar
                    if (config.isBandwidthLimitSet) {
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { config.dataUsagePercent.coerceIn(0f, 100f) / 100f },
                            modifier = Modifier.fillMaxWidth(),
                            color = if (config.dataUsagePercent > 80) MaterialTheme.colorScheme.error
                                else SkyBlue,
                            trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "%.0f MB ব্যবহৃত".format(config.dataUsedThisMonthMB),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${config.bandwidthLimitMB} MB",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp32))
        }
    }
}
