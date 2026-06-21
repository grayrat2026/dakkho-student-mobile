package com.dakkho.android.presentation.screens.reportissue

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dakkho.android.domain.model.HelpSupportModels.BugCategory
import com.dakkho.android.domain.model.HelpSupportModels.BugSeverity
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.Green
import com.dakkho.android.presentation.theme.SkyBlue

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ReportIssueScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: ReportIssueViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    val snackbarHostState = remember { SnackbarHostState() }

    val pickMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        viewModel.selectScreenshot(uri)
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    LaunchedEffect(uiState.isSubmitted) {
        if (uiState.isSubmitted) {
            snackbarHostState.showSnackbar("রিপোর্ট সফলভাবে জমা হয়েছে!")
        }
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "সমস্যা রিপোর্ট করুন",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "ফিরে যান"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (uiState.isSubmitted) {
            SubmittedSuccessView(
                onReportAnother = { viewModel.resetForm() },
                onNavigateBack = onNavigateBack,
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = DesignToken.Spacing.md)
            ) {
                Spacer(modifier = Modifier.height(DesignToken.Spacing.sm))

                // Category Dropdown
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(DesignToken.Spacing.md)) {
                        Text(
                            text = "সমস্যার ধরন",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(DesignToken.Spacing.sm))

                        ExposedDropdownMenuBox(
                            expanded = uiState.categoryDropdownExpanded,
                            onExpandedChange = { viewModel.toggleCategoryDropdown() }
                        ) {
                            OutlinedTextField(
                                value = categoryLabel(uiState.category),
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = uiState.categoryDropdownExpanded
                                    )
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SkyBlue,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                ),
                                shape = RoundedCornerShape(DesignToken.Spacing.sm)
                            )

                            ExposedDropdownMenu(
                                expanded = uiState.categoryDropdownExpanded,
                                onDismissRequest = { viewModel.dismissCategoryDropdown() }
                            ) {
                                BugCategory.entries.forEach { category ->
                                    DropdownMenuItem(
                                        text = { Text(categoryLabel(category)) },
                                        onClick = { viewModel.selectCategory(category) }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(DesignToken.Spacing.md))

                // Severity Selector
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(DesignToken.Spacing.md)) {
                        Text(
                            text = "গুরুত্বের মাত্রা",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(DesignToken.Spacing.sm))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(DesignToken.Spacing.sm),
                            verticalArrangement = Arrangement.spacedBy(DesignToken.Spacing.sm)
                        ) {
                            BugSeverity.entries.forEach { severity ->
                                SeverityChip(
                                    severity = severity,
                                    isSelected = uiState.severity == severity,
                                    onSelect = { viewModel.selectSeverity(severity) }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(DesignToken.Spacing.md))

                // Description
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(DesignToken.Spacing.md)) {
                        Text(
                            text = "বিবরণ",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(DesignToken.Spacing.sm))

                        OutlinedTextField(
                            value = uiState.description,
                            onValueChange = { viewModel.updateDescription(it) },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 5,
                            maxLines = 10,
                            placeholder = {
                                Text("সমস্যাটি বিস্তারিত বর্ণনা করুন...")
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SkyBlue,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            ),
                            shape = RoundedCornerShape(DesignToken.Spacing.sm)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(DesignToken.Spacing.md))

                // Screenshot Section
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(DesignToken.Spacing.md)) {
                        Text(
                            text = "স্ক্রিনশট",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(DesignToken.Spacing.sm))

                        if (uiState.screenshotUri != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(DesignToken.Spacing.sm))
                                    .border(
                                        width = 1.dp,
                                        color = SkyBlue.copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(DesignToken.Spacing.sm)
                                    )
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(uiState.screenshotUri)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "স্ক্রিনশট প্রিভিউ",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(DesignToken.Spacing.sm)),
                                    contentScale = ContentScale.Crop
                                )
                                IconButton(
                                    onClick = { viewModel.removeScreenshot() },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(DesignToken.Spacing.xs)
                                        .size(32.dp)
                                        .background(
                                            color = Color.Black.copy(alpha = 0.6f),
                                            shape = CircleShape
                                        )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "সরিয়ে দিন",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(DesignToken.Spacing.sm))
                        }

                        OutlinedButton(
                            onClick = {
                                pickMediaLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(DesignToken.Spacing.sm),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = SkyBlue
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(DesignToken.Spacing.xs))
                            Text(if (uiState.screenshotUri != null) "স্ক্রিনশট পরিবর্তন করুন" else "স্ক্রিনশট যোগ করুন")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(DesignToken.Spacing.md))

                // Log Collector Section
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(DesignToken.Spacing.md)) {
                        Text(
                            text = "লগ সংগ্রহ",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(DesignToken.Spacing.xs))
                        Text(
                            text = "অ্যাপের সাম্প্রতিক লগ সংগ্রহ করে রিপোর্টে যুক্ত করুন",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(DesignToken.Spacing.sm))

                        AnimatedVisibility(
                            visible = uiState.collectedLogs != null,
                            enter = fadeIn() + slideInVertically(),
                            exit = fadeOut()
                        ) {
                            if (uiState.collectedLogs != null) {
                                Column {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(120.dp)
                                            .clip(RoundedCornerShape(DesignToken.Spacing.sm))
                                            .background(Color(0xFF1A1A2E))
                                            .border(
                                                width = 1.dp,
                                                color = SkyBlue.copy(alpha = 0.3f),
                                                shape = RoundedCornerShape(DesignToken.Spacing.sm)
                                            )
                                            .padding(DesignToken.Spacing.sm)
                                    ) {
                                        Text(
                                            text = uiState.collectedLogs!!.take(500) + "...",
                                            color = Color(0xFF00FF88),
                                            fontSize = 10.sp,
                                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                            modifier = Modifier.verticalScroll(rememberScrollState())
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(DesignToken.Spacing.xs))
                                    OutlinedButton(
                                        onClick = { viewModel.removeLogs() },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(DesignToken.Spacing.sm),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = MaterialTheme.colorScheme.error
                                        )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(DesignToken.Spacing.xs))
                                        Text("লগ সরিয়ে দিন")
                                    }
                                    Spacer(modifier = Modifier.height(DesignToken.Spacing.sm))
                                }
                            }
                        }

                        OutlinedButton(
                            onClick = { viewModel.collectLogs() },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(DesignToken.Spacing.sm),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = SkyBlue
                            ),
                            enabled = !uiState.isCollectingLogs
                        ) {
                            Icon(
                                imageVector = Icons.Default.Terminal,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(DesignToken.Spacing.xs))
                            Text(if (uiState.isCollectingLogs) "লগ সংগ্রহ হচ্ছে..." else "লগ সংগ্রহ করুন")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(DesignToken.Spacing.md))

                // Device Info Card
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(DesignToken.Spacing.md)) {
                        Text(
                            text = "ডিভাইস তথ্য",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(DesignToken.Spacing.sm))

                        DeviceInfoRow(label = "ডিভাইস", value = uiState.deviceModel)
                        DeviceInfoRow(label = "ওএস সংস্করণ", value = "Android ${uiState.osVersion}")
                        DeviceInfoRow(label = "অ্যাপ সংস্করণ", value = uiState.appVersion)
                    }
                }

                Spacer(modifier = Modifier.height(DesignToken.Spacing.lg))

                // Submit Button
                GradientButton(
                    text = if (uiState.isSubmitting) "জমা হচ্ছে..." else "রিপোর্ট জমা দিন",
                    onClick = { viewModel.submitBugReport() },
                    enabled = !uiState.isSubmitting && uiState.description.isNotBlank()
                )

                Spacer(modifier = Modifier.height(DesignToken.Spacing.xl))
            }
        }
    }
}

