package com.dakkho.android.presentation.screens.instructorcontact

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dakkho.android.domain.model.InstructorDetail
import com.dakkho.android.domain.model.SocialLinks
import com.dakkho.android.presentation.components.AnimatedPage
import com.dakkho.android.presentation.components.EmptyState
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.components.ShimmerEffect
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.Neutral500
import com.dakkho.android.presentation.theme.SkyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstructorContactScreen(
    onBackClick: () -> Unit,
    viewModel: InstructorContactViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val context = LocalContext.current

    AnimatedPage {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Contact ${uiState.instructorName}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            }
        ) { paddingValues ->
            when {
                uiState.isLoading -> {
                    ShimmerContact(modifier = Modifier.padding(paddingValues))
                }
                uiState.error != null && uiState.instructor == null -> {
                    EmptyState(
                        icon = painterResource(id = android.R.drawable.ic_dialog_alert),
                        title = "Could not load contact info",
                        subtitle = uiState.error,
                        onActionClick = { viewModel.loadInstructor() },
                        actionLabel = "Retry",
                        modifier = Modifier.padding(paddingValues)
                    )
                }
                uiState.instructor != null -> {
                    val instructor = uiState.instructor!!
                    val socialLinks = instructor.socialLinks
                    val hasContactInfo = !instructor.email.isNullOrBlank() ||
                            socialLinks.hasAny

                    if (!hasContactInfo) {
                        EmptyState(
                            icon = painterResource(id = android.R.drawable.ic_menu_share),
                            title = "No contact info available",
                            subtitle = "This instructor hasn't shared any contact details",
                            modifier = Modifier.padding(paddingValues)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues),
                            contentPadding = PaddingValues(
                                horizontal = DesignToken.Space.dp16,
                                vertical = DesignToken.Space.dp8
                            ),
                            verticalArrangement = Arrangement.spacedBy(DesignToken.Space.dp8)
                        ) {
                            // ── Email Section ──
                            if (!instructor.email.isNullOrBlank()) {
                                item {
                                    ContactSectionTitle(title = "Email")
                                }
                                item {
                                    ContactCard(
                                        icon = painterResource(id = android.R.drawable.ic_dialog_email),
                                        label = instructor.email,
                                        subtitle = "Send an email",
                                        onClick = {
                                            launchEmailIntent(context, instructor.email)
                                        }
                                    )
                                }
                            }

                            // ── Social Links Section ──
                            if (socialLinks.hasAny) {
                                item {
                                    Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
                                    ContactSectionTitle(title = "Social Links")
                                }

                                socialLinks.youtube?.let { url ->
                                    item {
                                        SocialLinkCard(
                                            platform = "YouTube",
                                            url = url,
                                            color = Color(0xFFFF0000),
                                            onClick = { launchBrowserIntent(context, url) }
                                        )
                                    }
                                }

                                socialLinks.github?.let { url ->
                                    item {
                                        SocialLinkCard(
                                            platform = "GitHub",
                                            url = url,
                                            color = Color(0xFF333333),
                                            onClick = { launchBrowserIntent(context, url) }
                                        )
                                    }
                                }

                                socialLinks.facebook?.let { url ->
                                    item {
                                        SocialLinkCard(
                                            platform = "Facebook",
                                            url = url,
                                            color = Color(0xFF1877F2),
                                            onClick = { launchBrowserIntent(context, url) }
                                        )
                                    }
                                }

                                socialLinks.linkedin?.let { url ->
                                    item {
                                        SocialLinkCard(
                                            platform = "LinkedIn",
                                            url = url,
                                            color = Color(0xFF0A66C2),
                                            onClick = { launchBrowserIntent(context, url) }
                                        )
                                    }
                                }

                                socialLinks.website?.let { url ->
                                    item {
                                        SocialLinkCard(
                                            platform = "Website",
                                            url = url,
                                            color = SkyBlue,
                                            onClick = { launchBrowserIntent(context, url) }
                                        )
                                    }
                                }
                            }

                            // Bottom spacer
                            item {
                                Spacer(modifier = Modifier.height(DesignToken.Space.dp24))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ContactSectionTitle(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold
        ),
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier.padding(vertical = DesignToken.Space.dp4)
    )
}

@Composable
fun ContactCard(
    icon: androidx.compose.ui.graphics.painter.Painter,
    label: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignToken.Space.dp12),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon circle
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SkyBlue.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = icon,
                    contentDescription = label,
                    tint = SkyBlue,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(DesignToken.Space.dp12))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = Neutral500
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                contentDescription = "Open",
                tint = SkyBlue,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun SocialLinkCard(
    platform: String,
    url: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignToken.Space.dp12),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Platform icon circle
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = platform.take(1).uppercase(),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = color
                )
            }

            Spacer(modifier = Modifier.width(DesignToken.Space.dp12))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = platform,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = url.take(40) + if (url.length > 40) "..." else "",
                    style = MaterialTheme.typography.labelSmall,
                    color = Neutral500,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                contentDescription = "Open",
                tint = color,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun ShimmerContact(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(DesignToken.Space.dp16)
    ) {
        repeat(4) {
            ShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(vertical = DesignToken.Space.dp4)
            )
        }
    }
}

// ── Intent Helpers ──

private fun launchEmailIntent(context: Context, email: String) {
    try {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$email")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        // Fallback: open with generic send intent
        val fallback = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(fallback)
        } catch (_: Exception) { }
    }
}

private fun launchBrowserIntent(context: Context, url: String) {
    try {
        val finalUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) {
            "https://$url"
        } else url

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(finalUrl)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (_: Exception) { }
}
