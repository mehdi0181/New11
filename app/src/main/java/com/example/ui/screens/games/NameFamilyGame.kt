package com.example.ui.screens.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Send
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameFamilyGame(
    viewModel: AppViewModel,
    difficulty: String = "سطح متوسط",
    onExit: () -> Unit
) {
    val userSettings by viewModel.userSettings.collectAsStateWithLifecycle()
    val isDark = userSettings.themeMode == "DARK"
    val isGlassMode = userSettings.isGlassMode

    val targetLetter = remember { listOf("م", "ب", "س", "ش", "الف").random() }

    var nameInput by remember { mutableStateOf("") }
    var familyInput by remember { mutableStateOf("") }
    var cityInput by remember { mutableStateOf("") }
    var foodInput by remember { mutableStateOf("") }
    var animalInput by remember { mutableStateOf("") }

    val initialTime = when (difficulty) {
        "سطح آسان" -> 60
        "سطح سخت" -> 35
        "سطح پیشرفته و خیلی سخت" -> 25
        else -> 45 // سطح متوسط
    }

    var timeLeft by remember { mutableIntStateOf(initialTime) }
    var isSubmitted by remember { mutableStateOf(false) }
    var playerScore by remember { mutableIntStateOf(0) }
    var opponentScore by remember { mutableIntStateOf(0) }
    var hasAwardedCoins by remember { mutableStateOf(false) }

    // Countdown Timer
    LaunchedEffect(isSubmitted) {
        if (!isSubmitted) {
            while (timeLeft > 0 && !isSubmitted) {
                delay(1000L)
                timeLeft--
            }
            if (timeLeft <= 0) {
                isSubmitted = true
            }
        }
    }

    // Evaluate Scores on Submit
    LaunchedEffect(isSubmitted) {
        if (isSubmitted && !hasAwardedCoins) {
            var pScore = 0
            if (nameInput.trim().startsWith(targetLetter, ignoreCase = true) && nameInput.trim().length >= 2) pScore += 10
            if (familyInput.trim().startsWith(targetLetter, ignoreCase = true) && familyInput.trim().length >= 2) pScore += 10
            if (cityInput.trim().startsWith(targetLetter, ignoreCase = true) && cityInput.trim().length >= 2) pScore += 10
            if (foodInput.trim().startsWith(targetLetter, ignoreCase = true) && foodInput.trim().length >= 2) pScore += 10
            if (animalInput.trim().startsWith(targetLetter, ignoreCase = true) && animalInput.trim().length >= 2) pScore += 10

            playerScore = pScore
            opponentScore = when (difficulty) {
                "سطح آسان" -> listOf(10, 20).random()
                "سطح سخت" -> listOf(30, 40).random()
                "سطح پیشرفته و خیلی سخت" -> listOf(40, 50).random()
                else -> listOf(20, 30, 40).random()
            }

            val isAllCategoriesComplete = pScore == 50
            if (isAllCategoriesComplete && pScore >= opponentScore) {
                hasAwardedCoins = true
                viewModel.addCoins(100)
            }
        }
    }

    val isAllCategoriesComplete = playerScore == 50
    val isWin = isSubmitted && isAllCategoriesComplete && playerScore >= opponentScore

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
                                text = "اسم و فامیل آنلاین ✍️",
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
                            modifier = Modifier.testTag("name_family_exit_button")
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
                if (!isSubmitted) {
                    // Header Status & Letter Card
                    WhiteBorderCard(
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = MaterialTheme.colorScheme.surface,
                        borderColor = Color(0xFFEA580C),
                        borderWidth = 2.dp,
                        isGlassMode = isGlassMode
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
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
                                        tint = if (timeLeft <= 10) Color.Red else Color(0xFFEA580C)
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
                                    text = "حریف آنلاین: آرش 🧑‍💼",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0284C7)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            LinearProgressIndicator(
                                progress = { timeLeft / 45f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = if (timeLeft <= 10) Color.Red else Color(0xFFEA580C),
                                trackColor = Color.LightGray.copy(alpha = 0.3f)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "حرف انتخابی این دور:",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )

                            Box(
                                modifier = Modifier
                                    .padding(top = 6.dp)
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFED7AA))
                                    .border(2.dp, Color(0xFFEA580C), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = targetLetter,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF9A3412)
                                )
                            }
                        }
                    }

                    // Input Fields
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
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = nameInput,
                                onValueChange = { nameInput = it },
                                label = { Text("اسم (با حرف $targetLetter)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = familyInput,
                                onValueChange = { familyInput = it },
                                label = { Text("فامیل (با حرف $targetLetter)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = cityInput,
                                onValueChange = { cityInput = it },
                                label = { Text("شهر (با حرف $targetLetter)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = foodInput,
                                onValueChange = { foodInput = it },
                                label = { Text("غذا (با حرف $targetLetter)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = animalInput,
                                onValueChange = { animalInput = it },
                                label = { Text("حیوان (با حرف $targetLetter)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                    }

                    // Submit Button
                    Button(
                        onClick = { isSubmitted = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = "ارسال")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ثبت پاسخ‌ها و اتمام دور 🏁", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
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
                                text = if (isWin) "شما برنده شدید! 🎉" else "حریف برنده شد! 💔",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isWin) Color(0xFF16A34A) else Color(0xFFDC2626)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("امتیاز شما", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                                    Text("$playerScore", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("امتیاز حریف (آرش)", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                                    Text("$opponentScore", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0284C7))
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

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
                                    text = "برای دریافت ۱۰۰ سکه باید تمام ۵ دسته‌بندی را کامل و درست وارد کرده و امتیازی بالاتر یا برابر حریف کسب کنید.",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center
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
