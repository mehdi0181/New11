package com.example.ui.screens.games

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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.ui.components.DiceTrayBox
import com.example.ui.components.OnlineMatchmakingOverlay
import com.example.ui.components.OnlinePlayerInfo
import com.example.ui.components.RealisticGamePawn
import com.example.ui.components.ScenicGlassContainer
import com.example.ui.components.WhiteBorderCard
import com.example.viewmodel.AppViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenchGame(
    viewModel: AppViewModel,
    difficulty: String = "سطح متوسط",
    onExit: () -> Unit
) {
    val userSettings by viewModel.userSettings.collectAsStateWithLifecycle()
    val isDark = true
    val isGlassMode = userSettings.isGlassMode
    val scope = rememberCoroutineScope()

    var showMatchmaking by remember { mutableStateOf(true) }
    var onlinePlayer by remember { mutableStateOf<OnlinePlayerInfo?>(null) }

    var playerPawnStep by remember { mutableIntStateOf(-1) } // -1 = Base, 0..15 = track, 16 = Destination!
    var aiPawnStep by remember { mutableIntStateOf(-1) }

    var isPlayerTurn by remember { mutableStateOf(true) }
    var diceValue by remember { mutableIntStateOf(1) }
    var isRolling by remember { mutableStateOf(false) }
    var pawnNeedsManualMove by remember { mutableStateOf(false) }
    var statusText by remember { mutableStateOf("برای پرتاب تاس روی سینی تاس ضربه بزنید! (۶ برای خروج از پایگاه)") }
    var winner by remember { mutableStateOf<String?>(null) }
    var hasAwardedCoins by remember { mutableStateOf(false) }

    val isWin = winner == "PLAYER"

    // Award Coins
    LaunchedEffect(winner) {
        if (winner == "PLAYER" && !hasAwardedCoins) {
            hasAwardedCoins = true
            viewModel.addCoins(100)
        }
    }

    // Online Opponent Turn Runner
    fun runOpponentTurn() {
        scope.launch {
            isPlayerTurn = false
            val oppName = onlinePlayer?.name ?: "حریف آنلاین"
            statusText = "💬 $oppName در حال پرتاب تاس..."
            delay((1200..1800).random().toLong())

            isRolling = true
            repeat(6) {
                diceValue = (1..6).random()
                delay(80)
            }
            isRolling = false

            val aiRoll = diceValue
            if (aiPawnStep == -1) {
                if (aiRoll == 6) {
                    aiPawnStep = 0
                    statusText = "$oppName تاس ۶ آورد و مهره‌اش وارد بازی شد! یک جایزه پرتاب دیگر دارد..."
                    delay(1200)
                    runOpponentTurn() // Bonus roll for 6
                    return@launch
                } else {
                    statusText = "$oppName تاس $aiRoll آورد و مهره‌اش در پایگاه ماند."
                }
            } else {
                val aiNext = aiPawnStep + aiRoll
                if (aiNext >= 16) {
                    aiPawnStep = 16
                    winner = "OPPONENT"
                    statusText = "$oppName زودتر مهره خود را به مقصد رساند!"
                    return@launch
                } else {
                    aiPawnStep = aiNext
                    statusText = "$oppName مهره‌اش را به خانه $aiNext حرکت داد."
                }
            }

            statusText += "\nنوبت شماست! روی سینی تاس ضربه بزنید."
            isPlayerTurn = true
        }
    }

    // 1. Roll Dice inside Tray
    fun rollDiceForPlayer() {
        if (isRolling || winner != null || !isPlayerTurn || pawnNeedsManualMove) return

        scope.launch {
            isRolling = true
            repeat(7) {
                diceValue = (1..6).random()
                delay(80)
            }
            isRolling = false

            if (playerPawnStep == -1) {
                if (diceValue == 6) {
                    pawnNeedsManualMove = true
                    statusText = "تاس ۶ آورده‌اید! 👈 روی مهره سبز رنگ خود در پایگاه لمس کنید تا وارد بازی شود."
                } else {
                    statusText = "تاس $diceValue آمد (برای خروج به تاس ۶ نیاز دارید)."
                    runOpponentTurn()
                }
            } else {
                pawnNeedsManualMove = true
                statusText = "تاس $diceValue آورده‌اید! 👈 روی مهره سبز خود لمس کنید تا $diceValue خانه جلو برود."
            }
        }
    }

    // 2. Player Manually Taps Pawn
    fun onPlayerMovePawn() {
        if (!pawnNeedsManualMove || winner != null) return

        pawnNeedsManualMove = false

        scope.launch {
            if (playerPawnStep == -1) {
                // Must be 6
                playerPawnStep = 0
                statusText = "مهره شما وارد مسیر بازی شد! چون ۶ آوردید یک پرتاب جایزه دارید 🎉"
                isPlayerTurn = true
            } else {
                val nextStep = playerPawnStep + diceValue
                if (nextStep >= 16) {
                    playerPawnStep = 16
                    winner = "PLAYER"
                    statusText = "تبریک! مهره شما به مقصد نهایی منچ رسید! 🎉"
                    return@launch
                } else {
                    playerPawnStep = nextStep
                    statusText = "مهره شما به خانه $nextStep منتقل شد."
                    if (diceValue == 6) {
                        statusText += " (تاس ۶ = پرتاب مجدد جایزه!)"
                        isPlayerTurn = true
                    } else {
                        runOpponentTurn()
                    }
                }
            }
        }
    }

    if (showMatchmaking) {
        OnlineMatchmakingOverlay(
            gameTitle = "منچ آنلاین",
            onMatchFound = { player ->
                onlinePlayer = player
                showMatchmaking = false
            },
            onCancelSearch = onExit
        )
    } else {
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
                                    text = "بازی کلاسیک منچ آنلاین 🎲",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "اتاق آنلاین • پینگ ${onlinePlayer?.pingMs ?: 32}ms 📶",
                                    fontSize = 11.sp,
                                    color = Color(0xFFFEF08A),
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = onExit,
                                modifier = Modifier.testTag("mench_exit_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "خروج",
                                    tint = Color.White
                                )
                            }
                        },
                        actions = {
                            CoinBalanceHeaderBadge(
                                viewModel = viewModel,
                                coins = userSettings.coins,
                                isDark = true
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color(0xFF0F172A),
                            titleContentColor = Color.White,
                            navigationIconContentColor = Color.White
                        )
                    )
                },
                containerColor = Color(0xFF090D16)
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
                    // Status Header
                    WhiteBorderCard(
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = Color(0xFF1E293B),
                        borderColor = Color(0xFFDC2626),
                        borderWidth = 2.dp,
                        isGlassMode = false
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RealisticGamePawn(
                                    color = Color(0xFF00FF88),
                                    pawnSize = 26.dp,
                                    isHighlighted = false
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "شما (سبز): " + if (playerPawnStep == -1) "در پایگاه" else if (playerPawnStep == 16) "مقصد 🏁" else "خانه $playerPawnStep",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = onlinePlayer?.avatarEmoji ?: "🔴",
                                    fontSize = 18.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${onlinePlayer?.name ?: "حریف آنلاین"}: " + if (aiPawnStep == -1) "در پایگاه" else if (aiPawnStep == 16) "مقصد 🏁" else "خانه $aiPawnStep",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }

                // Mensch Board Arena Visualiser
                WhiteBorderCard(
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = Color(0xFF0F172A),
                    borderColor = Color(0xFF334155),
                    borderWidth = 2.dp,
                    isGlassMode = false
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "صفحه بازی منچ و مسیر مهره‌ها:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFDE047)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Player Base & AI Base + Track Steps
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Player Base (Left)
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("پایگاه شما", fontSize = 11.sp, color = Color(0xFF86EFAC), fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF064E3B))
                                        .border(2.dp, Color(0xFF00FF88), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (playerPawnStep == -1) {
                                        RealisticGamePawn(
                                            color = Color(0xFF00FF88),
                                            pawnSize = 36.dp,
                                            isHighlighted = pawnNeedsManualMove && playerPawnStep == -1,
                                            onClick = {
                                                if (pawnNeedsManualMove && playerPawnStep == -1) {
                                                    onPlayerMovePawn()
                                                }
                                            }
                                        )
                                    } else {
                                        Text("خالی", fontSize = 10.sp, color = Color.Gray)
                                    }
                                }
                            }

                            // Track Steps Grid
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("مسیر مسابقه (۱۶ خانه)", fontSize = 10.sp, color = Color.LightGray)
                                Spacer(modifier = Modifier.height(6.dp))

                                // 2 Rows of 8 Cells
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        (0..7).forEach { step ->
                                            val isPlayerOnStep = playerPawnStep == step
                                            val isAiOnStep = aiPawnStep == step

                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(28.dp)
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(
                                                        if (isPlayerOnStep) Color(0xFF065F46) else if (isAiOnStep) Color(0xFF991B1B) else Color(0xFF1E293B)
                                                    )
                                                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(6.dp)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (isPlayerOnStep) {
                                                    RealisticGamePawn(
                                                        color = Color(0xFF00FF88),
                                                        pawnSize = 22.dp,
                                                        isHighlighted = pawnNeedsManualMove,
                                                        onClick = {
                                                            if (pawnNeedsManualMove) {
                                                                onPlayerMovePawn()
                                                            }
                                                        }
                                                    )
                                                } else if (isAiOnStep) {
                                                    RealisticGamePawn(
                                                        color = Color(0xFFFF0055),
                                                        pawnSize = 22.dp,
                                                        isHighlighted = false
                                                    )
                                                } else {
                                                    Text("${step + 1}", fontSize = 9.sp, color = Color.Gray)
                                                }
                                            }
                                        }
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        (8..15).forEach { step ->
                                            val isPlayerOnStep = playerPawnStep == step
                                            val isAiOnStep = aiPawnStep == step

                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(28.dp)
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(
                                                        if (isPlayerOnStep) Color(0xFF065F46) else if (isAiOnStep) Color(0xFF991B1B) else Color(0xFF1E293B)
                                                    )
                                                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(6.dp)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (isPlayerOnStep) {
                                                    RealisticGamePawn(
                                                        color = Color(0xFF00FF88),
                                                        pawnSize = 22.dp,
                                                        isHighlighted = pawnNeedsManualMove,
                                                        onClick = {
                                                            if (pawnNeedsManualMove) {
                                                                onPlayerMovePawn()
                                                            }
                                                        }
                                                    )
                                                } else if (isAiOnStep) {
                                                    RealisticGamePawn(
                                                        color = Color(0xFFFF0055),
                                                        pawnSize = 22.dp,
                                                        isHighlighted = false
                                                    )
                                                } else {
                                                    Text("${step + 1}", fontSize = 9.sp, color = Color.Gray)
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // AI Base (Right)
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("پایگاه حریف", fontSize = 11.sp, color = Color(0xFFFCA5A5), fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF881337))
                                        .border(2.dp, Color(0xFFFF0055), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (aiPawnStep == -1) {
                                        RealisticGamePawn(
                                            color = Color(0xFFFF0055),
                                            pawnSize = 36.dp,
                                            isHighlighted = false
                                        )
                                    } else {
                                        Text("خالی", fontSize = 10.sp, color = Color.Gray)
                                    }
                                }
                            }
                        }
                    }
                }

                if (winner == null) {
                    // Dedicated Dice Tray
                    DiceTrayBox(
                        diceValue = diceValue,
                        isRolling = isRolling,
                        isPlayerTurn = isPlayerTurn && !pawnNeedsManualMove,
                        statusText = statusText,
                        onRollClick = { rollDiceForPlayer() }
                    )
                } else {
                    WhiteBorderCard(
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = Color(0xFF1E293B),
                        borderColor = if (isWin) Color(0xFF16A34A) else Color(0xFFDC2626),
                        borderWidth = 3.dp,
                        isGlassMode = false
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "پیروزی",
                                tint = if (isWin) Color(0xFFEAB308) else Color.Gray,
                                modifier = Modifier.size(56.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = if (isWin) "شما برنده بازی آنلاین منچ شدید! 🎉" else "${onlinePlayer?.name ?: "حریف آنلاین"} برنده منچ شد!",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isWin) Color(0xFF4ADE80) else Color(0xFFF87171),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            if (isWin) {
                                Text(
                                    text = "🏆 جایزه ۱۰۰ سکه به حساب شما اضافه شد!",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF86EFAC)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

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
}
