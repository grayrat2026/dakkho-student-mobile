package com.dakkho.android.presentation.components.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.Neutral200

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    showChevron: Boolean = true
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(
                    horizontal = DesignToken.Space.dp16,
                    vertical = DesignToken.Space.dp12
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(DesignToken.IconSize.medium)
            )

            Spacer(modifier = Modifier.width(DesignToken.Space.dp16))

            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = tint,
                modifier = Modifier.weight(1f)
            )

            if (showChevron) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = tint.copy(alpha = 0.5f),
                    modifier = Modifier.size(DesignToken.IconSize.medium)
                )
            }
        }

        HorizontalDivider(
            color = Neutral200,
            thickness = 0.5.dp,
            modifier = Modifier.padding(horizontal = DesignToken.Space.dp16)
        )
    }
}
