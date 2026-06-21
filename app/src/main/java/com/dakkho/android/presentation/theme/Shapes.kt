package com.dakkho.android.presentation.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val DakkhoShapes = Shapes(
    extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
    small = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
    large = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
    extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
)

object DakkhoShapeTokens {
    val small = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
    val medium = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
    val large = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
    val xl = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
    val xxl = androidx.compose.foundation.shape.RoundedCornerShape(32.dp)
    val full = CircleShape
    val none = androidx.compose.foundation.shape.RoundedCornerShape(0.dp)
}
