package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.LeaderboardEntry

@Composable
fun TopPodium(
    topThree: List<LeaderboardEntry>,
    modifier: Modifier = Modifier
) {
    val first = topThree.getOrNull(0)
    val second = topThree.getOrNull(1)
    val third = topThree.getOrNull(2)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        // 2nd Place (Silver)
        if (second != null) {
            PodiumStep(
                entry = second,
                rankNumber = 2,
                podiumHeight = 90.dp,
                accentColor = Color(0xFFC0C0C0),
                modifier = Modifier.weight(1f)
            )
        }

        // 1st Place (Gold)
        if (first != null) {
            PodiumStep(
                entry = first,
                rankNumber = 1,
                podiumHeight = 120.dp,
                accentColor = Color(0xFFFFD700),
                modifier = Modifier.weight(1.1f)
            )
        }

        // 3rd Place (Bronze)
        if (third != null) {
            PodiumStep(
                entry = third,
                rankNumber = 3,
                podiumHeight = 70.dp,
                accentColor = Color(0xFFCD7F32),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun PodiumStep(
    entry: LeaderboardEntry,
    rankNumber: Int,
    podiumHeight: androidx.compose.ui.unit.Dp,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.2f))
                .border(2.dp, accentColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = entry.avatarEmoji, fontSize = 22.sp)
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = entry.username,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = "${entry.score}",
            style = MaterialTheme.typography.bodySmall,
            color = accentColor,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(podiumHeight)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(accentColor.copy(alpha = 0.4f), MaterialTheme.colorScheme.surfaceVariant)
                    )
                )
                .border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$rankNumber",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = accentColor
            )
        }
    }
}
