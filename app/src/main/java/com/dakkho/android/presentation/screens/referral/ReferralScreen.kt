package com.dakkho.android.presentation.screens.referral

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dakkho.android.domain.model.ReferralHistoryItem
import com.dakkho.android.domain.model.ReferralStatus
import com.dakkho.android.presentation.components.EmptyState
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.components.GradientButton
import com.dakkho.android.presentation.components.ShimmerEffect
import com.dakkho.android.presentation.theme.DeepBlue
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.Neutral500
import com.dakkho.android.presentation.theme.SkyBlue
import com.dakkho.android.presentation.theme.Success
import com.dakkho.android.presentation.theme.Warning

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReferralScreen(
    onBackClick: () -> Unit,
    viewModel: ReferralViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "রেফারেল",
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
                scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(),
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                ReferralShimmer(modifier = Modifier.padding(innerPadding))
            }
            uiState.error != null && uiState.referralData == null -> {
                EmptyState(
                    title = "লোড ব্যর্থ",
                    subtitle = uiState.error ?: "",
                    actionText = "আবার চেষ্টা করুন",
                    onAction = { viewModel.loadReferralData() },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            uiState.referralData != null -> {
                val referralData = uiState.referralData!!
                ReferralContent(
                    referralData = referralData,
                    onCopyCode = { code ->
                        clipboardManager.setText(AnnotatedString(code))
                    },
                    onShareLink = { link, code ->
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "DAKKHO-তে যোগ দিন! আমার রেফারেল কোড: $code\n$link"
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "শেয়ার করুন"))
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun ReferralContent(
    referralData: com.dakkho.android.domain.model.ReferralData,
    onCopyCode: (String) -> Unit,
    onShareLink: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = DesignToken.Space.dp16)
    ) {
        // Hero card with referral code
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(DesignToken.Space.dp16))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(SkyBlue, DeepBlue)
                    )
                )
                .padding(DesignToken.Space.dp24)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.CardGiftcard,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(DesignToken.Space.dp12))
                Text(
                    text = "বন্ধুদের আমন্ত্রণ জানান",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "প্রতিটি সফল রেফারেলে ৳৫০ ক্রেডিট পান!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.85f)
                )
                Spacer(modifier = Modifier.height(DesignToken.Space.dp20))

                // Referral code box
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(DesignToken.Radius.dp12))
                        .background(Color.White.copy(alpha = 0.2f))
                        .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(DesignToken.Radius.dp12))
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = referralData.referralCode,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        ),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    IconButton(
                        onClick = { onCopyCode(referralData.referralCode) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.ContentCopy,
                            contentDescription = "কপি",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

        // Share button
        GradientButton(
            text = "শেয়ার করুন",
            onClick = { onShareLink(referralData.referralLink, referralData.referralCode) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        )

        Spacer(modifier = Modifier.height(DesignToken.Space.dp20))

        // Stats row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(DesignToken.Space.dp8)
        ) {
            ReferralStatCard(
                icon = Icons.Default.Group,
                label = "মোট রেফারেল",
                value = referralData.totalReferrals,
                modifier = Modifier.weight(1f)
            )
            ReferralStatCard(
                icon = Icons.Default.PersonAdd,
                label = "সফল",
                value = referralData.successfulReferrals,
                modifier = Modifier.weight(1f)
            )
            ReferralStatCard(
                icon = Icons.Default.Star,
                label = "অর্জিত ৳",
                value = referralData.earnedCredits.toInt(),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(DesignToken.Space.dp20))

        // Referral History
        if (referralData.referralHistory.isNotEmpty()) {
            Text(
                text = "রেফারেল ইতিহাস",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(DesignToken.Space.dp8))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                referralData.referralHistory.forEachIndexed { index, item ->
                    ReferralHistoryItemRow(item = item)
                    if (index < referralData.referralHistory.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = DesignToken.Space.dp16),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(DesignToken.Space.dp32))
    }
}

@Composable
private fun ReferralStatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: Int,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(DesignToken.Space.dp12),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = SkyBlue,
                modifier = Modifier.size(DesignToken.IconSize.medium)
            )
            Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(DesignToken.Space.dp4))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = Neutral500
            )
        }
    }
}

@Composable
private fun ReferralHistoryItemRow(item: ReferralHistoryItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DesignToken.Space.dp16, vertical = DesignToken.Space.dp12),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.referredName,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                )
            )
            Text(
                text = item.referredEmail,
                style = MaterialTheme.typography.bodySmall,
                color = Neutral500
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            // Status chip
            val (statusText, statusColor) = when (item.status) {
                ReferralStatus.PENDING -> "পেন্ডিং" to Warning
                ReferralStatus.REGISTERED -> "নিবন্ধিত" to SkyBlue
                ReferralStatus.ENROLLED -> "ভর্তি" to Success
                ReferralStatus.REWARDED -> "পুরস্কৃত" to Success
            }
            Text(
                text = statusText,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = statusColor
            )
            if (item.earnedCredits > 0) {
                Text(
                    text = "+৳${item.earnedCredits.toInt()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Success
                )
            }
        }
    }
}

@Composable
private fun ReferralShimmer(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = DesignToken.Space.dp16)
    ) {
        ShimmerEffect(modifier = Modifier.fillMaxWidth().height(200.dp))
        Spacer(modifier = Modifier.height(DesignToken.Space.dp16))
        ShimmerEffect(modifier = Modifier.fillMaxWidth().height(56.dp))
    }
}

