package com.example.ui.screens.games

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.CoinBalanceHeaderBadge
import com.example.ui.components.OnlineMatchmakingOverlay
import com.example.ui.components.OnlinePlayerInfo
import com.example.ui.components.ScenicGlassContainer
import com.example.viewmodel.AppViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Neon Glow Colors
val NeonCyan = Color(0xFF00F0FF)
val NeonCyanGlow = Color(0xFF00F0FF).copy(alpha = 0.35f)
val NeonPink = Color(0xFFFF007F)
val NeonPinkGlow = Color(0xFFFF007F).copy(alpha = 0.35f)
val NeonPurple = Color(0xFFD946EF)
val DarkCyberBg = Color(0xFF090D16)
val CyberCardBg = Color(0xFF111827)

@Composable
fun NeonXSymbol(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val minDim = size.minDimension
        val strokeGlow = minDim * 0.16f
        val strokeTube = minDim * 0.08f
        val strokeCore = minDim * 0.035f
        val pad = minDim * 0.22f

        val start1 = Offset(pad, pad)
        val end1 = Offset(size.width - pad, size.height - pad)
        val start2 = Offset(size.width - pad, pad)
        val end2 = Offset(pad, size.height - pad)

        // 1. Outer Glow Layer
        drawLine(color = NeonCyanGlow, start = start1, end = end1, strokeWidth = strokeGlow, cap = StrokeCap.Round)
        drawLine(color = NeonCyanGlow, start = start2, end = end2, strokeWidth = strokeGlow, cap = StrokeCap.Round)

        // 2. Neon Tube Layer
        drawLine(color = NeonCyan, start = start1, end = end1, strokeWidth = strokeTube, cap = StrokeCap.Round)
        drawLine(color = NeonCyan, start = start2, end = end2, strokeWidth = strokeTube, cap = StrokeCap.Round)

        // 3. Inner White Core
        drawLine(color = Color.White, start = start1, end = end1, strokeWidth = strokeCore, cap = StrokeCap.Round)
        drawLine(color = Color.White, start = start2, end = end2, strokeWidth = strokeCore, cap = StrokeCap.Round)
    }
}

