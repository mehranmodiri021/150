package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.RankTier
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.LocalAppLanguage

@Composable
fun RankBadge(
    rank: RankTier,
    modifier: Modifier = Modifier,
    showTitle: Boolean = true
) {
    val language = LocalAppLanguage.current
    val title = if (language == AppLanguage.PERSIAN) rank.titleFa else rank.titleEn

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(rank.primaryColor.copy(alpha = 0.15f))
            .border(1.dp, rank.primaryColor.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = rank.iconSymbol, fontSize = 14.sp)
            if (showTitle) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = title,
                    color = rank.primaryColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
