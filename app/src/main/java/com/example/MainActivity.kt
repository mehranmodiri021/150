package com.example

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.ChallengeItem
import com.example.ui.components.AboutDialog
import com.example.ui.components.ArenaBottomNavigation
import com.example.ui.components.ArenaHeader
import com.example.ui.components.ArenaScreenTab
import com.example.ui.components.DailyStreakDialog
import com.example.ui.components.SettingsDialog
import com.example.ui.components.VipDialog
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.LocalAppLanguage
import com.example.ui.localization.LocalStrings
import com.example.ui.localization.stringsForLanguage
import com.example.ui.screens.ArenaScreen
import com.example.ui.screens.ChallengesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.RewardsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.ChallengeArenaTheme
import com.example.ui.viewmodel.ArenaViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: ArenaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val settings by viewModel.settings.collectAsStateWithLifecycle()
            val language = settings.language
            val strings = remember(language) { stringsForLanguage(language) }

            CompositionLocalProvider(
                LocalAppLanguage provides language,
                LocalStrings provides strings,
                LocalLayoutDirection provides language.layoutDirection
            ) {
                ChallengeArenaTheme(arenaTheme = settings.theme) {
                    ArenaAppContent(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun ArenaAppContent(viewModel: ArenaViewModel) {
    val context = LocalContext.current
    val activity = context as? Activity

    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val challenges by viewModel.challenges.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val leaderboardType by viewModel.leaderboardType.collectAsStateWithLifecycle()
    val leaderboardEntries by viewModel.leaderboardEntries.collectAsStateWithLifecycle()
    val dailyStreaks by viewModel.dailyStreaks.collectAsStateWithLifecycle()
    val achievements by viewModel.achievements.collectAsStateWithLifecycle()
    val storeRewards by viewModel.storeRewards.collectAsStateWithLifecycle()
    val matchHistory by viewModel.matchHistory.collectAsStateWithLifecycle()
    val adState by viewModel.adState.collectAsStateWithLifecycle()
    val activeChallenge by viewModel.activeChallenge.collectAsStateWithLifecycle()
    val userNoticeMessage by viewModel.userNoticeMessage.collectAsStateWithLifecycle()

    var showSplash by remember { mutableStateOf(true) }
    var currentTab by remember { mutableStateOf(ArenaScreenTab.HOME) }

    var showVipDialog by remember { mutableStateOf(false) }
    var showStreakDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userNoticeMessage) {
        userNoticeMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearNotice()
        }
    }

    if (showSplash) {
        SplashScreen(
            onSplashFinished = { showSplash = false }
        )
    } else {
        Scaffold(
            topBar = {
                if (currentTab != ArenaScreenTab.ARENA) {
                    ArenaHeader(
                        userProfile = userProfile,
                        onVipClick = { showVipDialog = true },
                        onDailyStreakClick = { showStreakDialog = true },
                        onSettingsClick = { showSettingsDialog = true },
                        onAboutClick = { showAboutDialog = true }
                    )
                }
            },
            bottomBar = {
                if (currentTab != ArenaScreenTab.ARENA) {
                    ArenaBottomNavigation(
                        currentTab = currentTab,
                        onTabSelected = { currentTab = it }
                    )
                }
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentTab) {
                    ArenaScreenTab.HOME -> {
                        HomeScreen(
                            userProfile = userProfile,
                            featuredChallenges = challenges,
                            topLeaderboard = leaderboardEntries,
                            onStartChallenge = { challenge ->
                                viewModel.setActiveChallenge(challenge)
                                currentTab = ArenaScreenTab.ARENA
                            },
                            onNavigateToArena = {
                                viewModel.setActiveChallenge(challenges.firstOrNull())
                                currentTab = ArenaScreenTab.ARENA
                            },
                            onNavigateToChallenges = { currentTab = ArenaScreenTab.CHALLENGES },
                            onNavigateToLeaderboard = { currentTab = ArenaScreenTab.LEADERBOARD }
                        )
                    }
                    ArenaScreenTab.CHALLENGES -> {
                        ChallengesScreen(
                            challenges = challenges,
                            onStartChallenge = { challenge ->
                                viewModel.setActiveChallenge(challenge)
                                currentTab = ArenaScreenTab.ARENA
                            }
                        )
                    }
                    ArenaScreenTab.ARENA -> {
                        ArenaScreen(
                            userProfile = userProfile,
                            activeChallenge = activeChallenge,
                            onMatchFinished = { score, xp, coins, isWin ->
                                viewModel.onMatchFinished(score, xp, coins, isWin)
                            },
                            onExitArena = {
                                currentTab = ArenaScreenTab.HOME
                            }
                        )
                    }
                    ArenaScreenTab.LEADERBOARD -> {
                        LeaderboardScreen(
                            entries = leaderboardEntries,
                            currentType = leaderboardType,
                            onTypeChanged = { viewModel.setLeaderboardType(it) }
                        )
                    }
                    ArenaScreenTab.REWARDS -> {
                        RewardsScreen(
                            dailyStreaks = dailyStreaks,
                            achievements = achievements,
                            storeRewards = storeRewards,
                            adState = adState,
                            onWatchAdClick = {
                                activity?.let { viewModel.showTapsellRewardedAd(it) }
                            },
                            onClaimStreakClick = { day ->
                                viewModel.claimDailyStreak(day)
                            },
                            onBuyRewardClick = { reward ->
                                viewModel.buyStoreReward(reward)
                            }
                        )
                    }
                    ArenaScreenTab.PROFILE -> {
                        ProfileScreen(
                            userProfile = userProfile,
                            matchHistory = matchHistory,
                            onVipClick = { showVipDialog = true }
                        )
                    }
                }
            }
        }

        // Dialogs
        if (showVipDialog) {
            VipDialog(
                userProfile = userProfile,
                onPurchaseVip = { productId ->
                    viewModel.purchaseBazaarProduct(productId)
                },
                onRestorePurchases = {
                    viewModel.restoreBazaarPurchases()
                },
                onDismiss = { showVipDialog = false }
            )
        }

        if (showStreakDialog) {
            DailyStreakDialog(
                streaks = dailyStreaks,
                onClaimClick = { day ->
                    viewModel.claimDailyStreak(day)
                },
                onDismiss = { showStreakDialog = false }
            )
        }

        if (showSettingsDialog) {
            SettingsDialog(
                currentSettings = settings,
                onLanguageChanged = { viewModel.setLanguage(it) },
                onThemeChanged = { viewModel.setTheme(it) },
                onSoundToggled = { viewModel.setSoundEnabled(it) },
                onHapticToggled = { viewModel.setHapticEnabled(it) },
                onDismiss = { showSettingsDialog = false }
            )
        }

        if (showAboutDialog) {
            AboutDialog(
                onDismiss = { showAboutDialog = false }
            )
        }
    }
}
