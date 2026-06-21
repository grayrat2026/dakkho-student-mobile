package com.dakkho.android.presentation.screens.subscription

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dakkho.android.domain.model.SubscriptionPlan
import com.dakkho.android.domain.model.SubscriptionPlanType
import com.dakkho.android.presentation.components.EmptyState
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.components.GradientButton
import com.dakkho.android.presentation.components.ShimmerEffect
import com.dakkho.android.presentation.theme.DeepBlue
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.Error
import com.dakkho.android.presentation.theme.SkyBlue
import com.dakkho.android.presentation.theme.Success

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(
    onBackClick: () -> Unit,
    viewModel: SubscriptionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissError()
        }
    }

    if (uiState.showCancelDialog) {
        AlertDialog(
            onDismissRequest = viewModel::dismissCancelDialog,
            title = { Text("সাবস্ক্রিপশন বাতিল") },
            text = { Text("আপনি কি নিশ্চিত যে আপনি আপনার সাবস্ক্রিপশন বাতিল করতে চান? আপনি বর্তমান বিলিং পিরিয়ডের শেষ পর্যন্ত সুবিধা পাবেন।") },
            confirmButton = {
                TextButton(onClick = viewModel::cancelSubscription) {
                    Text("বাতিল করুন", color = Error)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissCancelDialog) {
                    Text("না, থাকুন")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "সাবস্ক্রিপশন",
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
                SubscriptionShimmer(modifier = Modifier.padding(innerPadding))
            }
            uiState.error != null && uiState.currentSubscription == null -> {
                EmptyState(
                    title = "লোড ব্যর্থ",
                    subtitle = uiState.error ?: "",
                    actionText = "আবার চেষ্টা করুন",
                    onAction = { viewModel.loadData() },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            else -> {
                SubscriptionContent(
                    uiState = uiState,
                    onSelectPlan = viewModel::selectPlan,
                    onSubscribe = viewModel::subscribeToPlan,
                    onCancelSubscription = viewModel::showCancelDialog,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun SubscriptionContent(
    uiState: SubscriptionUiState,
    onSelectPlan: (String) -> Unit,
    onSubscribe: () -> Unit,
    onCancelSubscription: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentSub = uiState.currentSubscription

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = DesignToken.Space.dp16)
    ) {
        // Current Plan Card
        if (currentSub != null) {
            CurrentPlanCard(
                subscription = currentSub,
                onCancelClick = onCancelSubscription
            )
            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))
        }

        // Plan selection header
        Text(
            text = "প্ল্যান নির্বাচন করুন",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(DesignToken.Space.dp12))

        // Plans horizontal scroll
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(DesignToken.Space.dp12)
        ) {
            items(uiState.plans) { plan ->
                PlanCard(
                    plan = plan,
                    isSelected = uiState.selectedPlanId == plan.id,
                    isCurrentPlan = currentSub?.planType == plan.planType,
                    onSelect = { onSelectPlan(plan.id) }
                )
            }
        }

        Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

        // Subscribe button
        if (uiState.selectedPlanId != null) {
            GradientButton(
                onClick = onSubscribe,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !uiState.isSubscribing
            ) {
                if (uiState.isSubscribing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "সাবস্ক্রাইব করুন",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(DesignToken.Space.dp24))

        // Payment History
        if (uiState.paymentHistory.isNotEmpty()) {
            Text(
                text = "পেমেন্ট ইতিহাস",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(DesignToken.Space.dp8))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                uiState.paymentHistory.forEach { payment ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = DesignToken.Space.dp16, vertical = DesignToken.Space.dp12),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = payment.planName,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            Text(
                                text = payment.paidAt ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                        Text(
                            text = "৳${payment.amount.toInt()}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = if (payment.status == "paid") Success else Error
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(DesignToken.Space.dp32))
    }
}

@Composable
private fun CurrentPlanCard(
    subscription: com.dakkho.android.domain.model.Subscription,
    onCancelClick: () -> Unit
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(DesignToken.Radius.dp16))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(SkyBlue, DeepBlue)
                    )
                )
                .padding(DesignToken.Space.dp20)
        ) {
            Column {
                Text(
                    text = "বর্তমান প্ল্যান",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subscription.planName,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )
                if (subscription.daysRemaining > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${subscription.daysRemaining} দিন বাকি",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
                if (subscription.autoRenew) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "স্বয়ংক্রিয় নবায়ন চালু",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }

        // Cancel button (only for paid plans)
        if (subscription.planType != SubscriptionPlanType.FREE && subscription.autoRenew) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = DesignToken.Space.dp16, vertical = DesignToken.Space.dp8),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onCancelClick,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Error
                    )
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("সাবস্ক্রিপশন বাতিল")
                }
            }
        }
    }
}

@Composable
private fun PlanCard(
    plan: SubscriptionPlan,
    isSelected: Boolean,
    isCurrentPlan: Boolean,
    onSelect: () -> Unit
) {
    val borderColor = when {
        isSelected -> SkyBlue
        plan.isPopular -> SkyBlue.copy(alpha = 0.5f)
        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
    }

    val cardWidth = 180.dp

    GlassCard(
        modifier = Modifier
            .width(cardWidth)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(DesignToken.Radius.dp16)
            )
    ) {
        Column(
            modifier = Modifier
                .padding(DesignToken.Space.dp16)
                .then(
                    if (!isCurrentPlan) Modifier else Modifier
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Popular badge
            if (plan.isPopular) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SkyBlue.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = SkyBlue,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "জনপ্রিয়",
                        style = MaterialTheme.typography.labelSmall,
                        color = SkyBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(
                text = plan.name,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Price
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = if (plan.price == 0.0) "বিনামূল্যে" else "৳${plan.price.toInt()}",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = SkyBlue
                )
                if (plan.price > 0) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "/${plan.billingCycle}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            // Discount
            if (plan.discountPercent > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${plan.discountPercent}% ছাড়!",
                    style = MaterialTheme.typography.labelMedium,
                    color = Success,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp12))

            // Features
            plan.features.forEach { feature ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = Success,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = feature,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp12))

            if (isCurrentPlan) {
                Text(
                    text = "বর্তমান প্ল্যান",
                    style = MaterialTheme.typography.labelMedium,
                    color = Success,
                    fontWeight = FontWeight.Bold
                )
            } else {
                GradientButton(
                    onClick = onSelect,
                    modifier = Modifier.fillMaxWidth().height(40.dp)
                ) {
                    Text(
                        text = "নির্বাচন করুন",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
private fun SubscriptionShimmer(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = DesignToken.Space.dp16)
    ) {
        ShimmerEffect(modifier = Modifier.fillMaxWidth().height(120.dp))
        Spacer(modifier = Modifier.height(DesignToken.Space.dp16))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(3) {
                ShimmerEffect(modifier = Modifier.width(180.dp).height(250.dp))
            }
        }
    }
}
