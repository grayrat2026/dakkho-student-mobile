package com.dakkho.android.presentation.screens.pricing

import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dakkho.android.domain.model.PricingPlan
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.components.GradientButton
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.SkyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PricingScreen(
    onBackClick: () -> Unit,
    viewModel: PricingViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val plans by viewModel.plans
    val showPlanSheet by viewModel.showPlanSheet
    val selectedPlanId by viewModel.selectedPlanId
    val context = LocalContext.current

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(text = "প্ল্যান ও মূল্য", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = DesignToken.Space.dp16)
        ) {
            Text(
                text = "আপনার শিক্ষার জন্য সেরা প্ল্যান বেছে নিন",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // Plan cards
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(DesignToken.Space.dp12)
            ) {
                items(plans) { plan ->
                    PricingPlanCard(
                        plan = plan,
                        onSelect = { viewModel.selectPlan(plan.id) }
                    )
                }
            }
        }
    }

    // Plan selection bottom sheet
    if (showPlanSheet) {
        val selectedPlan = plans.find { it.id == selectedPlanId }
        ModalBottomSheet(
            onDismissRequest = { viewModel.hidePlanSheet() },
            sheetState = rememberModalBottomSheetState()
        ) {
            selectedPlan?.let { plan ->
                Column(
                    modifier = Modifier.padding(DesignToken.Space.dp24),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = plan.name, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
                    Text(
                        text = "৳${"%.0f".format(plan.price)}/${plan.billingPeriod}",
                        style = MaterialTheme.typography.titleLarge,
                        color = SkyBlue
                    )
                    if (plan.discount > 0) {
                        Text(text = "${plan.discount}% ছাড়!", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.error)
                    }
                    Spacer(modifier = Modifier.height(DesignToken.Space.dp16))
                    plan.features.forEach { feature ->
                        Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = SkyBlue, modifier = Modifier.height(20.dp))
                            Spacer(modifier = Modifier.padding(DesignToken.Space.dp8))
                            Text(text = feature, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    Spacer(modifier = Modifier.height(DesignToken.Space.dp24))
                    GradientButton(
                        text = if (plan.isCurrent) "বর্তমান প্ল্যান" else "সাবস্ক্রাইব",
                        onClick = {
                            // Launch PipraPay via Chrome Custom Tab
                            val customTabsIntent = CustomTabsIntent.Builder().build()
                            customTabsIntent.launchUrl(context, Uri.parse("https://piprapay.com"))
                        },
                        enabled = !plan.isCurrent,
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    )
                    Spacer(modifier = Modifier.height(DesignToken.Space.dp32))
                }
            }
        }
    }
}

@Composable
private fun PricingPlanCard(
    plan: PricingPlan,
    onSelect: () -> Unit
) {
    GlassCard(
        modifier = Modifier.width(260.dp)
    ) {
        Column(
            modifier = Modifier.padding(DesignToken.Space.dp16),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (plan.isPopular) {
                Text(text = "জনপ্রিয়", style = MaterialTheme.typography.labelSmall, color = SkyBlue)
            }
            Text(text = plan.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(text = "৳${"%.0f".format(plan.price)}", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = SkyBlue)
                Text(text = "/${plan.billingPeriod}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (plan.discount > 0) {
                Text(text = "${plan.discount}% ছাড়", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
            }
            Spacer(modifier = Modifier.height(DesignToken.Space.dp12))
            plan.features.take(4).forEach { feature ->
                Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = SkyBlue, modifier = Modifier.height(16.dp))
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text(text = feature, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                }
            }
            if (plan.features.size > 4) {
                Text(text = "+${plan.features.size - 4} আরও", style = MaterialTheme.typography.labelSmall, color = SkyBlue)
            }
            Spacer(modifier = Modifier.height(DesignToken.Space.dp12))
            GradientButton(
                text = if (plan.isCurrent) "বর্তমান" else "বেছে নিন",
                onClick = onSelect,
                enabled = !plan.isCurrent,
                modifier = Modifier.height(40.dp)
            )
        }
    }
}