@Composable
private fun SeverityChip(
    severity: BugSeverity,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        severityColor(severity).copy(alpha = 0.2f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val borderColor = if (isSelected) {
        severityColor(severity)
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    }
    val textColor = if (isSelected) {
        severityColor(severity)
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(DesignToken.Spacing.sm))
            .background(backgroundColor)
            .border(
                width = 1.5.dp,
                color = borderColor,
                shape = RoundedCornerShape(DesignToken.Spacing.sm)
            )
            .clickable { onSelect() }
            .padding(horizontal = DesignToken.Spacing.md, vertical = DesignToken.Spacing.sm)
    ) {
        Text(
            text = severityLabel(severity),
            color = textColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun DeviceInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = DesignToken.Spacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun GradientButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    val gradientBrush = Brush.horizontalGradient(
        colors = if (enabled) listOf(SkyBlue, Green) else listOf(
            MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(DesignToken.Spacing.md))
            .background(gradientBrush)
            .then(
                if (enabled) Modifier.clickable { onClick() }
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (enabled) Color.White else Color.White.copy(alpha = 0.5f),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}

@Composable
private fun SubmittedSuccessView(
    onReportAnother: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = DesignToken.Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Green
        )
        Spacer(modifier = Modifier.height(DesignToken.Spacing.md))
        Text(
            text = "ধন্যবাদ!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(DesignToken.Spacing.sm))
        Text(
            text = "আপনার রিপোর্ট সফলভাবে জমা হয়েছে। আমরা শীঘ্রই এটি পর্যালোচনা করব।",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(DesignToken.Spacing.xl))

        GradientButton(
            text = "আরেকটি রিপোর্ট করুন",
            onClick = onReportAnother
        )
        Spacer(modifier = Modifier.height(DesignToken.Spacing.sm))

        OutlinedButton(
            onClick = onNavigateBack,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(DesignToken.Spacing.md),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = SkyBlue)
        ) {
            Text("ফিরে যান")
        }
    }
}

private fun categoryLabel(category: BugCategory): String = when (category) {
    BugCategory.APP_CRASH -> "অ্যাপ ক্র্যাশ"
    BugCategory.VIDEO_PLAYBACK -> "ভিডিও প্লেব্যাক"
    BugCategory.DOWNLOAD_ISSUE -> "ডাউনলোড সমস্যা"
    BugCategory.PAYMENT_ISSUE -> "পেমেন্ট সমস্যা"
    BugCategory.LOGIN_ISSUE -> "লগইন সমস্যা"
    BugCategory.CONTENT_ISSUE -> "কন্টেন্ট সমস্যা"
    BugCategory.OTHER -> "অন্যান্য"
}

private fun severityLabel(severity: BugSeverity): String = when (severity) {
    BugSeverity.MINOR -> "সামান্য"
    BugSeverity.MODERATE -> "মধ্যম"
    BugSeverity.MAJOR -> "গুরুতর"
    BugSeverity.CRITICAL -> "মারাত্মক"
}

private fun severityColor(severity: BugSeverity): Color = when (severity) {
    BugSeverity.MINOR -> Color(0xFF4CAF50)
    BugSeverity.MODERATE -> Color(0xFFFF9800)
    BugSeverity.MAJOR -> Color(0xFFFF5722)
    BugSeverity.CRITICAL -> Color(0xFFD32F2F)
}
