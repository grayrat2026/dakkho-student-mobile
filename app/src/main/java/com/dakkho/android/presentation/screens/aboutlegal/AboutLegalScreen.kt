package com.dakkho.android.presentation.screens.aboutlegal

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.theme.DeepBlue
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.Neutral500
import com.dakkho.android.presentation.theme.SkyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutLegalScreen(
    onBackClick: () -> Unit,
    viewModel: AboutLegalViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val context = LocalContext.current
    val info = uiState.appInfo

    // Open source licenses dialog
    if (uiState.showLicensesDialog) {
        AlertDialog(
            onDismissRequest = viewModel::dismissLicensesDialog,
            title = { Text("ওপেন সোর্স লাইসেন্স") },
            text = {
                Column {
                    val licenses = listOf(
                        "Kotlin" to "Apache 2.0",
                        "Jetpack Compose" to "Apache 2.0",
                        "Material3" to "Apache 2.0",
                        "Retrofit" to "Apache 2.0",
                        "OkHttp" to "Apache 2.0",
                        "Moshi" to "Apache 2.0",
                        "Room" to "Apache 2.0",
                        "Hilt/Dagger" to "Apache 2.0",
                        "Coil" to "Apache 2.0",
                        "ExoPlayer/Media3" to "Apache 2.0",
                        "Timber" to "Apache 2.0",
                        "EncryptedSharedPreferences" to "Apache 2.0"
                    )
                    licenses.forEach { (name, license) ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                name,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                license,
                                style = MaterialTheme.typography.bodySmall,
                                color = Neutral500
                            )
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = viewModel::dismissLicensesDialog) { Text("বন্ধ করুন") }
            }
        )
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "অ্যাপ সম্পর্কে",
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
            // ── App Info Card ──
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Verified,
                        contentDescription = null,
                        tint = SkyBlue,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        info.appName,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = DeepBlue
                    )
                    Text(
                        "ভার্সন ${info.appVersion} (${info.buildNumber})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Neutral500
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "বাংলাদেশের প্রিমিয়ার পলিটেকনিক শিক্ষার্থী প্ল্যাটফর্ম",
                        style = MaterialTheme.typography.bodySmall,
                        color = Neutral500
                    )
                }
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // ── Build Info ──
            SectionHeader("বিল্ড তথ্য")

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                InfoRow(icon = Icons.Default.Build, label = "বিল্ড তারিখ", value = info.buildDate)
                SettingsDivider()
                InfoRow(icon = Icons.Default.Info, label = "ন্যূনতম অ্যান্ড্রয়েড", value = info.minAndroidVersion)
                SettingsDivider()
                InfoRow(icon = Icons.Default.Code, label = "টার্গেট SDK", value = info.targetSdkVersion)
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // ── Legal ──
            SectionHeader("আইনি")

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                AboutClickItem(
                    icon = Icons.Default.Policy,
                    title = "গোপনীয়তা নীতি",
                    onClick = { openUrl(context, info.privacyPolicyUrl) }
                )
                SettingsDivider()
                AboutClickItem(
                    icon = Icons.Default.Gavel,
                    title = "সেবার শর্তাবলী",
                    onClick = { openUrl(context, info.termsOfServiceUrl) }
                )
                SettingsDivider()
                AboutClickItem(
                    icon = Icons.Default.Article,
                    title = "ওপেন সোর্স লাইসেন্স",
                    onClick = viewModel::showLicensesDialog
                )
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // ── Contact ──
            SectionHeader("যোগাযোগ")

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                AboutClickItem(
                    icon = Icons.Default.Language,
                    title = "ওয়েবসাইট",
                    subtitle = info.websiteUrl,
                    onClick = { openUrl(context, info.websiteUrl) }
                )
                SettingsDivider()
                AboutClickItem(
                    icon = Icons.Default.Email,
                    title = "ইমেইল সাপোর্ট",
                    subtitle = info.developerEmail,
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:${info.developerEmail}")
                            putExtra(Intent.EXTRA_SUBJECT, "DAKKHO App Support")
                        }
                        context.startActivity(Intent.createChooser(intent, "ইমেইল পাঠান"))
                    }
                )
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // ── Rate App ──
            OutlinedButton(
                onClick = { openUrl(context, info.playStoreUrl) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("অ্যাপ রেট করুন")
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp8))

            Text(
                "© 2026 ${info.developerName}. সর্বস্বত্ব সংরক্ষিত।",
                style = MaterialTheme.typography.bodySmall,
                color = Neutral500,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(DesignToken.Space.dp24))
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
private fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = Neutral500)
        Spacer(modifier = Modifier.width(16.dp))
        Text(label, style = MaterialTheme.typography.bodyMedium, color = Neutral500, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
    }
}

@Composable
private fun AboutClickItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
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
            if (subtitle != null) {
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Neutral500)
            }
        }
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Neutral500,
            modifier = Modifier.size(20.dp)
        )
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

private fun openUrl(context: android.content.Context, url: String) {
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    } catch (_: Exception) { }
}