@Composable
fun NeonOSymbol(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val minDim = size.minDimension
        val strokeGlow = minDim * 0.16f
        val strokeTube = minDim * 0.08f
        val strokeCore = minDim * 0.035f

        val radius = (minDim / 2f) - (minDim * 0.22f)
        val center = Offset(size.width / 2f, size.height / 2f)

        // 1. Outer Glow Layer
        drawCircle(color = NeonPinkGlow, radius = radius, center = center, style = Stroke(width = strokeGlow))

        // 2. Neon Tube Layer
        drawCircle(color = NeonPink, radius = radius, center = center, style = Stroke(width = strokeTube))

        // 3. Inner White Core
        drawCircle(color = Color.White, radius = radius, center = center, style = Stroke(width = strokeCore))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicTacToeGame(
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

    var board = remember { mutableStateListOf("", "", "", "", "", "", "", "", "") }
    var isPlayerTurn by remember { mutableStateOf(true) } // Player is "X", Online player is "O"
    var winner by remember { mutableStateOf<String?>(null) } // "X", "O", "DRAW"
    var playerWins by remember { mutableIntStateOf(0) }
    var hasAwardedCoins by remember { mutableStateOf(false) }

    fun checkWinner(b: List<String>): String? {
        val winPatterns = listOf(
            listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8), // Rows
            listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8), // Cols
            listOf(0, 4, 8), listOf(2, 4, 6)                  // Diagonals
        )

        for (p in winPatterns) {
            if (b[p[0]].isNotEmpty() && b[p[0]] == b[p[1]] && b[p[1]] == b[p[2]]) {
                return b[p[0]]
            }
        }
        if (b.all { it.isNotEmpty() }) return "DRAW"
        return null
    }

    fun startNextRound() {
        for (i in 0..8) {
            board[i] = ""
        }
        isPlayerTurn = true
        winner = null
    }

    // Award Coins if Player X Wins 3 times
    LaunchedEffect(winner) {
        if (winner == "X") {
            val newWins = playerWins + 1
            playerWins = newWins
            if (newWins >= 3 && !hasAwardedCoins) {
                hasAwardedCoins = true
                viewModel.addCoins(100)
            }
        }
    }

    fun handleCellClick(index: Int) {
        if (board[index].isNotEmpty() || winner != null || !isPlayerTurn) return

        board[index] = "X"
        val w = checkWinner(board)
        if (w != null) {
            winner = w
            return
        }

        isPlayerTurn = false

        // Online Opponent Move (Simulated synchronized real online turn timing)
        scope.launch {
            delay((1200..1800).random().toLong())
            val emptyIndices = board.indices.filter { board[it].isEmpty() }
            if (emptyIndices.isNotEmpty()) {
                val opponentIndex = emptyIndices.random()
                board[opponentIndex] = "O"
                val oppWin = checkWinner(board)
                if (oppWin != null) {
                    winner = oppWin
                }
            }
            isPlayerTurn = true
        }
    }

    if (showMatchmaking) {
        OnlineMatchmakingOverlay(
            gameTitle = "دوز (XO)",
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
                                    text = "بازی دوز آنلاین (XO) ❌⭕",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "اتاق آنلاین • پینگ ${onlinePlayer?.pingMs ?: 32}ms 📶",
                                    fontSize = 11.sp,
                                    color = NeonCyan,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = onExit,
                                modifier = Modifier.testTag("tictactoe_exit_button")
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
                containerColor = DarkCyberBg
            ) { paddingValues ->
                // Centered layout container
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 420.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Header Player Info with Neon Glow
                        val headerGradientBrush = remember {
                            Brush.horizontalGradient(listOf(NeonCyan, NeonPurple, NeonPink))
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(CyberCardBg)
                                .border(
                                    width = 1.5.dp,
                                    brush = headerGradientBrush,
                                    shape = RoundedCornerShape(18.dp)
                                )
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(NeonCyanGlow)
                                            .border(1.5.dp, NeonCyan, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        NeonXSymbol(modifier = Modifier.size(22.dp))
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "شما (X)",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NeonCyan
                                        )
                                        Text(
                                            text = "برد: $playerWins / ۳",
                                            fontSize = 11.sp,
                                            color = Color.White.copy(alpha = 0.8f)
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(32.dp)
                                        .background(Color.White.copy(alpha = 0.2f))
                                )

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(NeonPinkGlow)
                                            .border(1.5.dp, NeonPink, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = onlinePlayer?.avatarEmoji ?: "👤",
                                            fontSize = 18.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = onlinePlayer?.name ?: "حریف آنلاین",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NeonPink
                                        )
                                        Text(
                                            text = "🟢 ${onlinePlayer?.location ?: "آنلاین"}",
                                            fontSize = 11.sp,
                                            color = Color(0xFF4ADE80)
                                        )
                                    }
                                }
                            }
                        }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 3x3 Grid Board with Glowing Neon Borders
                    val boardRadialBg = remember {
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF1E1B4B),
                                Color(0xFF0F172A),
                                Color(0xFF090D16)
                            )
                        )
                    }
                    val boardSweepBorder = remember {
                        Brush.sweepGradient(
                            listOf(NeonCyan, NeonPurple, NeonPink, NeonCyan)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(boardRadialBg)
                            .border(
                                width = 2.5.dp,
                                brush = boardSweepBorder,
                                shape = RoundedCornerShape(24.dp)
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            for (row in 0..2) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    for (col in 0..2) {
                                        val index = row * 3 + col
                                        val cellText = board[index]

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1f)
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(
                                                    when (cellText) {
                                                        "X" -> Color(0xFF082F49)
                                                        "O" -> Color(0xFF4C0519)
                                                        else -> Color(0xFF1E293B).copy(alpha = 0.8f)
                                                    }
                                                )
                                                .border(
                                                    width = 2.dp,
                                                    color = when (cellText) {
                                                        "X" -> NeonCyan
                                                        "O" -> NeonPink
                                                        else -> NeonCyan.copy(alpha = 0.35f)
                                                    },
                                                    shape = RoundedCornerShape(16.dp)
                                                )
                                                .clickable(enabled = cellText.isEmpty() && winner == null) {
                                                    handleCellClick(index)
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            when (cellText) {
                                                "X" -> NeonXSymbol(modifier = Modifier.fillMaxSize(0.68f))
                                                "O" -> NeonOSymbol(modifier = Modifier.fillMaxSize(0.68f))
                                                else -> {}
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Turn Status Banner
                    if (winner == null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isPlayerTurn) NeonCyanGlow else NeonPinkGlow
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isPlayerTurn) NeonCyan else NeonPink,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 20.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = if (isPlayerTurn) "⚡ نوبت شماست (روی خانه خالی بزنید)" else "💬 ${onlinePlayer?.name ?: "حریف آنلاین"} در حال تصمیم‌گیری...",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isPlayerTurn) NeonCyan else NeonPink,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        // Game Outcome Card
                        val roundWin = winner == "X"
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + scaleIn()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(CyberCardBg)
                                    .border(
                                        width = 2.dp,
                                        color = if (roundWin) Color(0xFF22C55E) else if (winner == "O") NeonPink else Color(0xFFEAB308),
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .padding(18.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.EmojiEvents,
                                        contentDescription = "نتیجه",
                                        tint = if (roundWin) Color(0xFFEAB308) else Color.Gray,
                                        modifier = Modifier.size(48.dp)
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = when (winner) {
                                            "X" -> "🏆 شما برنده این دور شدید!"
                                            "O" -> "👑 ${onlinePlayer?.name ?: "حریف آنلاین"} برنده شد!"
                                            else -> "🤝 بازی مساوی شد!"
                                        },
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (roundWin) Color(0xFF4ADE80) else if (winner == "O") NeonPink else Color(0xFFFDE047)
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    if (playerWins >= 3) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(Color(0xFF166534))
                                                .padding(horizontal = 14.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = "🎉 تبریک! ۳ برد کامل شد و ۱۰۰ سکه جایزه گرفتید!",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    } else {
                                        Text(
                                            text = "تعداد بردهای شما: $playerWins از ۳ برد برای ۱۰۰ سکه",
                                            fontSize = 12.sp,
                                            color = Color.White.copy(alpha = 0.8f),
                                            textAlign = TextAlign.Center
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Button(
                                            onClick = { startNextRound() },
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(44.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text("دور بعدی 🔁", color = Color.White, fontWeight = FontWeight.Bold)
                                        }

                                        Button(
                                            onClick = onExit,
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(44.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text("خروج", color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
}
