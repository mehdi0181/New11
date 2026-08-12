package com.example.ui.screens.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import kotlinx.coroutines.delay

data class WordItem(
    val category: String,
    val word: String, // Persian word e.g. "انار"
    val hint: String
)

val WORD_LIST = listOf(
    WordItem("میوه 🍎", "انار", "میوه بهشتی سرخ رنگ با دانه‌های یاقوتی"),
    WordItem("حيوان 🦁", "شیر", "سلطان جنگل"),
    WordItem("کشور 🗺️", "ایران", "میهن عزیزمان"),
    WordItem("شهر 🏙️", "شیراز", "شهر حافظ و سعدی و بهار نارنج"),
    WordItem("خوراکی 🍕", "پیتزا", "غذای لذیذ ایتالیایی با پنیر فراوان"),
    WordItem("ورزش ⚽", "فوتبال", "محبوب‌ترین ورزش توپی جهان"),
    WordItem("وسیله 📱", "موبایل", "ابزار همراه ارتباطی روزمره"),
    WordItem("میوه 🍌", "موز", "میوه زرد رنگ و انرژی‌بخش"),
    WordItem("حیوان 🐘", "فیل", "بزرگ‌ترین پستاندار روی خشکی"),
    WordItem("گل 🌹", "رز", "نماد عشق و زیبایی"),
    WordItem("سیاره 🪐", "زحل", "سیاره‌ای با حلقه‌های زیبا"),
    WordItem("پرنده 🦅", "عقاب", "پرنده تیزبین و بلندپرواز"),
    WordItem("پوشاک 🧥", "کاپشن", "لباس گرم زمستانی"),
    WordItem("وسیله 💻", "لپ‌تاپ", "رایانه قابل حمل"),
    WordItem("طبیعت ⛰️", "دماوند", "بلندترین قله ایران")
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun WordGuessGame(
    viewModel: AppViewModel,
    difficulty: String = "سطح متوسط",
    onExit: () -> Unit
) {
    val userSettings by viewModel.userSettings.collectAsStateWithLifecycle()
    val isDark = userSettings.themeMode == "DARK"
    val isGlassMode = userSettings.isGlassMode

    val initialTime = when (difficulty) {
        "سطح آسان" -> 80
        "سطح سخت" -> 45
        "سطح پیشرفته و خیلی سخت" -> 30
        else -> 60 // سطح متوسط
    }

    var timeLeft by remember { mutableIntStateOf(initialTime) }
    var currentWordIndex by remember { mutableIntStateOf(0) }
    var solvedCount by remember { mutableIntStateOf(0) }
    var guessedLetters = remember { mutableStateListOf<Char>() }
    var inputGuess by remember { mutableStateOf("") }
    var isGameOver by remember { mutableStateOf(false) }
    var hasAwardedCoins by remember { mutableStateOf(false) }

    val currentItem = WORD_LIST.getOrNull(currentWordIndex)

    // Check if word is fully solved
    val isWordSolved = currentItem?.word?.all { char ->
        char.isWhitespace() || guessedLetters.contains(char)
    } == true

    // Countdown Timer
    LaunchedEffect(isGameOver) {
        if (!isGameOver) {
            while (timeLeft > 0 && !isGameOver) {
                delay(1000L)
                timeLeft--
            }
            if (timeLeft <= 0) {
                isGameOver = true
            }
        }
    }

    val isWin = solvedCount >= 8
    LaunchedEffect(isGameOver) {
        if (isGameOver && isWin && !hasAwardedCoins) {
            hasAwardedCoins = true
            viewModel.addCoins(100)
        }
    }

    // Auto advance when solved
    LaunchedEffect(isWordSolved) {
        if (isWordSolved && currentItem != null && !isGameOver) {
            solvedCount++
            delay(1000L)
            if (currentWordIndex < WORD_LIST.size - 1) {
                currentWordIndex++
                guessedLetters.clear()
                inputGuess = ""
            } else {
                isGameOver = true
            }
        }
    }

    ScenicGlassContainer(
        isGlassMode = isGlassMode,
        isDark = isDark
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "مسابقه ۱ دقیقه حدس کلمه 🔤",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "سطح: $difficulty",
                                fontSize = 11.sp,
                                color = Color(0xFFFEF08A),
                                fontWeight = FontWeight.Normal
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onExit,
                            modifier = Modifier.testTag("word_guess_exit_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "خروج"
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
                        containerColor = if (isGlassMode) Color.White.copy(alpha = 0.22f) else MaterialTheme.colorScheme.primary,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            },
            containerColor = if (isGlassMode) Color.Transparent else MaterialTheme.colorScheme.background
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (!isGameOver && currentItem != null) {
                    // Status Bar
                    WhiteBorderCard(
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = MaterialTheme.colorScheme.surface,
                        borderColor = Color(0xFF7C3AED),
                        borderWidth = 2.dp,
                        isGlassMode = isGlassMode
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Timer,
                                        contentDescription = "زمان",
                                        tint = if (timeLeft <= 10) Color.Red else Color(0xFF7C3AED)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "زمان: $timeLeft ثانیه",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = if (timeLeft <= 10) Color.Red else MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Text(
                                    text = "کلمات حل شده: $solvedCount / 8 (هدف)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF16A34A)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            LinearProgressIndicator(
                                progress = { timeLeft / 60f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = if (timeLeft <= 10) Color.Red else Color(0xFF7C3AED),
                                trackColor = Color.LightGray.copy(alpha = 0.3f)
                            )
                        }
                    }

                    // Word Display Box
                    WhiteBorderCard(
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = MaterialTheme.colorScheme.surface,
                        borderColor = MaterialTheme.colorScheme.outline,
                        borderWidth = 2.dp,
                        isGlassMode = isGlassMode
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "دسته‌بندی: ${currentItem.category}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF7C3AED)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = "راهنما",
                                    tint = Color(0xFFEAB308),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = currentItem.hint,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                    textAlign = TextAlign.Center
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Blanks representation
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                currentItem.word.forEach { char ->
                                    val isRevealed = guessedLetters.contains(char)
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isRevealed) Color(0xFFDCFCE7) else Color.LightGray.copy(alpha = 0.3f))
                                            .border(
                                                width = 2.dp,
                                                color = if (isRevealed) Color(0xFF16A34A) else Color.Gray,
                                                shape = RoundedCornerShape(8.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (isRevealed) char.toString() else "؟",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isRevealed) Color(0xFF166534) else Color.DarkGray
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Keyboard of letters
                    val persianLetters = listOf(
                        'ا', 'ب', 'پ', 'ت', 'ث', 'ج', 'چ', 'ح', 'خ',
                        'د', 'ذ', 'ر', 'ز', 'ژ', 'س', 'ش', 'ص', 'ض',
                        'ط', 'ظ', 'ع', 'غ', 'ف', 'ق', 'ک', 'گ', 'ل',
                        'م', 'ن', 'و', 'ه', 'ی'
                    )

                    WhiteBorderCard(
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = MaterialTheme.colorScheme.surface,
                        borderColor = MaterialTheme.colorScheme.outline,
                        borderWidth = 1.dp,
                        isGlassMode = isGlassMode
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "حروف را انتخاب کنید:",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                persianLetters.forEach { letterChar ->
                                    val isUsed = guessedLetters.contains(letterChar)
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isUsed) Color.LightGray else Color(0xFF7C3AED).copy(alpha = 0.15f))
                                            .clickable(enabled = !isUsed) {
                                                guessedLetters.add(letterChar)
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = letterChar.toString(),
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isUsed) Color.Gray else Color(0xFF7C3AED)
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Result Card
                    WhiteBorderCard(
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = MaterialTheme.colorScheme.surface,
                        borderColor = if (isWin) Color(0xFF16A34A) else Color(0xFFDC2626),
                        borderWidth = 3.dp,
                        isGlassMode = isGlassMode
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "نتیجه",
                                tint = if (isWin) Color(0xFFEAB308) else Color.Gray,
                                modifier = Modifier.size(64.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = if (isWin) "پیروز شدید! 🎉" else "زمان به پایان رسید! ⏱️",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isWin) Color(0xFF16A34A) else Color(0xFFDC2626)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "تعداد کلمات حدس زده شده: $solvedCount",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            if (isWin) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFFDCFCE7))
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = "🏆 جایزه ۱۰۰ سکه به حساب شما اضافه شد!",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF166534)
                                    )
                                }
                            } else {
                                Text(
                                    text = "برای دریافت ۱۰۰ سکه باید حداقل ۸ کلمه را درست حدس بزنید.",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = onExit,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("بازگشت به بازی‌ها", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
