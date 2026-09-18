package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.ChallengeItem
import com.example.domain.model.ChallengeType
import com.example.ui.components.ChallengeCard
import com.example.ui.localization.LocalStrings

@Composable
fun ChallengesScreen(
    challenges: List<ChallengeItem>,
    onStartChallenge: (ChallengeItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalStrings.current
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }

    val categories = listOf(
        Pair(strings.tabAll, null),
        Pair(strings.tabQuick, ChallengeType.QUICK),
        Pair(strings.tabDaily, ChallengeType.DAILY),
        Pair(strings.tabTimeAttack, ChallengeType.TIME_ATTACK),
        Pair(strings.tabAccuracy, ChallengeType.ACCURACY),
        Pair(strings.tabEndless, ChallengeType.ENDLESS),
        Pair(strings.tabTournaments, ChallengeType.TOURNAMENT),
        Pair(strings.tabSpecial, ChallengeType.SPECIAL_EVENT)
    )

    val filteredChallenges = remember(challenges, selectedCategoryIndex) {
        val selectedType = categories[selectedCategoryIndex].second
        if (selectedType == null) {
            challenges
        } else {
            challenges.filter { it.type == selectedType }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("screen_challenges")
    ) {
        Text(
            text = strings.challengesTitle,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // Category Filter Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedCategoryIndex,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedCategoryIndex]),
                    color = MaterialTheme.colorScheme.primary
                )
            },
            divider = {}
        ) {
            categories.forEachIndexed { index, pair ->
                val isSelected = selectedCategoryIndex == index
                Tab(
                    selected = isSelected,
                    onClick = { selectedCategoryIndex = index },
                    text = {
                        Text(
                            text = pair.first,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Challenges List
        if (filteredChallenges.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text(
                    text = strings.emptyChallenges,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredChallenges) { challenge ->
                    ChallengeCard(
                        challenge = challenge,
                        onStartClick = onStartChallenge
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
