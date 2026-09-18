package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.config.EconomyConfig
import com.example.domain.model.ChallengeDifficulty
import com.example.domain.model.ChallengeItem
import com.example.domain.model.ChallengeType
import com.example.domain.model.UserProfile
import com.example.ui.localization.LocalStrings
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun ArenaScreen(
    userProfile: UserProfile,
    activeChallenge: ChallengeItem?,
    onMatchFinished: (score: Int, xpEarned: Int, coinsEarned: Int, isWin: Boolean) -> Unit,
    onExitArena: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalStrings.current

    val duration = activeChallenge?.durationSeconds ?: 30
    val challengeTitle = activeChallenge?.titleFa ?: "دوئل سرعتی آرنا"

    var score by remember { mutableIntStateOf(0) }
    var combo by remember { mutableIntStateOf(1) }
    var hits by remember { mutableIntStateOf(0) }
    var misses by remember { mutableIntStateOf(0) }
    var secondsLeft by remember { mutableIntStateOf(duration) }
    var isRunning by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }

    // Target state for Reflex / Precision challenge
    var targetX by remember { mutableFloatStateOf(0.5f) }
    var targetY by remember { mutableFloatStateOf(0.5f) }
    var isGoldenTarget by remember { mutableStateOf(false) }
    var lastTargetSpawnTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    // Mental Math Rush state
    var mathNum1 by remember { mutableIntStateOf(7) }
    var mathNum2 by remember { mutableIntStateOf(5) }
    var mathCorrectAnswer by remember { mutableIntStateOf(12) }
    var mathChoices by remember { mutableStateOf(listOf(10, 12, 14, 15)) }

    fun spawnNewMathProblem() {
        val n1 = Random.nextInt(5, 30)
        val n2 = Random.nextInt(3, 20)
        val isAdd = Random.nextBoolean()
        val correct = if (isAdd) n1 + n2 else (n1 - n2).coerceAtLeast(1)
        mathNum1 = n1
        mathNum2 = n2
        mathCorrectAnswer = correct

        val options = mutableSetOf(correct)
        while (options.size < 4) {
            val delta = Random.nextInt(-5, 6)
            if (delta != 0) {
                options.add((correct + delta).coerceAtLeast(1))
            }
        }
        mathChoices = options.shuffled()
    }

    fun spawnNewTarget() {
        targetX = Random.nextFloat().coerceIn(0.15f, 0.85f)
        targetY = Random.nextFloat().coerceIn(0.15f, 0.85f)
        isGoldenTarget = Random.nextFloat() < 0.25f // 25% chance for golden target
        lastTargetSpawnTime = System.currentTimeMillis()
    }

    // Timer countdown
    LaunchedEffect(isRunning, isPaused) {
        if (!isRunning || isPaused) return@LaunchedEffect
        while (secondsLeft > 0 && isRunning && !isPaused) {
            delay(1000)
            secondsLeft--
        }
        if (secondsLeft <= 0 && isRunning) {
            isRunning = false
            isGameOver = true

            // Anti-cheat verification
            val isLegitimate = score <= EconomyConfig.MAX_POSSIBLE_SCORE_30S
            val finalScore = if (isLegitimate) score else 0

            val baseWin = finalScore >= EconomyConfig.BASE_WIN_SCORE_THRESHOLD
            val xpMultiplier = if (userProfile.isVip) EconomyConfig.VIP_XP_MULTIPLIER else 1.0f
            val coinMultiplier = if (userProfile.isVip) EconomyConfig.VIP_COIN_MULTIPLIER else 1.0f

            val earnedXp = ((activeChallenge?.xpReward ?: 120) * xpMultiplier).toInt()
            val earnedCoins = ((activeChallenge?.coinReward ?: 80) * coinMultiplier).toInt()

            onMatchFinished(finalScore, earnedXp, earnedCoins, baseWin)
        }
    }

    // Auto start game
    LaunchedEffect(Unit) {
        spawnNewTarget()
        spawnNewMathProblem()
        isRunning = true
    }

    val isMathType = activeChallenge?.type == ChallengeType.DAILY

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF070B14))
            .padding(16.dp)
            .testTag("screen_arena")
    ) {
        // Top Arena Bar (Score, Timer, Pause, Exit)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = challengeTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "${strings.comboMultiplier}: ${combo}X",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = if (combo >= 4) Color(0xFFFFD700) else Color(0xFF00E5FF)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Timer badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (secondsLeft <= 5) Color(0xFFFF1744).copy(alpha = 0.25f) else Color(0xFF00E5FF).copy(alpha = 0.15f))
                        .border(
                            1.dp,
                            if (secondsLeft <= 5) Color(0xFFFF1744) else Color(0xFF00E5FF),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "⏱️ $secondsLeft ${strings.durationSec}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (secondsLeft <= 5) Color(0xFFFF1744) else Color(0xFF00E5FF)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = { isPaused = !isPaused },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = strings.pause,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(
                    onClick = { showExitDialog = true },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = strings.quitMatch,
                        tint = Color(0xFFFF1744),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Time progress indicator
        LinearProgressIndicator(
            progress = { (secondsLeft.toFloat() / duration.coerceAtLeast(1)).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = if (secondsLeft <= 5) Color(0xFFFF1744) else Color(0xFF00E5FF),
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Current score display
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$score",
                fontSize = 44.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Arena Canvas / Gameplay Zone
        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                .testTag("arena_gameplay_canvas")
        ) {
            val canvasWidth = maxWidth
            val canvasHeight = maxHeight

            if (isMathType) {
                // Mental Math Rush Mode
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = strings.solveProblem,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "$mathNum1 + $mathNum2 = ?",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF00E5FF)
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            mathChoices.take(2).forEach { choice ->
                                Button(
                                    onClick = {
                                        if (choice == mathCorrectAnswer) {
                                            score += EconomyConfig.MATH_CORRECT_POINTS * combo
                                            combo = (combo + 1).coerceAtMost(EconomyConfig.MAX_COMBO_MULTIPLIER)
                                            hits++
                                        } else {
                                            combo = 1
                                            misses++
                                        }
                                        spawnNewMathProblem()
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.surface,
                                        contentColor = Color.White
                                    )
                                ) {
                                    Text(text = "$choice", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            mathChoices.drop(2).take(2).forEach { choice ->
                                Button(
                                    onClick = {
                                        if (choice == mathCorrectAnswer) {
                                            score += EconomyConfig.MATH_CORRECT_POINTS * combo
                                            combo = (combo + 1).coerceAtMost(EconomyConfig.MAX_COMBO_MULTIPLIER)
                                            hits++
                                        } else {
                                            combo = 1
                                            misses++
                                        }
                                        spawnNewMathProblem()
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.surface,
                                        contentColor = Color.White
                                    )
                                ) {
                                    Text(text = "$choice", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            } else {
                // Reflex & Target Tapping Mode
                val targetSize = 64.dp
                val xOffset = (canvasWidth - targetSize) * targetX
                val yOffset = (canvasHeight - targetSize) * targetY

                Box(
                    modifier = Modifier
                        .offset(x = xOffset, y = yOffset)
                        .size(targetSize)
                        .clip(CircleShape)
                        .background(
                            if (isGoldenTarget) Brush.radialGradient(listOf(Color(0xFFFFD700), Color(0xFFFF8F00)))
                            else Brush.radialGradient(listOf(Color(0xFF00E5FF), Color(0xFF0077B6)))
                        )
                        .border(
                            2.dp,
                            if (isGoldenTarget) Color.White else Color(0xFF00E5FF),
                            CircleShape
                        )
                        .clickable {
                            val reactionMs = System.currentTimeMillis() - lastTargetSpawnTime
                            // Anti-cheat reaction check
                            if (reactionMs >= EconomyConfig.MIN_TARGET_REACTION_MS) {
                                val basePoints = if (isGoldenTarget) EconomyConfig.GOLDEN_TARGET_TAP_POINTS else EconomyConfig.BASE_TARGET_TAP_POINTS
                                score += basePoints * combo
                                combo = (combo + 1).coerceAtMost(EconomyConfig.MAX_COMBO_MULTIPLIER)
                                hits++
                            }
                            spawnNewTarget()
                        }
                        .testTag("arena_target_node"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isGoldenTarget) "⭐" else "⚡",
                        fontSize = 24.sp
                    )
                }
            }
        }
    }

    // Exit Confirmation Dialog
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text(text = strings.quitMatch, fontWeight = FontWeight.Bold) },
            text = { Text(text = strings.confirmExitMatch) },
            confirmButton = {
                Button(
                    onClick = {
                        showExitDialog = false
                        isRunning = false
                        onExitArena()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF1744))
                ) {
                    Text(text = strings.exitMatch)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text(text = strings.stayInMatch)
                }
            }
        )
    }

    // Game Over Results Dialog
    if (isGameOver) {
        AlertDialog(
            onDismissRequest = { /* Require button click */ },
            title = {
                Text(
                    text = if (score >= EconomyConfig.BASE_WIN_SCORE_THRESHOLD) strings.victory else strings.defeat,
                    fontWeight = FontWeight.Black,
                    color = if (score >= EconomyConfig.BASE_WIN_SCORE_THRESHOLD) Color(0xFF00E676) else Color(0xFFFF1744),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .border(2.dp, if (score >= EconomyConfig.BASE_WIN_SCORE_THRESHOLD) Color(0xFFFFD700) else Color(0xFF00E5FF), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_arena_logo),
                            contentDescription = "Arena Trophy",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "${strings.finalScore}: $score",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "ضربات موفق: $hits • خطاها: $misses",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "+${activeChallenge?.xpReward ?: 120} XP • +${activeChallenge?.coinReward ?: 80} 🪙",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD700)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        isGameOver = false
                        onExitArena()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(text = strings.returnToLobby, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
