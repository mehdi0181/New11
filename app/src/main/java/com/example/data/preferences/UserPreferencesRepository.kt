package com.example.data.preferences

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserSettings(
    val fontSizeSp: Float = 18f,
    val fontFamily: String = "vazir", // "vazir", "sans", "serif", "monospace", "nastaliq"
    val fontColorHex: String = "", // empty = default for selected theme
    val themeMode: String = "IPHONE_GLASS", // "LIGHT_CYAN", "DARK_GOLD", "IPHONE_GLASS"
    val isGlassMode: Boolean = true,
    val coins: Int = 0,
    val lastDailyClaimTime: Long = 0L
)


class UserPreferencesRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("app_settings_prefs", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<UserSettings> = _settings.asStateFlow()

    private fun loadSettings(): UserSettings {
        var themeMode = prefs.getString("theme_mode", "IPHONE_GLASS") ?: "IPHONE_GLASS"
        if (themeMode == "LIGHT") themeMode = "LIGHT_CYAN"
        if (themeMode == "DARK") themeMode = "DARK_GOLD"

        return UserSettings(
            fontSizeSp = prefs.getFloat("font_size", 18f),
            fontFamily = prefs.getString("font_family", "vazir") ?: "vazir",
            fontColorHex = prefs.getString("font_color", "") ?: "",
            themeMode = themeMode,
            isGlassMode = prefs.getBoolean("glass_mode", true)
        )
    }

    fun updateFontSize(size: Float) {
        prefs.edit().putFloat("font_size", size).apply()
        _settings.value = _settings.value.copy(fontSizeSp = size)
    }

    fun updateFontFamily(family: String) {
        prefs.edit().putString("font_family", family).apply()
        _settings.value = _settings.value.copy(fontFamily = family)
    }

    fun updateFontColor(hex: String) {
        prefs.edit().putString("font_color", hex).apply()
        _settings.value = _settings.value.copy(fontColorHex = hex)
    }

    fun updateThemeMode(mode: String) {
        prefs.edit().putString("theme_mode", mode).apply()
        _settings.value = _settings.value.copy(themeMode = mode)
    }

    fun updateGlassMode(enabled: Boolean) {
        prefs.edit().putBoolean("glass_mode", enabled).apply()
        _settings.value = _settings.value.copy(isGlassMode = enabled)
    }

    fun resetToDefaults() {
        prefs.edit().clear().apply()
        _settings.value = UserSettings()
    }
}

