package com.example.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.localization.LocalStrings

enum class ArenaScreenTab(val tag: String) {
    HOME("nav_home"),
    CHALLENGES("nav_challenges"),
    ARENA("nav_arena"),
    LEADERBOARD("nav_leaderboard"),
    REWARDS("nav_rewards"),
    PROFILE("nav_profile")
}

@Composable
fun ArenaBottomNavigation(
    currentTab: ArenaScreenTab,
    onTabSelected: (ArenaScreenTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalStrings.current

    val navItems = listOf(
        NavigationItemData(ArenaScreenTab.HOME, strings.navHome, Icons.Default.Home),
        NavigationItemData(ArenaScreenTab.CHALLENGES, strings.navChallenges, Icons.Default.SportsEsports),
        NavigationItemData(ArenaScreenTab.ARENA, strings.navArena, Icons.Default.Bolt),
        NavigationItemData(ArenaScreenTab.LEADERBOARD, strings.navLeaderboard, Icons.Default.Leaderboard),
        NavigationItemData(ArenaScreenTab.REWARDS, strings.navRewards, Icons.Default.CardGiftcard),
        NavigationItemData(ArenaScreenTab.PROFILE, strings.navProfile, Icons.Default.Person)
    )

    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        navItems.forEach { item ->
            val isSelected = currentTab == item.tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(item.tab) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 9.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                ),
                modifier = Modifier.testTag(item.tab.tag)
            )
        }
    }
}

private data class NavigationItemData(
    val tab: ArenaScreenTab,
    val label: String,
    val icon: ImageVector
)
