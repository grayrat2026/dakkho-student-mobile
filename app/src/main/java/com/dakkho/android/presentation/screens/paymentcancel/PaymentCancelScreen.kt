package com.dakkho.android.presentation.screens.paymentcancel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.components.GradientButton
import com.dakkho.android.presentation.theme.DesignToken

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentCancelScreen(
    onReturnToCourse: () -> Unit = {},
    onGoHome: () -> Unit = {},
    onTryAlternative: () -> Unit = {}
) {
    Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = DesignToken.Space.dp24),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Cancel,
                contentDescription = null,
                modifier = Modifier.size(96.dp),
                tint = Color(0xFFF59E0B)
            )
            Spacer(modifier = Modifier.height(DesignToken.Space.dp24))
            Text(
                text = "পেমেন্ট বাতিল",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFFF59E0B)
            )
            Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
            Text(
                text = "আপনি পেমেন্ট বাতিল করেছেন। কোনো অর্থ কাটা হয়নি।",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            // Alternative payment methods
            Spacer(modifier = Modifier.height(DesignToken.Space.dp24))
            GlassCard(modifier = Modifier.padding()) {
                Column(modifier = Modifier.padding(DesignToken.Space.dp16)) {
                    Text(text = "বিকল্প পেমেন্ট পদ্ধতি", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
                    Text(text = "• বিকাশ", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "• নগদ", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "• রকেট", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "• ব্যাংক কার্ড", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp24))
            GradientButton(text = "কোর্সে ফিরে যান", onClick = onReturnToCourse, modifier = Modifier.height(50.dp))
            Spacer(modifier = Modifier.height(DesignToken.Space.dp12))
            OutlinedButton(
                onClick = onTryAlternative,
                modifier = Modifier.height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("বিকল্প পেমেন্ট")
            }
            Spacer(modifier = Modifier.height(DesignToken.Space.dp12))
            OutlinedButton(
                onClick = onGoHome,
                modifier = Modifier.height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("হোমে যান")
            }
        }
    }
}
