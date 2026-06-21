package com.dakkho.android.presentation.screens.certificates

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dakkho.android.domain.model.Certificate
import com.dakkho.android.presentation.components.EmptyState
import com.dakkho.android.presentation.components.ShimmerEffect
import com.dakkho.android.presentation.theme.DeepBlue
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.Green
import com.dakkho.android.presentation.theme.Neutral500

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CertificatesScreen(
    onBackClick: () -> Unit = {},
    viewModel: CertificatesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }

    // Show share success snackbar
    LaunchedEffect(uiState.showShareSuccess) {
        if (uiState.showShareSuccess) {
            snackbarHostState.showSnackbar("Certificate shared successfully!")
            viewModel.dismissShareSuccess()
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Certificates",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
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
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        when {
            uiState.isLoading && uiState.certificates.isEmpty() -> {
                CertificatesShimmerContent(modifier = Modifier.padding(innerPadding))
            }
            uiState.certificates.isEmpty() && !uiState.isLoading -> {
                EmptyState(
                    title = "No certificates yet",
                    subtitle = "Complete a course to earn your first certificate!",
                    iconRes = android.R.drawable.ic_menu_info_details,
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = DesignToken.Space.dp16),
                    verticalArrangement = Arrangement.spacedBy(DesignToken.Space.dp16)
                ) {
                    item { Spacer(modifier = Modifier.height(DesignToken.Space.dp8)) }

                    items(
                        items = uiState.certificates,
                        key = { it.id }
                    ) { certificate ->
                        CertificateCard(
                            certificate = certificate,
                            isGenerating = uiState.generatingPdfForId == certificate.id,
                            isSharing = uiState.sharingPdfForId == certificate.id,
                            onDownloadClick = { viewModel.downloadCertificate(certificate) },
                            onShareClick = { viewModel.shareCertificate(certificate) }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(DesignToken.Space.dp16)) }
                }
            }
        }
    }
}

@Composable
private fun CertificateCard(
    certificate: Certificate,
    isGenerating: Boolean,
    isSharing: Boolean,
    onDownloadClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Glassmorphism card with emerald accent for certificates
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
        ),
        border = BorderStroke(
            width = 1.dp,
            color = Green.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Top gradient accent bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Green, DeepBlue)
                        )
                    )
            )

            Column(
                modifier = Modifier.padding(DesignToken.Space.dp16)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Certificate icon
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                color = Green.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CardMembership,
                            contentDescription = "Certificate",
                            tint = Green,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(DesignToken.Space.dp12))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = certificate.courseName,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(DesignToken.Space.dp4))
                        Text(
                            text = "Completed on ${certificate.completionDate}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Neutral500
                        )
                    }
                }

                Spacer(modifier = Modifier.height(DesignToken.Space.dp12))

                // Details row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(DesignToken.Space.dp16)
                ) {
                    certificate.studentName.let { name ->
                        if (name.isNotEmpty()) {
                            Column {
                                Text(
                                    text = "Student",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Neutral500
                                )
                                Text(
                                    text = name,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }
                    }

                    certificate.instructorName?.let { instructor ->
                        Column {
                            Text(
                                text = "Instructor",
                                style = MaterialTheme.typography.labelSmall,
                                color = Neutral500
                            )
                            Text(
                                text = instructor,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }

                    certificate.grade?.let { grade ->
                        Column {
                            Text(
                                text = "Grade",
                                style = MaterialTheme.typography.labelSmall,
                                color = Neutral500
                            )
                            Text(
                                text = grade,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(DesignToken.Space.dp12))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Download button
                    IconButton(
                        onClick = onDownloadClick,
                        enabled = !isGenerating
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = Green
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Download PDF",
                                tint = Green,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(DesignToken.Space.dp8))

                    // Share button
                    IconButton(
                        onClick = onShareClick,
                        enabled = !isSharing
                    ) {
                        if (isSharing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = DeepBlue
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share Certificate",
                                tint = DeepBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CertificatesShimmerContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = DesignToken.Space.dp16),
        verticalArrangement = Arrangement.spacedBy(DesignToken.Space.dp16)
    ) {
        Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
        repeat(3) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                )
            ) {
                Column(modifier = Modifier.padding(DesignToken.Space.dp16)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ShimmerEffect(
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.width(DesignToken.Space.dp12))
                        Column(modifier = Modifier.weight(1f)) {
                            ShimmerEffect(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(16.dp)
                            )
                            Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
                            ShimmerEffect(
                                modifier = Modifier
                                    .fillMaxWidth(0.5f)
                                    .height(12.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(DesignToken.Space.dp12))
                    ShimmerEffect(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                    )
                }
            }
        }
    }
}
