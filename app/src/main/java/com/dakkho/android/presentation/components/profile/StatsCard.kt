package com.dakkho.android.presentation.components.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.dakkho.android.presentation.components.AnimatedCounter
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.Neutral500
import com.dakkho.android.presentation.theme.SkyBlue

@Composable
fun StatsCard(
    label: String,
    value: Int,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = SkyBlue,
                modifier = Modifier.size(DesignToken.IconSize.medium)
            )

            Spacer(modifier = Modifier.height(DesignToken.Space.dp8))

            AnimatedCounter(
                targetValue = value,
                textStyle = MaterialTheme.typography.headlineSmall.copy(
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
