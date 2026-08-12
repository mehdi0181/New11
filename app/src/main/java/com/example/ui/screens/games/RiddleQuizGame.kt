package com.example.ui.screens.games

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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

data class RiddleQuestion(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctIndex: Int
)

val RIDDLE_QUESTIONS = listOf(
    RiddleQuestion(1, "آن چیست که هر چه از آن برمی‌داری، بزرگ‌تر می‌شود؟", listOf("سوراخ", "رودخانه", "کوه", "درخت"), 0),
    RiddleQuestion(2, "آن چیست که پر دارد اما پرواز نمی‌کند، خانه دارد اما سقف ندارد؟", listOf("شاهین", "تیر", "کشتی", "هواپیما"), 1),
    RiddleQuestion(3, "آن چیست که تا اسمش را می‌بری می‌شکند؟", listOf("شیشه", "سکوت", "غرور", "لیوان"), 1),
    RiddleQuestion(4, "آن چیست که کلیدهای زیاد دارد اما هیچ درگاهی را باز نمی‌کند؟", listOf("پیانو", "کیف", "گاوصندوق", "ماشین"), 0),
    RiddleQuestion(5, "آن چیست که پا ندارد ولی همواره می‌دود؟", listOf("باد", "زمان", "رودخانه", "موتور"), 2),
    RiddleQuestion(6, "آن چیست که یک چشم دارد اما هیچ‌چیز را نمی‌بیند؟", listOf("سوزن", "طوفان", "پنجره", "دوربین"), 0),
    RiddleQuestion(7, "آن چیست که سه چشم و یک پا دارد؟", listOf("چراغ راهنمایی", "عصا", "دوربین عکاسی", "ساعت"), 0),
    RiddleQuestion(8, "آن چیست که تا آب به آن می‌رسد می‌میرد؟", listOf("آتش", "ماهی", "گل", "سنگ"), 0),
    RiddleQuestion(9, "آن چیست که همه آن را دارند ولی هیچ‌کس نمی‌تواند آن را ببیند؟", listOf("نام", "سایه", "نفس", "فکر"), 0),
    RiddleQuestion(10, "آن چیست که هر چه بشویید کثیف‌تر می‌شود؟", listOf("آب", "لباس", "دست", "ظرف"), 0),
    RiddleQuestion(11, "آن چیست که نیش دارد اما زنبور نیست؟", listOf("عقرب", "سوزن", "حقیقت", "مار"), 0),
    RiddleQuestion(12, "آن چیست که دندان دارد ولی نمی‌جود؟", listOf("شانه", "اره", "سیر", "همه موارد"), 3),
    RiddleQuestion(13, "آن چیست که هر چقدر پیرتر شود، جوان‌تر به نظر می‌رسد؟", listOf("شمع", "پیاز", "عکس", "هنر"), 1),
    RiddleQuestion(14, "آن چیست که اگر آن را نگه نداری می‌شکند؟", listOf("قول", "بادکنک", "شیشه", "تخم‌مرغ"), 0),
    RiddleQuestion(15, "آن چیست که بالا می‌رود اما هرگز پایین نمی‌آید؟", listOf("سن", "دود", "قیمت", "پرنده"), 0),
    RiddleQuestion(16, "کدام حیوان را اگر برعکس کنید قرمز می‌شود؟", listOf("خرس", "گرگ", "پلنگ", "شیر"), 0),
    RiddleQuestion(17, "آن کدام شبه‌فلزی است که اگر وارونه‌اش کنید نوعی سبزی می‌شود؟", listOf("جیوه", "سرب", "روی", "مس"), 0),
    RiddleQuestion(18, "نوعی غذای فرنگی که در وسطش نام رودخانه ایرانی قرار دارد؟", listOf("لازانیا", "پیتزا", "ماکارونی", "سوپ"), 2),
    RiddleQuestion(19, "آن چیست که بدنش زرد است، لباسش سبز و موهایش سفید؟", listOf("موز", "بلال", "خیار", "هویج"), 1),
    RiddleQuestion(20, "کدام کلمه ۵ حرفی است که اگر دو حرف به آن اضافه کنید کوتاه‌تر می‌شود؟", listOf("کوتاه", "بزرگ", "زیبا", "کوچک"), 0),
    RiddleQuestion(21, "در مسابقه دو اگر از نفر دوم سبقت بگیرید نفر چندم می‌شوید؟", listOf("نفر اول", "نفر دوم", "نفر سوم", "آخرین نفر"), 1),
    RiddleQuestion(22, "چه چیزی هرچه بیشتر از آن بردارید بزرگ‌تر می‌شود؟", listOf("چاله", "کوه‌های برف", "بادکنک", "امید"), 0),
    RiddleQuestion(23, "کدام عدد اگر وارونه شود یک و نیم برابر خودش می‌شود؟", listOf("۳", "۶", "۸", "۹"), 1),
    RiddleQuestion(24, "دو پدر و دو پسر سر سفره‌اند اما فقط ۳ نفر هستند، چطور ممکن است؟", listOf("اشتباه محاسباتی", "پدربزرگ، پدر و پسر", "یکی غایب است", "دوقلو هستند"), 1),
    RiddleQuestion(25, "چه چیزی با گفتن نامش از بین می‌رود و می‌شکند؟", listOf("سکوت", "شیشه", "غرور", "راز"), 0)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiddleQuizGame(
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
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var selectedAnswerIndex by remember { mutableStateOf<Int?>(null) }
    var isGameOver by remember { mutableStateOf(false) }
    var hasAwardedCoins by remember { mutableStateOf(false) }

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

    // Award reward if won
    val isWin = score >= 8
    LaunchedEffect(isGameOver) {
        if (isGameOver && isWin && !hasAwardedCoins) {
            hasAwardedCoins = true
            viewModel.addCoins(100)
        }
    }

    val currentQ = RIDDLE_QUESTIONS.getOrNull(currentQuestionIndex)

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
                                text = "مسابقه ۱ دقیقه چیستان ⏱️",
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
                            modifier = Modifier.testTag("riddle_exit_button")
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
                if (!isGameOver && currentQ != null) {
                    // Timer & Status Bar
                    WhiteBorderCard(
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = MaterialTheme.colorScheme.surface,
                        borderColor = Color(0xFF0284C7),
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
                                        tint = if (timeLeft <= 10) Color.Red else Color(0xFF0284C7)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "زمان باقی‌مانده: $timeLeft ثانیه",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = if (timeLeft <= 10) Color.Red else MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Text(
                                    text = "امتیاز: $score / 8 (پیروزی)",
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
                                color = if (timeLeft <= 10) Color.Red else Color(0xFF0284C7),
                                trackColor = Color.LightGray.copy(alpha = 0.3f)
                            )
                        }
                    }

                    // Question Card
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
                                text = "سوال ${currentQuestionIndex + 1} از ${RIDDLE_QUESTIONS.size}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = currentQ.question,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 26.sp
                            )
                        }
                    }

                    // Answer Options
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        currentQ.options.forEachIndexed { index, option ->
                            val isSelected = selectedAnswerIndex == index
                            val isCorrect = index == currentQ.correctIndex

                            val buttonColor = when {
                                selectedAnswerIndex == null -> MaterialTheme.colorScheme.surface
                                isSelected && isCorrect -> Color(0xFFDCFCE7)
                                isSelected && !isCorrect -> Color(0xFFFEE2E2)
                                isCorrect -> Color(0xFFDCFCE7)
                                else -> MaterialTheme.colorScheme.surface
                            }

                            val borderColor = when {
                                selectedAnswerIndex == null -> MaterialTheme.colorScheme.outline
                                isCorrect -> Color(0xFF16A34A)
                                isSelected -> Color(0xFFDC2626)
                                else -> MaterialTheme.colorScheme.outline
                            }

                            WhiteBorderCard(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    if (selectedAnswerIndex == null) {
                                        selectedAnswerIndex = index
                                        if (isCorrect) {
                                            score++
                                        }
                                    }
                                },
                                containerColor = buttonColor,
                                borderColor = borderColor,
                                borderWidth = 2.dp,
                                isGlassMode = isGlassMode
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = option,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    if (selectedAnswerIndex != null) {
                                        if (isCorrect) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "درست",
                                                tint = Color(0xFF16A34A)
                                            )
                                        } else if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Cancel,
                                                contentDescription = "نادرست",
                                                tint = Color(0xFFDC2626)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Next Question Button
                    if (selectedAnswerIndex != null) {
                        Button(
                            onClick = {
                                selectedAnswerIndex = null
                                if (currentQuestionIndex < RIDDLE_QUESTIONS.size - 1) {
                                    currentQuestionIndex++
                                } else {
                                    isGameOver = true
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (currentQuestionIndex < RIDDLE_QUESTIONS.size - 1) "سوال بعدی ➔" else "مشاهده نتیجه مسابقه 🏁",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                } else {
                    // Game Over Screen
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
                                text = "تعداد پاسخ‌های درست: $score از ${RIDDLE_QUESTIONS.size}",
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
                                    text = "برای دریافت ۱۰۰ سکه باید حداقل به ۸ سوال پاسخ درست دهید.",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = onExit,
                                    modifier = Modifier
                                        .weight(1f)
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
}
