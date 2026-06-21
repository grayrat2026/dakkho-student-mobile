package com.dakkho.android.presentation.screens.maintenance

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dakkho.android.domain.model.MaintenanceInfo
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.SkyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceScreen(
    info: MaintenanceInfo = MaintenanceInfo()
) {
    // Wrench rotation animation
    val infiniteTransition = rememberInfiniteTransition(label = "wrench")
    val rotation by infiniteTransition.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = DesignToken.Space.dp24),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Build,
                contentDescription = null,
                modifier = Modifier.size(96.dp).rotate(rotation),
                tint = SkyBlue
            )
            Spacer(modifier = Modifier.height(DesignToken.Space.dp24))

            Text(
                text = info.title.ifBlank { "রক্ষণাবেক্ষণ চলছে" },
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(DesignToken.Space.dp8))

            Text(
                text = info.message.ifBlank { "আমরা সিস্টেম উন্নত করছি। অনুগ্রহ করে কিছুক্ষণ পর আবার চেষ্টা করুন।" },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            // Estimated return time
            if (info.estimatedReturn.isNotBlank()) {
                Spacer(modifier = Modifier.height(DesignToken.Space.dp16))
                GlassCard {
                    Column(
                        modifier = Modifier.padding(DesignToken.Space.dp16),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "আনুমানিক ফিরে আসার সময়", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = info.estimatedReturn, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = SkyBlue)
                    }
                }
            }

            // Progress bar
            if (info.showProgress) {
                Spacer(modifier = Modifier.height(DesignToken.Space.dp24))
                LinearProgressIndicator(
                    progress = { info.progressPercent / 100f },
                    modifier = Modifier.fillMaxWidth().height(8.dp),
                    color = SkyBlue,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeCap = StrokeCap.Round
                )
                Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
                Text(text = "${info.progressPercent}% সম্পন্ন", style = MaterialTheme.typography.labelMedium, color = SkyBlue)
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp24))
            Text(
                text = "স্বয়ংক্রিয়ভাবে পুনরায় চেষ্টা হবে...",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}
