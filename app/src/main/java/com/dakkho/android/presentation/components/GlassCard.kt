package com.dakkho.android.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.GlassmorphismDefaults
import com.dakkho.android.presentation.theme.Neutral100
import com.dakkho.android.presentation.theme.Neutral200
import com.dakkho.android.presentation.theme.Neutral800

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val glassBackground = if (isDark) {
        Color(0xB30F172A) // rgba(15,23,42,0.7)
    } else {
        Color(0xB3FFFFFF) // rgba(255,255,255,0.7)
    }
    val glassBorder = if (isDark) {
        Color(0x1AFFFFFF) // rgba(255,255,255,0.1)
    } else {
        Color(0x1A0F172A) // rgba(15,23,42,0.1)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(GlassmorphismDefaults.CORNER_RADIUS),
        colors = CardDefaults.cardColors(
            containerColor = glassBackground
        ),
        border = BorderStroke(
            width = GlassmorphismDefaults.BORDER_WIDTH,
            color = glassBorder
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = DesignToken.Elevation.level2
        )
    ) {
        Column(
            modifier = Modifier.padding(DesignToken.Space.dp16),
            content = content
        )
    }
}
