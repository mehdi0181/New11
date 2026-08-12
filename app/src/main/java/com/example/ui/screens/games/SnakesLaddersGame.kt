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

val LADDERS_MAP = mapOf(
    4 to 16,
    12 to 26,
    18 to 29
)

val SNAKES_MAP = mapOf(
    15 to 5,
    24 to 10,
    32 to 20
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SnakesLaddersGame(
    viewModel: AppViewModel,
    difficulty: String = "سطح متوسط",
    onExit: () -> Unit
) {
    val userSettings by viewModel.userSettings.collectAsStateWithLifecycle()
    val isDark = true // Always dark mode for games for eye comfort
    val isGlassMode = userSettings.isGlassMode
    val scope = rememberCoroutineScope()

    var showMatchmaking by remember { mutableStateOf(true) }
    var onlinePlayer by remember { mutableStateOf<OnlinePlayerInfo?>(null) }

    var playerPos by remember { mutableIntStateOf(1) }
    var aiPos by remember { mutableIntStateOf(1) }
    var isPlayerTurn by remember { mutableStateOf(true) }
    var diceValue by remember { mutableIntStateOf(1) }
    var isRolling by remember { mutableStateOf(false) }
    var pawnNeedsManualMove by remember { mutableStateOf(false) }
    var statusText by remember { mutableStateOf("برای ریختن تاس روی سینی تاس ضربه بزنید 🎲") }
    var winner by remember { mutableStateOf<String?>(null) } // "PLAYER" or "OPPONENT"
    var hasAwardedCoins by remember { mutableStateOf(false) }

    val isWin = winner == "PLAYER"

    // Award Coins
    LaunchedEffect(winner) {
        if (winner == "PLAYER" && !hasAwardedCoins) {
            hasAwardedCoins = true
            viewModel.addCoins(100)
        }
    }

    // 1. Player Rolls Dice inside Tray
    fun rollDiceForPlayer() {
        if (isRolling || winner != null || !isPlayerTurn || pawnNeedsManualMove) return

        scope.launch {
            isRolling = true
            repeat(7) {
                diceValue = (1..6).random()
                delay(80)
            }
            isRolling = false
            pawnNeedsManualMove = true
            statusText = "تاس $diceValue آوردید! 👈 روی مهره آبی رنگ خود ضربه بزنید تا $diceValue خانه حرکت کند."
        }
    }

    // 2. Player Manually Taps Pawn to Move
    fun onPlayerMovePawn() {
        if (!pawnNeedsManualMove || winner != null) return

        pawnNeedsManualMove = false

        scope.launch {
            var newPos = playerPos + diceValue
            if (newPos > 36) newPos = 36

            var log = "شما $diceValue خانه جلو رفتید!"
            if (LADDERS_MAP.containsKey(newPos)) {
                val climbed = LADDERS_MAP[newPos]!!
                log += " 🪜 نردبان! صعود از $newPos به $climbed!"
                newPos = climbed
            } else if (SNAKES_MAP.containsKey(newPos)) {
                val bitten = SNAKES_MAP[newPos]!!
                log += " 🐍 نیش مار! سقوط از $newPos به $bitten!"
                newPos = bitten
            }

            playerPos = newPos
            statusText = log

            if (playerPos >= 36) {
                winner = "PLAYER"
                return@launch
            }

            // Switch to Online Opponent Turn
            isPlayerTurn = false
            val oppName = onlinePlayer?.name ?: "حریف آنلاین"
            statusText = "💬 $oppName در حال تاس ریختن..."
            delay((1200..1800).random().toLong())

            // Opponent Turn Simulation
            isRolling = true
            repeat(6) {
                diceValue = (1..6).random()
                delay(80)
            }
            isRolling = false

            val aiRoll = diceValue
            var aiNewPos = aiPos + aiRoll
            if (aiNewPos > 36) aiNewPos = 36

            var aiLog = "$oppName تاس $aiRoll آورد و به خانه $aiNewPos رفت."
            if (LADDERS_MAP.containsKey(aiNewPos)) {
                val climbed = LADDERS_MAP[aiNewPos]!!
                aiLog += " 🪜 نردبان! صعود به $climbed"
                aiNewPos = climbed
            } else if (SNAKES_MAP.containsKey(aiNewPos)) {
                val bitten = SNAKES_MAP[aiNewPos]!!
                aiLog += " 🐍 نیش مار! سقوط به $bitten"
                aiNewPos = bitten
            }

            aiPos = aiNewPos
            if (aiPos >= 36) {
                winner = "OPPONENT"
                statusText = aiLog
                return@launch
            }

            statusText = "$aiLog \nنوبت شماست! تاس بریزید."
            isPlayerTurn = true
        }
    }

    if (showMatchmaking) {
        OnlineMatchmakingOverlay(
            gameTitle = "مار و پله آنلاین",
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
                                    text = "بازی مار و پله آنلاین 🐍🪜",
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
                                modifier = Modifier.testTag("snakes_exit_button")
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
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header Player Info with Pawns
                    WhiteBorderCard(
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = Color(0xFF1E293B),
                        borderColor = Color(0xFF16A34A),
                        borderWidth = 2.dp,
                        isGlassMode = false
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RealisticGamePawn(
                                    color = Color(0xFF00F0FF),
                                    accentColor = Color.White,
                                    pawnSize = 26.dp,
                                    isHighlighted = false
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "شما: خانه $playerPos",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = onlinePlayer?.avatarEmoji ?: "👤",
                                    fontSize = 18.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${onlinePlayer?.name ?: "حریف آنلاین"}: خانه $aiPos",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }

                // 6x6 Board Grid
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
                            .padding(8.dp)
                    ) {
                        for (row in 5 downTo 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                val colIndices = if (row % 2 == 1) (5 downTo 0) else (0..5)
                                for (col in colIndices) {
                                    val cellNum = row * 6 + col + 1
                                    val isPlayerHere = playerPos == cellNum
                                    val isAiHere = aiPos == cellNum
                                    val hasLadder = LADDERS_MAP.containsKey(cellNum)
                                    val hasSnake = SNAKES_MAP.containsKey(cellNum)

                                    val cellBg = when {
                                        cellNum == 36 -> Color(0xFF854D0E)
                                        hasLadder -> Color(0xFF064E3B)
                                        hasSnake -> Color(0xFF881337)
                                        else -> if ((row + col) % 2 == 0) Color(0xFF1E293B) else Color(0xFF111827)
                                    }

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(54.dp)
                                            .padding(2.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(cellBg)
                                            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxSize().padding(2.dp)
                                        ) {
                                            Text(
                                                text = if (hasLadder) "🪜$cellNum" else if (hasSnake) "🐍$cellNum" else "$cellNum",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White.copy(alpha = 0.8f)
                                            )

                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                if (isPlayerHere) {
                                                    RealisticGamePawn(
                                                        color = Color(0xFF00F0FF),
                                                        pawnSize = 28.dp,
                                                        isHighlighted = pawnNeedsManualMove,
                                                        onClick = {
                                                            if (pawnNeedsManualMove) {
                                                                onPlayerMovePawn()
                                                            }
                                                        }
                                                    )
                                                }
                                                if (isAiHere) {
                                                    RealisticGamePawn(
                                                        color = Color(0xFFFF0055),
                                                        pawnSize = 28.dp,
                                                        isHighlighted = false
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (winner == null) {
                    // Dedicated Interactive Dice Rolling Tray Box
                    DiceTrayBox(
                        diceValue = diceValue,
                        isRolling = isRolling,
                        isPlayerTurn = isPlayerTurn && !pawnNeedsManualMove,
                        statusText = statusText,
                        onRollClick = { rollDiceForPlayer() }
                    )
                } else {
                    // Game Outcome
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
                                text = if (isWin) "شما برنده بازی آنلاین مار و پله شدید! 🎉" else "${onlinePlayer?.name ?: "حریف آنلاین"} زودتر به خانه ۳۶ رسید!",
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
