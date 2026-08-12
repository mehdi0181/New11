package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.FormatColorFill
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.ScenicGlassContainer
import com.example.ui.components.WhiteBorderCard
import com.example.ui.theme.FontUtils
import com.example.ui.theme.THEME_DARK_GOLD
import com.example.ui.theme.THEME_IPHONE_GLASS
import com.example.ui.theme.THEME_LIGHT_CYAN
import com.example.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit
) {
    val userSettings by viewModel.userSettings.collectAsStateWithLifecycle()
    val activeTheme = userSettings.themeMode

    ScenicGlassContainer(
        themeMode = activeTheme
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "تنظیمات برنامه",
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("settings_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "بازگشت",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Live Text & Theme Preview Card
                WhiteBorderCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "پیش‌نمایش زنده تم و قلم",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )

                            val themeLabel = when (activeTheme) {
                                THEME_LIGHT_CYAN -> "سفید و آبی"
                                THEME_DARK_GOLD -> "مشکی و طلایی"
                                else -> "آیفون شیشه‌ای"
                            }
                            Text(
                                text = "تم: $themeLabel",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val textColor = if (userSettings.fontColorHex.isNotEmpty()) {
                            FontUtils.parseHexColor(userSettings.fontColorHex, MaterialTheme.colorScheme.onSurface)
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }

                        Text(
                            text = "ملانصرالدین گفت: چطور وقتی دیگ زایمان کرد باور کردی، اما حالا که مرده باور نمی‌کنی؟!",
                            fontFamily = FontUtils.getFontFamily(userSettings.fontFamily),
                            fontSize = userSettings.fontSizeSp.sp,
                            color = textColor,
                            lineHeight = (userSettings.fontSizeSp * 1.55f).sp
                        )
                    }
                }

                // 2. Theme Selection Section (3 Distinct Theme Types)
                WhiteBorderCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "انتخاب تم و قالب تصویری (۳ مدل)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Theme Option 1: LIGHT_CYAN (کامل سفید با حاشیه آبی روشن و پسزمینه فیروزه‌ای، قلم مشکی)
                        ThemeOptionCard(
                            title = "۱. تم سفید و آبی فیروزه‌ای 🩵",
                            description = "صفحه سفید خالص، کارت‌های فیروزه‌ای روشن با حاشیه آبی و قلم مشکی",
                            isSelected = activeTheme == THEME_LIGHT_CYAN,
                            previewBg = Color(0xFFFFFFFF),
                            previewCard = Color(0xFFE0F7FA),
                            previewBorder = Color(0xFF38BDF8),
                            previewText = Color(0xFF000000),
                            onClick = { viewModel.updateThemeMode(THEME_LIGHT_CYAN) },
                            testTag = "theme_option_light_cyan"
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Theme Option 2: DARK_GOLD (مشکی با حاشیه طلایی و رنگ فونت سفید)
                        ThemeOptionCard(
                            title = "۲. تم مشکی و طلایی 🌙",
                            description = "صفحه مشکی عمیق، کارت‌های ذغالی با حاشیه طلایی متالیک و قلم سفید",
                            isSelected = activeTheme == THEME_DARK_GOLD,
                            previewBg = Color(0xFF090D16),
                            previewCard = Color(0xFF131B2A),
                            previewBorder = Color(0xFFFFD700),
                            previewText = Color(0xFFFFFFFF),
                            onClick = { viewModel.updateThemeMode(THEME_DARK_GOLD) },
                            testTag = "theme_option_dark_gold"
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Theme Option 3: IPHONE_GLASS (طراحی شیشه‌ای آیفون خلاقانه)
                        ThemeOptionCard(
                            title = "۳. طراحی شیشه‌ای آیفون (iOS Glass) 📱✨",
                            description = "گرادینت آیفون، کارت‌های شیشه‌ای مات (Frosted Glass) و حاشیه کریستالی",
                            isSelected = activeTheme == THEME_IPHONE_GLASS,
                            previewBgBrush = Brush.horizontalGradient(
                                listOf(Color(0xFF1E1B4B), Color(0xFF311B92), Color(0xFF0369A1))
                            ),
                            previewCard = Color.White.copy(alpha = 0.25f),
                            previewBorder = Color.White.copy(alpha = 0.8f),
                            previewText = Color.White,
                            onClick = { viewModel.updateThemeMode(THEME_IPHONE_GLASS) },
                            testTag = "theme_option_iphone_glass"
                        )
                    }
                }

                // 3. Font Size Slider & Presets
                WhiteBorderCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.FormatSize,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "اندازه قلم متون",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Text(
                                text = "${userSettings.fontSizeSp.toInt()} sp",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Slider(
                            value = userSettings.fontSizeSp,
                            onValueChange = { viewModel.updateFontSize(it) },
                            valueRange = 14f..32f,
                            steps = 17,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("font_size_slider")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick Size Presets
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                "کوچک" to 16f,
                                "متوسط" to 20f,
                                "بزرگ" to 24f,
                                "خیلی بزرگ" to 28f
                            ).forEach { (label, size) ->
                                val isPresetActive = userSettings.fontSizeSp == size
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (isPresetActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                        )
                                        .clickable { viewModel.updateFontSize(size) }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 12.sp,
                                        fontWeight = if (isPresetActive) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isPresetActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                // 4. Font Family Selection
                WhiteBorderCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.TextFields,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "نوع فونت (قلم نگارش)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            FontUtils.availableFontFamilies.forEach { (key, label) ->
                                val isSelected = userSettings.fontFamily == key
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                        )
                                        .clickable { viewModel.updateFontFamily(key) }
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = label,
                                        fontFamily = FontUtils.getFontFamily(key),
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                        fontSize = 15.sp
                                    )

                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 5. Font Color Override Selection
                WhiteBorderCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.FormatColorFill,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "رنگ سفارشی قلم (اختیاری)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "به صورت پیش‌فرض، رنگ قلم متناسب با تم انتخابی شما تنظیم شده است.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Reset to theme default color chip
                        val isCustomColorActive = userSettings.fontColorHex.isNotEmpty()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (!isCustomColorActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                                .clickable { viewModel.updateFontColor("") }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "✨ استفاده از رنگ هوشمند تم (توصیه شده)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (!isCustomColorActive) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                            )
                            if (!isCustomColorActive) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            FontUtils.availableFontColors.forEach { (hex, name) ->
                                val color = FontUtils.parseHexColor(hex)
                                val isSelected = userSettings.fontColorHex.equals(hex, ignoreCase = true)
                                val isWhiteColor = hex.equals("#FFFFFF", ignoreCase = true)

                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .border(
                                            width = if (isSelected) 3.dp else 1.dp,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                                            shape = CircleShape
                                        )
                                        .clickable { viewModel.updateFontColor(hex) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = name,
                                            tint = if (isWhiteColor) Color.Black else Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 6. Reset Settings Action
                WhiteBorderCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "بازنشانی تنظیمات به حالت اولیه",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "بازگرداندن اندازه قلم و تم به تنظیمات اولیه",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                            )
                        }

                        OutlinedButton(
                            onClick = { viewModel.resetSettings() },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            modifier = Modifier.testTag("reset_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("بازنشانی", fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ThemeOptionCard(
    title: String,
    description: String,
    isSelected: Boolean,
    previewBg: Color? = null,
    previewBgBrush: Brush? = null,
    previewCard: Color,
    previewBorder: Color,
    previewText: Color,
    onClick: () -> Unit,
    testTag: String
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    val borderWidth = if (isSelected) 2.5.dp else 1.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
            .border(borderWidth, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(14.dp)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Visual Theme Preview Badge
            Box(
                modifier = Modifier
                    .size(54.dp, 44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .then(
                        if (previewBgBrush != null) Modifier.background(previewBgBrush)
                        else Modifier.background(previewBg ?: Color.White)
                    )
                    .border(1.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Inner card preview
                Box(
                    modifier = Modifier
                        .size(38.dp, 28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(previewCard)
                        .border(1.dp, previewBorder, RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(previewText)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 11.5.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "انتخاب شده",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
