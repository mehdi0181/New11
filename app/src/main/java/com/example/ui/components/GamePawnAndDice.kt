package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * A realistic 3D board game pawn piece (مهره بازی)
 */
@Composable
fun RealisticGamePawn(
    color: Color,
    accentColor: Color = Color.White,
    modifier: Modifier = Modifier,
    pawnSize: Dp = 32.dp,
    isHighlighted: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pawnPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val currentScale = if (isHighlighted) pulseScale else 1.0f

    Box(
        modifier = modifier
            .size(pawnSize)
            .scale(currentScale)
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height

            // 1. Shadow underneath
            drawOval(
                color = Color.Black.copy(alpha = 0.35f),
                topLeft = Offset(w * 0.15f, h * 0.82f),
                size = Size(w * 0.7f, h * 0.18f)
            )

            // 2. Pulse Highlight Ring if clickable/ready
            if (isHighlighted) {
                drawCircle(
                    color = accentColor.copy(alpha = 0.6f),
                    radius = (w / 2f) * 0.95f,
                    center = Offset(w / 2f, h / 2f),
                    style = Stroke(width = w * 0.08f)
                )
            }

            // 3. Flared Pawn Base
            val basePath = Path().apply {
                moveTo(w * 0.2f, h * 0.85f)
                quadraticTo(w * 0.5f, h * 0.72f, w * 0.8f, h * 0.85f)
                quadraticTo(w * 0.5f, h * 0.96f, w * 0.2f, h * 0.85f)
            }
            drawPath(
                path = basePath,
                brush = Brush.verticalGradient(
                    colors = listOf(color, color.copy(alpha = 0.7f))
                )
            )

            // 4. Pawn Body (Cone/Stem)
            val bodyPath = Path().apply {
                moveTo(w * 0.32f, h * 0.78f)
                quadraticTo(w * 0.42f, h * 0.50f, w * 0.40f, h * 0.38f)
                lineTo(w * 0.60f, h * 0.38f)
                quadraticTo(w * 0.58f, h * 0.50f, w * 0.68f, h * 0.78f)
                close()
            }
            drawPath(
                path = bodyPath,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        color.copy(alpha = 0.6f),
                        color,
                        Color.White.copy(alpha = 0.5f),
                        color
                    )
                )
            )

            // 5. Neck Ring
            drawOval(
                brush = Brush.verticalGradient(listOf(accentColor, color)),
                topLeft = Offset(w * 0.35f, h * 0.34f),
                size = Size(w * 0.30f, h * 0.08f)
            )

            // 6. Shiny Spherical Head
            val headRadius = w * 0.22f
            val headCenter = Offset(w / 2f, h * 0.24f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White,
                        color,
                        color.copy(alpha = 0.8f)
                    ),
                    center = Offset(headCenter.x - headRadius * 0.3f, headCenter.y - headRadius * 0.3f),
                    radius = headRadius * 1.2f
                ),
                radius = headRadius,
                center = headCenter
            )

            // 7. Head Glare Specular Highlight
            drawCircle(
                color = Color.White.copy(alpha = 0.85f),
                radius = headRadius * 0.28f,
                center = Offset(headCenter.x - headRadius * 0.35f, headCenter.y - headRadius * 0.35f)
            )
        }
    }
}

/**
 * Render standard 3D Die Face (۱ تا ۶)
 */
@Composable
fun DieFace3D(
    value: Int,
    modifier: Modifier = Modifier,
    dieSize: Dp = 54.dp,
    dotColor: Color = Color(0xFF1E293B)
) {
    Box(
        modifier = modifier
            .size(dieSize)
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color.White,
                        Color(0xFFE2E8F0)
                    )
                )
            )
            .border(
                width = 2.dp,
                brush = Brush.verticalGradient(
                    listOf(Color.White, Color(0xFF94A3B8))
                ),
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(6.dp)) {
            val w = this.size.width
            val dotR = w * 0.11f

            val cx = w / 2f
            val cy = w / 2f
            val left = w * 0.25f
            val right = w * 0.75f
            val top = w * 0.25f
            val bottom = w * 0.75f

            fun drawDot(x: Float, y: Float, color: Color = dotColor) {
                drawCircle(color = color, radius = dotR, center = Offset(x, y))
            }

            val redOne = Color(0xFFDC2626)

            when (value) {
                1 -> drawDot(cx, cy, redOne)
                2 -> {
                    drawDot(left, top)
                    drawDot(right, bottom)
                }
                3 -> {
                    drawDot(left, top)
                    drawDot(cx, cy)
                    drawDot(right, bottom)
                }
                4 -> {
                    drawDot(left, top)
                    drawDot(right, top)
                    drawDot(left, bottom)
                    drawDot(right, bottom)
                }
                5 -> {
                    drawDot(left, top)
                    drawDot(right, top)
                    drawDot(cx, cy)
                    drawDot(left, bottom)
                    drawDot(right, bottom)
                }
                6 -> {
                    drawDot(left, top)
                    drawDot(right, top)
                    drawDot(left, cy)
                    drawDot(right, cy)
                    drawDot(left, bottom)
                    drawDot(right, bottom)
                }
                else -> drawDot(cx, cy)
            }
        }
    }
}

/**
 * Dedicated Dice Rolling Tray / Arena Box (کادر سینی تاس)
 */
@Composable
fun DiceTrayBox(
    diceValue: Int,
    isRolling: Boolean,
    isPlayerTurn: Boolean,
    statusText: String,
    onRollClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E1B4B)
                    )
                )
            )
            .border(
                width = 2.dp,
                brush = Brush.horizontalGradient(
                    listOf(
                        Color(0xFFD97706),
                        Color(0xFFFDE047),
                        Color(0xFFD97706)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🎲 سینی تاس بازی",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFDE047)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Dice Rolling Tray Felt Arena
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF064E3B)) // Felt green arena
                    .border(1.5.dp, Color(0xFF047857), RoundedCornerShape(14.dp))
                    .clickable(enabled = isPlayerTurn && !isRolling) {
                        onRollClick()
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(8.dp)
                ) {
                    val dieScale by animateFloatAsState(
                        targetValue = if (isRolling) 1.2f else 1.0f,
                        animationSpec = tween(150),
                        label = "dieScale"
                    )

                    Box(modifier = Modifier.scale(dieScale)) {
                        DieFace3D(value = diceValue, dieSize = 52.dp)
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = if (isRolling) "در حال چرخش تاس... 🎲" else "نتیجه تاس: $diceValue",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isPlayerTurn) Icons.Default.TouchApp else Icons.Default.Casino,
                                contentDescription = null,
                                tint = Color(0xFFA7F3D0),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isRolling) "لطفاً شکیبا باشید..." else if (isPlayerTurn) "برای پرتاب تاس اینجا کلیک کنید 👆" else "نوبت هوش مصنوعی...",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFA7F3D0)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Instruction Log
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF1E293B))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = statusText,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
