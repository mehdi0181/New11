package com.example.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily

object FontUtils {

    fun getFontFamily(fontFamilyName: String): FontFamily {
        return when (fontFamilyName.lowercase()) {
            "vazir", "standard" -> FontFamily.SansSerif
            "serif" -> FontFamily.Serif
            "monospace" -> FontFamily.Monospace
            "cursive", "nastaliq" -> FontFamily.Cursive
            else -> FontFamily.Default
        }
    }

    fun parseHexColor(hexString: String, defaultColor: Color = TextDark): Color {
        return try {
            val cleanedHex = hexString.replace("#", "")
            if (cleanedHex.length == 6) {
                Color(android.graphics.Color.parseColor("#$cleanedHex"))
            } else {
                defaultColor
            }
        } catch (e: Exception) {
            defaultColor
        }
    }

    val availableFontFamilies = listOf(
        "vazir" to "وزیر (استاندارد)",
        "serif" to "سنتی (Serif)",
        "monospace" to "ماشینی (Monospace)",
        "cursive" to "دست‌نویس (Cursive)"
    )

    val availableFontColors = listOf(
        "#FFFFFF" to "سفید",
        "#0F172A" to "مشکی ذغالی",
        "#1E1B4B" to "سورمه‌ای تیره",
        "#701A75" to "ارغوانی تیره",
        "#064E3B" to "سبز یشم",
        "#991B1B" to "زرشکی تیره"
    )
}
