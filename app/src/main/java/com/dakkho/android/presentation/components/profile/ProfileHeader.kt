package com.dakkho.android.presentation.components.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.Neutral500
import com.dakkho.android.presentation.theme.SkyBlue

@Composable
fun ProfileHeader(
    name: String,
    technology: String?,
    instituteName: String?,
    avatarUrl: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar with CircleShape and SkyBlue border
        Box(
            modifier = Modifier
                .size(DesignToken.ComponentSize.avatarLarge)
                .clip(CircleShape)
                .border(
                    width = 2.dp,
                    color = SkyBlue,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (!avatarUrl.isNullOrBlank()) {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = "Profile avatar",
                    modifier = Modifier.size(DesignToken.ComponentSize.avatarLarge),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Placeholder initial letter
                Box(
                    modifier = Modifier
                        .size(DesignToken.ComponentSize.avatarLarge)
                        .background(SkyBlue.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val initial = name.firstOrNull()?.uppercase() ?: "?"
                    Text(
                        text = initial,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = SkyBlue
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(DesignToken.Space.dp12))

        // Name
        Text(
            text = name,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )

        // Technology badge
        if (!technology.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(DesignToken.Space.dp8))

            Surface(
                shape = MaterialTheme.shapes.small,
                color = SkyBlue.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, SkyBlue.copy(alpha = 0.3f))
            ) {
                Text(
                    text = technology,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = SkyBlue
                )
            }
        }

        // Institute name
        if (!instituteName.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(DesignToken.Space.dp4))

            Text(
                text = instituteName,
                style = MaterialTheme.typography.bodySmall,
                color = Neutral500
            )
        }
    }
}
