package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.THEME_DARK_GOLD
import com.example.ui.theme.THEME_LIGHT_CYAN

// iPhone Glassmorphism Mesh Backdrop Brush
val IphoneGlassBackgroundBrush = Brush.verticalGradient(
    colorStops = arrayOf(
        0.00f to Color(0xFF1E1B4B), // iOS Deep Indigo Sky
        0.28f to Color(0xFF311B92), // Vivid Ambient Purple
        0.60f to Color(0xFF0F172A), // Midnight Dark Slate
        0.85f to Color(0xFF0369A1), // Soft Cyan Horizon
        1.00f to Color(0xFF1E1B4B)  // iOS Bottom Accent
    )
)

@Composable
fun ScenicGlassContainer(
    themeMode: String = "IPHONE_GLASS",
    isGlassMode: Boolean = true,
    isDark: Boolean = true,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val modifierWithBg = when (themeMode) {
        THEME_LIGHT_CYAN -> modifier.fillMaxSize().background(Color(0xFFFFFFFF))
        THEME_DARK_GOLD -> modifier.fillMaxSize().background(Color(0xFF090D16))
        else -> modifier.fillMaxSize().background(IphoneGlassBackgroundBrush)
    }

    Box(
        modifier = modifierWithBg
    ) {
        content()
    }
}


