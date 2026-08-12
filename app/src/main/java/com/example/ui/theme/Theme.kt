package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

const val THEME_LIGHT_CYAN = "LIGHT_CYAN"
const val THEME_DARK_GOLD = "DARK_GOLD"
const val THEME_IPHONE_GLASS = "IPHONE_GLASS"

// 1. LIGHT_CYAN Theme: Pure White background, Light Blue borders, Turquoise Cyan text card background, Black font
private val LightCyanColorScheme = lightColorScheme(
    primary = Color(0xFF0284C7),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB2EBF2),
    onPrimaryContainer = Color(0xFF004D40),
    secondary = Color(0xFF00ACC1),
    background = Color(0xFFFFFFFF), // Pure white
    onBackground = Color(0xFF000000), // Black font
    surface = Color(0xFFE0F7FA), // Turquoise cyan background for cards
    onSurface = Color(0xFF000000), // Black font
    surfaceVariant = Color(0xFFB2EBF2),
    onSurfaceVariant = Color(0xFF000000),
    outline = Color(0xFF38BDF8) // Light blue border
)

// 2. DARK_GOLD Theme: Deep Black background, Gold borders, White font
private val DarkGoldColorScheme = darkColorScheme(
    primary = Color(0xFFF59E0B),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF78350F),
    onPrimaryContainer = Color(0xFFFEF3C7),
    secondary = Color(0xFFFFD700),
    background = Color(0xFF090D16), // Deep Black
    onBackground = Color(0xFFFFFFFF), // Pure White font
    surface = Color(0xFF131B2A), // Dark charcoal surface
    onSurface = Color(0xFFFFFFFF), // Pure White font
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = Color(0xFFFFFFFF),
    outline = Color(0xFFFFD700) // Gold border
)

// 3. IPHONE_GLASS Theme: iOS Mesh Backdrop, Frosted Glass Cards, Crystal Glass Borders, White font
private val IphoneGlassColorScheme = darkColorScheme(
    primary = Color(0xFF38BDF8),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF818CF8).copy(alpha = 0.4f),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF38BDF8),
    background = Color(0xFF0F172A),
    onBackground = Color(0xFFFFFFFF),
    surface = Color(0xFFFFFFFF).copy(alpha = 0.18f), // Frosted glass surface
    onSurface = Color(0xFFFFFFFF), // Frosted white font
    surfaceVariant = Color(0xFFFFFFFF).copy(alpha = 0.25f),
    onSurfaceVariant = Color(0xFFFFFFFF),
    outline = Color(0xFFFFFFFF).copy(alpha = 0.55f) // Crystal white glass border
)

@Composable
fun AppTheme(
    themeMode: String = THEME_IPHONE_GLASS,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        THEME_LIGHT_CYAN -> LightCyanColorScheme
        THEME_DARK_GOLD -> DarkGoldColorScheme
        else -> IphoneGlassColorScheme
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

