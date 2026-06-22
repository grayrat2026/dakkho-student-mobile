package com.dakkho.android.presentation.screens.paymentsuccess

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dakkho.android.domain.model.PaymentReceipt
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.components.GradientButton
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.SkyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentSuccessScreen(
    onGoToCourse: (String) -> Unit = {},
    onGoHome: () -> Unit = {},
    viewModel: PaymentSuccessViewModel = hiltViewModel()
) {
    val receipt by viewModel.receipt.collectAsState()

    // Success animation
    val infiniteTransition = rememberInfiniteTransition(label = "success")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = DesignToken.Space.dp24),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated success icon
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(96.dp).scale(scale),
                tint = Color(0xFF22C55E)
            )
            Spacer(modifier = Modifier.height(DesignToken.Space.dp24))

            Text(
                text = "পেমেন্ট সফল!",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF22C55E)
            )
            Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
            Text(
                text = "আপনার পেমেন্ট সফলভাবে সম্পন্ন হয়েছে",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            // Receipt details
            receipt?.let { r ->
                Spacer(modifier = Modifier.height(DesignToken.Space.dp24))
                GlassCard(modifier = Modifier.padding()) {
                    Column(modifier = Modifier.padding(DesignToken.Space.dp16)) {
                        ReceiptRow(label = "অর্ডার আইডি", value = r.orderId)
                        ReceiptRow(label = "কোর্স", value = r.courseTitle)
                        ReceiptRow(label = "পরিমাণ", value = "৳${"%.2f".format(r.amount)}")
                        if (r.paymentMethod.isNotBlank()) ReceiptRow(label = "পেমেন্ট মাধ্যম", value = r.paymentMethod)
                        if (r.transactionId.isNotBlank()) ReceiptRow(label = "লেনদেন আইডি", value = r.transactionId)
                        ReceiptRow(label = "সময়", value = r.paidAt)
                    }
                }
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp32))

            // CTA
            receipt?.courseId?.let { courseId ->
                GradientButton(
                    text = "কোর্সে যান",
                    onClick = { onGoToCourse(courseId) },
                    modifier = Modifier.height(50.dp)
                )
            }
            Spacer(modifier = Modifier.height(DesignToken.Space.dp12))
            OutlinedButton(
                onClick = onGoHome,
                modifier = Modifier.height(50.dp)
            ) {
                Text(text = "হোমে যান")
            }
        }
    }
}

@Composable
private fun ReceiptRow(label: String, value: String) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Text(text = value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium), modifier = Modifier.weight(1f), textAlign = TextAlign.End)
    }
}
