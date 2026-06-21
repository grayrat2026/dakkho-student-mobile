package com.dakkho.android.presentation.components.explore

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dakkho.android.domain.model.PriceType
import com.dakkho.android.domain.model.Technology

@Composable
fun FilterChipsRow(
    technologies: List<Technology>,
    selectedTechnology: String?,
    selectedLevel: String?,
    selectedPriceType: PriceType,
    onTechnologySelected: (String?) -> Unit,
    onLevelSelected: (String?) -> Unit,
    onPriceTypeSelected: (PriceType) -> Unit,
    modifier: Modifier = Modifier
) {
    val levels = listOf("beginner", "intermediate", "advanced")
    val priceTypes = PriceType.entries

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Technology chips
        technologies.forEach { tech ->
            FilterChip(
                selected = selectedTechnology == tech.name,
                onClick = {
                    onTechnologySelected(
                        if (selectedTechnology == tech.name) null else tech.name
                    )
                },
                label = { Text(text = tech.name, style = MaterialTheme.typography.labelMedium) },
                leadingIcon = if (selectedTechnology == tech.name) {
                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }

        // Level chips
        levels.forEach { level ->
            FilterChip(
                selected = selectedLevel == level,
                onClick = {
                    onLevelSelected(
                        if (selectedLevel == level) null else level
                    )
                },
                label = {
                    Text(
                        text = level.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                leadingIcon = if (selectedLevel == level) {
                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
        }

        // Price type chips
        priceTypes.forEach { priceType ->
            FilterChip(
                selected = selectedPriceType == priceType,
                onClick = { onPriceTypeSelected(priceType) },
                label = { Text(text = priceType.label, style = MaterialTheme.typography.labelMedium) },
                leadingIcon = if (selectedPriceType == priceType && priceType != PriceType.ALL) {
                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onTertiaryContainer
                )
            )
        }
    }
}
