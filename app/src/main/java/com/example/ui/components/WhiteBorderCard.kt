package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun WhiteBorderCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    borderColor: Color? = null,
    borderWidth: Dp = 1.5.dp,
    containerColor: Color? = null,
    isGlassMode: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    val finalContainerColor = containerColor ?: MaterialTheme.colorScheme.surface
    val finalBorderColor = borderColor ?: MaterialTheme.colorScheme.outline
    val shape = RoundedCornerShape(22.dp)

    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier,
            shape = shape,
            colors = CardDefaults.cardColors(containerColor = finalContainerColor),
            border = BorderStroke(borderWidth, finalBorderColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            content = content
        )
    } else {
        Card(
            modifier = modifier,
            shape = shape,
            colors = CardDefaults.cardColors(containerColor = finalContainerColor),
            border = BorderStroke(borderWidth, finalBorderColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            content = content
        )
    }
}

