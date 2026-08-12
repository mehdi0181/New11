package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

data class OnlinePlayerInfo(
    val name: String,
    val avatarEmoji: String,
    val winRate: String,
    val location: String,
    val pingMs: Int
)

val ONLINE_PLAYERS_POOL = listOf(
    OnlinePlayerInfo("علی_تهرانی", "😎", "٪۶۸ برد", "تهران", 32),
    OnlinePlayerInfo("سارا_شیراز", "🌸", "٪۷۴ برد", "شیراز", 42),
    OnlinePlayerInfo("امیر_اصفهان", "⚡", "٪۸۱ برد", "اصفهان", 28),
    OnlinePlayerInfo("رضا_تبریز", "🦁", "٪۶۵ برد", "تبریز", 38),
    OnlinePlayerInfo("مریم_مشهد", "💎", "٪۷۹ برد", "مشهد", 45),
    OnlinePlayerInfo("حسین_کرج", "🔥", "٪۷۱ برد", "کرج", 29),
    OnlinePlayerInfo("نیلوفر_اهواز", "✨", "٪۷۶ برد", "اهواز", 35)
)

/**
 * Real-time Online Matchmaking Queue Dialog / Overlay
 */
@Composable
fun OnlineMatchmakingOverlay(
    gameTitle: String,
    onMatchFound: (OnlinePlayerInfo) -> Unit,
    onCancelSearch: () -> Unit
) {
    var searchSeconds by remember { mutableIntStateOf(0) }
    var matchedPlayer by remember { mutableStateOf<OnlinePlayerInfo?>(null) }
    var isConnectingToRoom by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Simulate real online matchmaking search
    LaunchedEffect(Unit) {
        while (matchedPlayer == null) {
            delay(1000L)
            searchSeconds++

            // Match found after 2 to 4 seconds
            if (searchSeconds >= 3) {
                val found = ONLINE_PLAYERS_POOL.random()
                matchedPlayer = found
                isConnectingToRoom = true
                delay(1800L) // Prepare online synchronized room
                onMatchFound(found)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.65f))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF0F172A).copy(alpha = 0.75f),
            border = androidx.compose.foundation.BorderStroke(
                width = 2.dp,
                brush = Brush.horizontalGradient(
                    listOf(Color.White.copy(alpha = 0.9f), Color(0xFF38BDF8), Color.White.copy(alpha = 0.9f))
                )
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (matchedPlayer == null) {
                    // Searching for Online Opponent
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(Color(0xFF00F0FF).copy(alpha = 0.3f), Color.Transparent)
                                )
                            )
                            .border(2.dp, Color(0xFF00F0FF), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(70.dp),
                            color = Color(0xFF00F0FF),
                            strokeWidth = 3.dp
                        )
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = "سرور آنلاین",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "در حال جست‌وجوی حریف آنلاین... 🌐",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "بازی آنلاین $gameTitle • زمان جست‌وجو: $searchSeconds ثانیه",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Wifi,
                            contentDescription = "پینگ سرور",
                            tint = Color(0xFF00FF88),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "وضعیت سرور: آنلاین (پینگ 34ms)",
                            fontSize = 11.sp,
                            color = Color(0xFF4ADE80),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedButton(
                        onClick = onCancelSearch,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF87171)),
                        modifier = Modifier.fillMaxWidth().height(46.dp)
                    ) {
                        Text("انصراف و خروج", fontWeight = FontWeight.Bold)
                    }
                } else {
                    // Match Found!
                    val player = matchedPlayer!!

                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "حریف پیدا شد",
                        tint = Color(0xFF00FF88),
                        modifier = Modifier.size(64.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "حریف آنلاین پیدا شد! 🎯",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF00FF88)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Opponent Profile Card
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF1E293B))
                            .border(1.5.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF3B82F6)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = player.avatarEmoji, fontSize = 26.sp)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = player.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "🟢 آنلاین",
                                    fontSize = 10.sp,
                                    color = Color(0xFF4ADE80),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "شهر: ${player.location} • سابقه: ${player.winRate}",
                                fontSize = 11.5.sp,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color(0xFF00F0FF),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "آماده‌سازی اتاق همگام‌سازی بازی آنلاین...",
                            fontSize = 12.sp,
                            color = Color(0xFFFEF08A),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
