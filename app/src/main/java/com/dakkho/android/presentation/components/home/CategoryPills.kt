package com.dakkho.android.presentation.components.home

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.dakkho.android.domain.model.Technology
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.SkyBlue

@Composable
fun CategoryPills(
    technologies: List<Technology>,
    selectedTechnology: String?,
    onTechnologySelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = DesignToken.Space.dp16, vertical = DesignToken.Space.dp4),
        horizontalArrangement = Arrangement.spacedBy(DesignToken.Space.dp8)
    ) {
        // "All" chip
        FilterChip(
            selected = selectedTechnology == null,
            onClick = { onTechnologySelected(null) },
            label = {
                Text(
                    text = "All",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = SkyBlue,
                selectedLabelColor = Color.White,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            border = FilterChipDefaults.filterChipBorder(
                borderColor = MaterialTheme.colorScheme.outlineVariant,
                selectedBorderColor = SkyBlue,
                enabled = true,
                selected = selectedTechnology == null
            ),
            contentPadding = PaddingValues(horizontal = DesignToken.Space.dp12)
        )

        // Technology chips
        technologies.forEach { technology ->
            FilterChip(
                selected = selectedTechnology == technology.name,
                onClick = { onTechnologySelected(technology.name) },
                label = {
                    Text(
                        text = technology.name,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = SkyBlue,
                    selectedLabelColor = Color.White,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = MaterialTheme.colorScheme.outlineVariant,
                    selectedBorderColor = SkyBlue,
                    enabled = true,
                    selected = selectedTechnology == technology.name
                ),
                contentPadding = PaddingValues(horizontal = DesignToken.Space.dp12)
            )
        }
    }
}
