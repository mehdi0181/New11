package com.example.ui.screens.games

import android.widget.Toast
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
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.CoinBalanceHeaderBadge
import com.example.ui.components.ScenicGlassContainer
import com.example.ui.components.WhiteBorderCard
import com.example.viewmodel.AppViewModel

import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Surface

enum class GameType {
    NONE,
    RIDDLE_QUIZ,      // ۱. مسابقه زمانی ۱ دقیقه چیستان
    WORD_GUESS,       // ۲. حدس کلمه پیشنهادی
    NAME_FAMILY,      // ۳. اسم و فامیل آنلاین
    SNAKES_LADDERS,   // ۴. مار و پله
    MENCH,            // ۵. بازی منچ
    TIC_TAC_TOE       // ۶. دوز یا XO
}

data class DifficultyOption(
    val level: String,
    val description: String,
    val color: Color,
    val tag: String
)

val GAME_DIFFICULTIES = listOf(
    DifficultyOption("سطح آسان", "مناسب برای شروع و یادگیری 🟢", Color(0xFF16A34A), "easy"),
    DifficultyOption("سطح متوسط", "چالش متعادل و استاندارد 🟡", Color(0xFFCA8A04), "medium"),
    DifficultyOption("سطح سخت", "برای بازیکنان حرفه‌ای 🔴", Color(0xFFDC2626), "hard"),
    DifficultyOption("سطح پیشرفته و خیلی سخت", "چالش واقعی برای اساتید 🟣", Color(0xFF9333EA), "advanced")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizGamesHubScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val userSettings by viewModel.userSettings.collectAsStateWithLifecycle()
    val isDark = true // Enforce Dark mode on games hub and all games for eye comfort
    val isGlassMode = userSettings.isGlassMode

    var selectedGame by remember { mutableStateOf(GameType.NONE) }
    var selectedDifficulty by remember { mutableStateOf("سطح متوسط") }
    
    var pendingGameForLevel by remember { mutableStateOf<GameType?>(null) }
    var showDifficultyDialog by remember { mutableStateOf(false) }
    
    var pendingGameToStart by remember { mutableStateOf<GameType?>(null) }
    var showInsufficientCoinsDialog by remember { mutableStateOf(false) }

    fun tryStartGame(game: GameType, level: String = selectedDifficulty) {
        if (userSettings.coins >= 50) {
            val success = viewModel.spendCoins(50)
            if (success) {
                selectedGame = game
                selectedDifficulty = level
                Toast.makeText(context, "۵۰ سکه کسر شد! بازی در $level شروع شد 🎯", Toast.LENGTH_SHORT).show()
            } else {
                showInsufficientCoinsDialog = true
            }
        } else {
            pendingGameToStart = game
            showInsufficientCoinsDialog = true
        }
    }

    if (selectedGame != GameType.NONE) {
        when (selectedGame) {
            GameType.RIDDLE_QUIZ -> {
                RiddleQuizGame(
                    viewModel = viewModel,
                    difficulty = selectedDifficulty,
                    onExit = { selectedGame = GameType.NONE }
                )
            }
            GameType.WORD_GUESS -> {
                WordGuessGame(
                    viewModel = viewModel,
                    difficulty = selectedDifficulty,
                    onExit = { selectedGame = GameType.NONE }
                )
            }
            GameType.NAME_FAMILY -> {
                NameFamilyGame(
                    viewModel = viewModel,
                    difficulty = selectedDifficulty,
                    onExit = { selectedGame = GameType.NONE }
                )
            }
            GameType.SNAKES_LADDERS -> {
                SnakesLaddersGame(
                    viewModel = viewModel,
                    difficulty = selectedDifficulty,
                    onExit = { selectedGame = GameType.NONE }
                )
            }
            GameType.MENCH -> {
                MenchGame(
                    viewModel = viewModel,
                    difficulty = selectedDifficulty,
                    onExit = { selectedGame = GameType.NONE }
                )
            }
            GameType.TIC_TAC_TOE -> {
                TicTacToeGame(
                    viewModel = viewModel,
                    difficulty = selectedDifficulty,
                    onExit = { selectedGame = GameType.NONE }
                )
            }
            else -> {}
        }
        return
    }

    ScenicGlassContainer(
        isGlassMode = isGlassMode,
        isDark = isDark
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "مسابقه آنلاین و بازی‌ها 🎮",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("quiz_hub_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "بازگشت"
                            )
                        }
                    },
                    actions = {
                        CoinBalanceHeaderBadge(
                            viewModel = viewModel,
                            coins = userSettings.coins,
                            isDark = isDark
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = if (isGlassMode) Color.White.copy(alpha = 0.22f) else Color(0xFF0F172A),
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            },
            containerColor = if (isGlassMode) Color.Transparent else Color(0xFF0F172A)
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Coin Status Bar
                WhiteBorderCard(
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = if (isDark) Color(0xFF1E293B) else Color.White,
                    borderColor = Color(0xFFEAB308),
                    borderWidth = 2.dp,
                    isGlassMode = isGlassMode
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFEF08A)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MonetizationOn,
                                        contentDescription = "سکه",
                                        tint = Color(0xFFCA8A04),
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "موجودی سکه شما",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = "${userSettings.coins} سکه",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFFEAB308)
                                    )
                                }
                            }

                            // Daily Claim Button
                            val canClaim = viewModel.canClaimDailyReward()
                            Button(
                                onClick = {
                                    if (canClaim) {
                                        viewModel.claimDailyReward()
                                        Toast.makeText(context, "🎁 ۱۵ سکه پاداش روزانه دریافت شد!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "سکه روزانه قبلاً دریافت شده است. ۲۴ ساعت بعد دوباره تلاش کنید.", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (canClaim) Color(0xFF16A34A) else Color.Gray
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("claim_daily_button")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (canClaim) "🎁 دریافت ۱۵ سکه" else "✔ روزانه گرفته شد",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF38BDF8).copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🎯 ورود به هر بازی: ۵۰ سکه",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "🏆 جایزه پیروزی: ۱۰۰ سکه",
                                fontSize = 12.sp,
                                color = Color(0xFF16A34A),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Text(
                    text = "انتخاب مسابقه یا بازی:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                )

                // 1. مسابقه زمانی یک دقیقه چیستان
                GameCardItem(
                    title = "۱. مسابقه زمانی ۱ دقیقه چیستان ⏱️",
                    subtitle = "پاسخ سریع به چیستان‌ها در ۱ دقیقه و کسب امتیاز برتر",
                    icon = Icons.Default.Psychology,
                    iconColor = Color(0xFF0284C7),
                    isGlassMode = isGlassMode,
                    onClick = {
                        pendingGameForLevel = GameType.RIDDLE_QUIZ
                        showDifficultyDialog = true
                    }
                )

                // 2. مسابقه زمانی حدس کلمه پیشنهادی
                GameCardItem(
                    title = "۲. مسابقه زمانی حدس کلمه 🔤",
                    subtitle = "حدس سریع کلمات با نشانه‌های راهنما در ۶۰ ثانیه",
                    icon = Icons.Default.Spellcheck,
                    iconColor = Color(0xFF7C3AED),
                    isGlassMode = isGlassMode,
                    onClick = {
                        pendingGameForLevel = GameType.WORD_GUESS
                        showDifficultyDialog = true
                    }
                )

                // 3. اسم و فامیل دونفره آنلاین
                GameCardItem(
                    title = "۳. اسم و فامیل دونفره آنلاین ✍️",
                    subtitle = "رقابت اسم و فامیل با حروف تصادفی جلوی حریف آنلاین",
                    icon = Icons.Default.People,
                    iconColor = Color(0xFFEA580C),
                    isGlassMode = isGlassMode,
                    onClick = {
                        pendingGameForLevel = GameType.NAME_FAMILY
                        showDifficultyDialog = true
                    }
                )

                // 4. بازی مار و پله
                GameCardItem(
                    title = "۴. بازی جذاب مار و پله 🐍🪜",
                    subtitle = "تاس بریزید، از نردبان بالا بروید و از نیش مارها فرار کنید",
                    icon = Icons.Default.EmojiEvents,
                    iconColor = Color(0xFF16A34A),
                    isGlassMode = isGlassMode,
                    onClick = {
                        pendingGameForLevel = GameType.SNAKES_LADDERS
                        showDifficultyDialog = true
                    }
                )

                // 5. بازی منچ
                GameCardItem(
                    title = "۵. بازی کلاسیک منچ 🎲",
                    subtitle = "تاس ۶ بیاورید، مهره‌های خود را وارد کنید و به مقصد برسانید",
                    icon = Icons.Default.Casino,
                    iconColor = Color(0xFFDC2626),
                    isGlassMode = isGlassMode,
                    onClick = {
                        pendingGameForLevel = GameType.MENCH
                        showDifficultyDialog = true
                    }
                )

                // 6. بازی دوز یا XO
                GameCardItem(
                    title = "۶. بازی دوز (XO) ❌⭕",
                    subtitle = "رقابت دوز ۳ در ۳ با هوش مصنوعی یا بازیکن دیگر",
                    icon = Icons.Default.GridOn,
                    iconColor = Color(0xFF0D9488),
                    isGlassMode = isGlassMode,
                    onClick = {
                        pendingGameForLevel = GameType.TIC_TAC_TOE
                        showDifficultyDialog = true
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Difficulty Level Selection Dialog
    if (showDifficultyDialog && pendingGameForLevel != null) {
        val game = pendingGameForLevel!!
        AlertDialog(
            onDismissRequest = { showDifficultyDialog = false },
            title = {
                Column {
                    Text(
                        text = "انتخاب سطح بازی 🎯",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "لطفاً سطح دشواری مورد نظر خود را برای شروع بازی انتخاب کنید:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Normal
                    )
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                ) {
                    GAME_DIFFICULTIES.forEach { option ->
                        Surface(
                            onClick = {
                                showDifficultyDialog = false
                                tryStartGame(game, option.level)
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = option.color.copy(alpha = 0.12f),
                            border = BorderStroke(1.5.dp, option.color.copy(alpha = 0.7f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("difficulty_option_${option.tag}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = option.level,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = option.color
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = option.description,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                    )
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                    contentDescription = option.level,
                                    tint = option.color
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(
                    onClick = { showDifficultyDialog = false },
                    modifier = Modifier.testTag("dismiss_difficulty_dialog")
                ) {
                    Text("انصراف")
                }
            }
        )
    }

    // Insufficient Coins Dialog
    if (showInsufficientCoinsDialog) {
        AlertDialog(
            onDismissRequest = { showInsufficientCoinsDialog = false },
            title = {
                Text(
                    text = "سکه کافی ندارید! 🪙",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "برای شروع هر بازی به ۵۰ سکه نیاز دارید. موجودی فعلی شما ${userSettings.coins} سکه است.",
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "شما می‌توانید هر ۲۴ ساعت با کلیک روی دکمه دریافت سکه روزانه، ۱۵ سکه هدیه بگیرید!",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showInsufficientCoinsDialog = false
                        if (viewModel.canClaimDailyReward()) {
                            viewModel.claimDailyReward()
                            Toast.makeText(context, "🎁 ۱۵ سکه هدیه دریافت شد!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A))
                ) {
                    Text("دریافت سکه روزانه (۱۵)", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showInsufficientCoinsDialog = false }) {
                    Text("متوجه شدم")
                }
            }
        )
    }
}

@Composable
private fun GameCardItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    isGlassMode: Boolean,
    onClick: () -> Unit
) {
    WhiteBorderCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.surface,
        borderColor = iconColor.copy(alpha = 0.6f),
        borderWidth = 2.dp,
        isGlassMode = isGlassMode
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    lineHeight = 17.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFFEF08A))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "ورودی: ۵۰ 🪙",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF854D0E)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFDCFCE7))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "جایزه: ۱۰۰ 🏆",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF166534)
                        )
                    }
                }
            }
        }
    }
}
