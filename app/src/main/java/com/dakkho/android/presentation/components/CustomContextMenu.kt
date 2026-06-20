package com.dakkho.android.presentation.components

import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.dakkho.android.presentation.theme.DesignToken

@Composable
fun ProtectedText(
    text: String,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    BasicText(
        text = text,
        style = style.copy(color = color),
        modifier = modifier
    )
}
