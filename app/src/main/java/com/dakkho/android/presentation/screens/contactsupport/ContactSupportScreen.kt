package com.dakkho.android.presentation.screens.contactsupport

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.Green
import com.dakkho.android.presentation.theme.SkyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactSupportScreen(
    onBackClick: () -> Unit,
    onNavigateToLiveChat: () -> Unit = {},
    viewModel: ContactSupportViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "সাপোর্টে যোগাযোগ",
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
                .padding(horizontal = DesignToken.Space.dp16)
        ) {
            Spacer(modifier = Modifier.height(DesignToken.Space.dp8))

            // Section Header
            Text(
                text = "যোগাযোগের মাধ্যম",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = SkyBlue,
                modifier = Modifier.padding(bottom = DesignToken.Space.dp12)
            )

            // Live Chat Option
            ContactOptionCard(
                icon = Icons.Default.Chat,
                title = "লাইভ চ্যাট",
                subtitle = "রিয়েল-টাইম সহায়তা",
                accentColor = SkyBlue,
                onClick = onNavigateToLiveChat
            )

            Spacer(modifier = Modifier.height(DesignToken.Space.dp12))

            // Email Option
            ContactOptionCard(
                icon = Icons.Default.Email,
                title = "ইমেইল",
                subtitle = "support@dakkho.com.bd",
                accentColor = SkyBlue,
                onClick = {
                    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:support@dakkho.com.bd")
                        putExtra(Intent.EXTRA_SUBJECT, "DAKKHO সাপোর্ট অনুরোধ")
                    }
                    context.startActivity(Intent.createChooser(emailIntent, "ইমেইল পাঠান"))
                }
            )

            Spacer(modifier = Modifier.height(DesignToken.Space.dp12))

            // Phone Option
            ContactOptionCard(
                icon = Icons.Default.Phone,
                title = "ফোন",
                subtitle = "+880 1234-567890",
                accentColor = SkyBlue,
                onClick = {
                    val phoneIntent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:+8801234567890")
                    }
                    context.startActivity(phoneIntent)
                }
            )

            Spacer(modifier = Modifier.height(DesignToken.Space.dp24))

            // Operating Hours Card
            GlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(DesignToken.IconSize.large),
                        tint = SkyBlue
                    )

                    Spacer(modifier = Modifier.width(DesignToken.Space.dp16))

                    Column {
                        Text(
                            text = "কার্যসময়",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(DesignToken.Space.dp4))
                        Text(
                            text = uiState.operatingDays,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(DesignToken.Space.dp2))
                        Text(
                            text = uiState.operatingHours,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp24))

            // WhatsApp Button
            Button(
                onClick = {
                    val whatsappIntent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("https://wa.me/8801234567890?text=%E0%A6%B9%E0%A7%8D%E0%A6%AF%E0%A6%BE%E0%A6%B2%E0%A7%8B%2C%20%E0%A6%86%E0%A6%AE%E0%A6%BE%E0%A6%B0%20DAKKHO%20%E0%A6%85%E0%A7%8D%E0%A6%AF%E0%A6%BE%E0%A6%AA%20%E0%A6%B8%E0%A6%AE%E0%A7%8D%E0%A6%AA%E0%A6%B0%E0%A7%8D%E0%A6%95%E0%A7%87%20%E0%A6%B8%E0%A6%B9%E0%A6%BE%E0%A6%AF%E0%A6%BC%E0%A6%A4%E0%A6%BE%20%E0%A6%AA%E0%A7%8D%E0%A6%B0%E0%A6%AF%E0%A6%BC%E0%A7%8B%E0%A6%9C%E0%A6%A8%E0%A5%A4")
                    }
                    try {
                        context.startActivity(whatsappIntent)
                    } catch (e: Exception) {
                        // Fallback to web version
                        val webIntent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("https://web.whatsapp.com/send?phone=8801234567890")
                        }
                        context.startActivity(webIntent)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(DesignToken.ComponentSize.buttonHeight),
                shape = DesignToken.Shape.large,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Green
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Chat,
                    contentDescription = null,
                    modifier = Modifier.size(DesignToken.IconSize.medium),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(DesignToken.Space.dp8))
                Text(
                    text = "WhatsApp এ মেসেজ করুন",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp32))
        }
    }
}

@Composable
private fun ContactOptionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clip(DesignToken.Shape.large)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignToken.Space.dp4),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon container
            Box(
                modifier = Modifier
                    .size(DesignToken.ComponentSize.avatarMedium)
                    .clip(DesignToken.Shape.medium),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(DesignToken.IconSize.medium),
                    tint = accentColor
                )
            }

            Spacer(modifier = Modifier.width(DesignToken.Space.dp16))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                modifier = Modifier
                    .size(16.dp)
                    .rotate(180f),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}
