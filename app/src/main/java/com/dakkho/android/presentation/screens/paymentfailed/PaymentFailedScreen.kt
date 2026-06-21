package com.dakkho.android.presentation.screens.paymentfailed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dakkho.android.presentation.components.GradientButton
import com.dakkho.android.presentation.theme.DesignToken

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentFailedScreen(
    onRetry: () -> Unit = {},
    onGoHome: () -> Unit = {},
    onContactSupport: () -> Unit = {},
    viewModel: PaymentFailedViewModel = hiltViewModel()
) {
    val reason by viewModel.reason

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = DesignToken.Space.dp24),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                modifier = Modifier.size(96.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(DesignToken.Space.dp24))
            Text(
                text = "পেমেন্ট ব্যর্থ",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
            Text(
                text = reason.ifBlank { "পেমেন্ট প্রক্রিয়ায় একটি ত্রুটি হয়েছে। অনুগ্রহ করে আবার চেষ্টা করুন।" },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(DesignToken.Space.dp32))
            GradientButton(text = "আবার চেষ্টা করুন", onClick = onRetry, modifier = Modifier.height(50.dp))
            Spacer(modifier = Modifier.height(DesignToken.Space.dp12))
            GradientButton(
                text = "সাপোর্টে যোগাযোগ",
                onClick = onContactSupport,
                modifier = Modifier.height(50.dp),
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(DesignToken.Space.dp12))
            GradientButton(
                text = "হোমে যান",
                onClick = onGoHome,
                modifier = Modifier.height(50.dp),
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
