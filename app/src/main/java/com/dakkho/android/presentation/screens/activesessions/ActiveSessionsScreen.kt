package com.dakkho.android.presentation.screens.activesessions

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dakkho.android.domain.model.ActiveSession
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.Green
import com.dakkho.android.presentation.theme.SkyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveSessionsScreen(
    onBackClick: () -> Unit,
    viewModel: ActiveSessionsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    // Logout confirmation dialog
    uiState.showLogoutConfirmDialog?.let { sessionId ->
        val session = uiState.sessions.find { it.id == sessionId }
        AlertDialog(
            onDismissRequest = viewModel::dismissLogoutConfirm,
            title = { Text("রিমোট লগআউট") },
            text = {
                Text(
                    "আপনি কি নিশ্চিত যে আপনি এই ডিভাইস থেকে লগআউট করতে চান?\n\n" +
                        "ডিভাইস: ${session?.displayModel ?: "অজানা"}"
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.logoutSession(sessionId) }) {
                    Text("লগআউট", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissLogoutConfirm) {
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
                        text = "সক্রিয় সেশন",
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
        if (uiState.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(color = SkyBlue)
                Spacer(modifier = Modifier.height(16.dp))
                Text("সেশন লোড হচ্ছে...", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = DesignToken.Space.dp16),
                verticalArrangement = Arrangement.spacedBy(DesignToken.Space.dp12)
            ) {
                // ── Current Device Info ──
                item {
                    Text(
                        text = "বর্তমান ডিভাইস",
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
                                imageVector = Icons.Default.PhoneAndroid,
                                contentDescription = null,
                                modifier = Modifier.size(DesignToken.IconSize.large),
                                tint = Green
                            )
                            Spacer(modifier = Modifier.width(DesignToken.Space.dp12))
                            Column {
                                Text(
                                    text = uiState.currentDeviceFingerprint.displayString,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "API ${uiState.currentDeviceFingerprint.sdkInt}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "বর্তমান সেশন",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Green
                                )
                            }
                        }
                    }
                }

                // ── Other Sessions ──
                item {
                    val otherSessions = uiState.sessions.filter { !it.isCurrentDevice }
                    Text(
                        text = "অন্যান্য সেশন (${otherSessions.size})",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = SkyBlue,
                        modifier = Modifier.padding(vertical = DesignToken.Space.dp8)
                    )
                }

                val otherSessions = uiState.sessions.filter { !it.isCurrentDevice }
                if (otherSessions.isEmpty()) {
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(DesignToken.Space.dp24),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Computer,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "অন্য কোনো সক্রিয় সেশন নেই",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(otherSessions, key = { it.id }) { session ->
                        SessionCard(
                            session = session,
                            isLoggingOut = uiState.isLoggingOut == session.id,
                            onLogoutClick = { viewModel.showLogoutConfirm(session.id) }
                        )
                    }
                }

                // ── Security Tips ──
                item {
                    Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(DesignToken.Space.dp16)) {
                            Text(
                                text = "নিরাপত্তা টিপস",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = SkyBlue
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "আপনি যদি কোনো অপরিচিত ডিভাইস দেখেন, তাহলে সাথে সাথে সেই সেশন থেকে লগআউট করুন এবং আপনার পাসওয়ার্ড পরিবর্তন করুন।",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(DesignToken.Space.dp32))
                }
            }
        }
    }
}

@Composable
private fun SessionCard(
    session: ActiveSession,
    isLoggingOut: Boolean,
    onLogoutClick: () -> Unit
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignToken.Space.dp16),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Computer,
                contentDescription = null,
                modifier = Modifier.size(DesignToken.IconSize.large),
                tint = if (session.isActive) SkyBlue else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(DesignToken.Space.dp12))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = session.displayModel,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                )
                Text(
                    text = "সর্বশেষ: ${session.lastActiveTime}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "IP: ${session.lastIpAddress}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (session.isActive) {
                    Text(
                        text = "সক্রিয়",
                        style = MaterialTheme.typography.labelSmall,
                        color = Green
                    )
                }
            }
            if (isLoggingOut) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = SkyBlue
                )
            } else {
                OutlinedButton(
                    onClick = onLogoutClick,
                    colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("লগআউট")
                }
            }
        }
    }
}
