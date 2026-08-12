package com.example.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import com.example.ui.components.ScenicGlassContainer
import com.example.ui.theme.THEME_DARK_GOLD
import com.example.ui.theme.THEME_IPHONE_GLASS
import com.example.ui.theme.THEME_LIGHT_CYAN
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.components.CoinBalanceHeaderBadge
import com.example.ui.components.WhiteBorderCard
import com.example.viewmodel.AppViewModel

@Composable
fun HomeScreen(
    viewModel: AppViewModel,
    onNavigateToContent: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToQuizGames: () -> Unit = {},
    onNavigateToAiAssistant: () -> Unit = {}
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val userSettings by viewModel.userSettings.collectAsStateWithLifecycle()
    val activeTheme = userSettings.themeMode
    val isDark = activeTheme != THEME_LIGHT_CYAN

    var showExitDialog by remember { mutableStateOf(false) }

    ScenicGlassContainer(
        themeMode = activeTheme
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Action Bar: Theme Switcher Toggle (Cycles through 3 Themes)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = {
                                val nextMode = when (activeTheme) {
                                    THEME_LIGHT_CYAN -> THEME_DARK_GOLD
                                    THEME_DARK_GOLD -> THEME_IPHONE_GLASS
                                    else -> THEME_LIGHT_CYAN
                                }
                                viewModel.updateThemeMode(nextMode)
                                val msg = when (nextMode) {
                                    THEME_LIGHT_CYAN -> "تم سفید و آبی فیروزه‌ای فعال شد 🩵"
                                    THEME_DARK_GOLD -> "تم مشکی و طلایی فعال شد 🌙"
                                    else -> "تم شیشه‌ای آیفون فعال شد 📱✨"
                                }
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                                .testTag("home_theme_toggle")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = "تغییر تم",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            val themeLabel = when (activeTheme) {
                                THEME_LIGHT_CYAN -> "سفید و آبی 🩵"
                                THEME_DARK_GOLD -> "مشکی و طلایی 🌙"
                                else -> "آیفون شیشه‌ای 📱"
                            }
                            Text(
                                text = themeLabel,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }


                Spacer(modifier = Modifier.height(12.dp))

                // Header & Title
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // App Logo Display
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF38BDF8),
                                        Color(0xFF0284C7)
                                    )
                                )
                            )
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(4.dp)
                            .clip(CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.app_icon_fg),
                            contentDescription = "لوگوی برنامه دنیای سرگرمی",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "دنیای سرگرمی",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 34.sp,
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.6f),
                                offset = Offset(2f, 4f),
                                blurRadius = 8f
                            )
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "مجموعه‌ای خنده‌دار، سرگرم‌کننده و علمی با داستان‌ها و دانستنی‌های جذاب",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 15.sp,
                            lineHeight = 22.sp,
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.4f),
                                offset = Offset(1f, 2f),
                                blurRadius = 4f
                            )
                        ),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Menu Options Cards
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Option 0: AI Assistant
                    MenuOptionCard(
                        title = "✨ دستیار و داستان‌ساز هوشمند (AI)",
                        subtitle = "خلق داستان اختصاصی با هوش مصنوعی و چت با شهرزاد",
                        icon = Icons.Default.AutoAwesome,
                        isDark = isDark,
                        isGlassMode = true,
                        containerColor = if (isDark) Color(0xFF581C87).copy(alpha = 0.55f) else Color(0xFFF3E8FF).copy(alpha = 0.85f),
                        titleColor = if (isDark) Color(0xFFF3E8FF) else Color(0xFF581C87),
                        subtitleColor = if (isDark) Color(0xFFE9D5FF) else Color(0xFF6B21A8),
                        iconBgColor = Color(0xFF9333EA),
                        iconTint = Color.White,
                        borderColor = Color(0xFFC084FC),
                        testTag = "ai_assistant_button",
                        onClick = onNavigateToAiAssistant
                    )

                    // Option 1: ورود به برنامه
                    MenuOptionCard(
                        title = "ورود به برنامه",
                        subtitle = "مشاهده جک‌ها، چیستان‌ها، داستان‌ها و دانستنی‌ها",
                        icon = Icons.Default.MenuBook,
                        isDark = isDark,
                        isGlassMode = true,
                        containerColor = if (isDark) Color(0xFF0F172A).copy(alpha = 0.45f) else Color.White.copy(alpha = 0.28f),
                        titleColor = if (isDark) Color.White else Color(0xFF0F172A),
                        subtitleColor = if (isDark) Color(0xFFE2E8F0) else Color(0xFF1E293B),
                        iconBgColor = if (isDark) Color(0xFF0284C7).copy(alpha = 0.3f) else Color.White.copy(alpha = 0.5f),
                        iconTint = Color(0xFF38BDF8),
                        borderColor = Color.White.copy(alpha = 0.8f),
                        testTag = "enter_app_button",
                        onClick = onNavigateToContent
                    )

                    // Option 2: تنظیمات
                    MenuOptionCard(
                        title = "تنظیمات",
                        subtitle = "تغییر سایز فونت، نوع فونت، رنگ، تم و حالت شیشه‌ای",
                        icon = Icons.Default.Settings,
                        isDark = isDark,
                        isGlassMode = true,
                        containerColor = if (isDark) Color(0xFF0F172A).copy(alpha = 0.45f) else Color.White.copy(alpha = 0.28f),
                        titleColor = if (isDark) Color.White else Color(0xFF0F172A),
                        subtitleColor = if (isDark) Color(0xFFE2E8F0) else Color(0xFF1E293B),
                        iconBgColor = if (isDark) Color(0xFF0369A1).copy(alpha = 0.3f) else Color.White.copy(alpha = 0.5f),
                        iconTint = Color(0xFF38BDF8),
                        borderColor = Color.White.copy(alpha = 0.8f),
                        testTag = "settings_button",
                        onClick = onNavigateToSettings
                    )

                    // Option 3: درباره برنامه
                    MenuOptionCard(
                        title = "درباره برنامه",
                        subtitle = "شناسنامه برنامه و راه ارتباطی با سازنده",
                        icon = Icons.Default.Info,
                        isDark = isDark,
                        isGlassMode = true,
                        containerColor = if (isDark) Color(0xFF0F172A).copy(alpha = 0.45f) else Color.White.copy(alpha = 0.28f),
                        titleColor = if (isDark) Color.White else Color(0xFF0F172A),
                        subtitleColor = if (isDark) Color(0xFFE2E8F0) else Color(0xFF1E293B),
                        iconBgColor = if (isDark) Color(0xFF475569).copy(alpha = 0.3f) else Color.White.copy(alpha = 0.5f),
                        iconTint = Color.White,
                        borderColor = Color.White.copy(alpha = 0.8f),
                        testTag = "about_button",
                        onClick = onNavigateToAbout
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Option 4: دکمه گرد خروج از برنامه
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFFEF4444),
                                        Color(0xFF991B1B)
                                    )
                                )
                            )
                            .border(
                                width = 2.dp,
                                color = Color.White.copy(alpha = 0.85f),
                                shape = CircleShape
                            )
                            .clickable { showExitDialog = true }
                            .testTag("exit_app_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "خروج از برنامه",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "خروج از برنامه",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Exit Dialog Confirmation
            if (showExitDialog) {
                AlertDialog(
                    onDismissRequest = { showExitDialog = false },
                    title = {
                        Text(
                            text = "خروج از برنامه",
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else Color(0xFF0F172A)
                        )
                    },
                    text = {
                        Text(
                            text = "آیا مطمئن هستید که می‌خواهید از برنامه خارج شوید؟",
                            color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569)
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showExitDialog = false
                                activity?.finish()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFDC2626)
                            )
                        ) {
                            Text("خروج", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showExitDialog = false }) {
                            Text("انصراف", color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B))
                        }
                    },
                    containerColor = if (isDark) Color(0xFF1E293B) else Color.White,
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }
    }
}

@Composable
private fun MenuOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isDark: Boolean,
    isGlassMode: Boolean = false,
    containerColor: Color,
    titleColor: Color,
    subtitleColor: Color,
    iconBgColor: Color,
    iconTint: Color,
    borderColor: Color,
    testTag: String,
    onClick: () -> Unit
) {
    WhiteBorderCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag),
        onClick = onClick,
        containerColor = containerColor,
        borderColor = borderColor,
        borderWidth = 2.dp,
        isGlassMode = isGlassMode
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(iconBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconTint,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = titleColor
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = subtitleColor
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "ورود",
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
