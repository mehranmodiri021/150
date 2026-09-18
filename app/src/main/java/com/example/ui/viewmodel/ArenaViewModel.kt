package com.example.ui.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ads.AdState
import com.example.ads.TapsellManager
import com.example.billing.BazaarBillingManager
import com.example.billing.PurchaseResult
import com.example.config.BazaarConfig
import com.example.data.database.AppDatabase
import com.example.data.preferences.SettingsDataStore
import com.example.data.preferences.UserSettings
import com.example.data.repository.LocalChallengeRepository
import com.example.data.repository.LocalLeaderboardRepository
import com.example.data.repository.LocalRewardRepository
import com.example.data.repository.LocalUserRepository
import com.example.domain.model.AchievementItem
import com.example.domain.model.ChallengeItem
import com.example.domain.model.DailyStreakReward
import com.example.domain.model.LeaderboardEntry
import com.example.domain.model.LeaderboardType
import com.example.domain.model.MatchHistoryItem
import com.example.domain.model.RewardItem
import com.example.domain.model.UserProfile
import com.example.ui.localization.AppLanguage
import com.example.ui.theme.ArenaTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ArenaViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val userRepository = LocalUserRepository(database)
    private val challengeRepository = LocalChallengeRepository(database)
    private val leaderboardRepository = LocalLeaderboardRepository()
    private val rewardRepository = LocalRewardRepository(database)
    private val settingsDataStore = SettingsDataStore(application)

    val billingManager = BazaarBillingManager.getInstance(application)
    val tapsellManager = TapsellManager.getInstance(application)

    // UI States
    val userProfile: StateFlow<UserProfile> = userRepository.getUserProfile()
        .stateIn(viewModelScope, SharingStarted.Eagerly, UserProfile())

    val challenges: StateFlow<List<ChallengeItem>> = challengeRepository.getChallenges()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val settings: StateFlow<UserSettings> = settingsDataStore.settingsFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, UserSettings())

    private val _leaderboardType = MutableStateFlow(LeaderboardType.GLOBAL)
    val leaderboardType: StateFlow<LeaderboardType> = _leaderboardType.asStateFlow()

    private val _leaderboardEntries = MutableStateFlow<List<LeaderboardEntry>>(emptyList())
    val leaderboardEntries: StateFlow<List<LeaderboardEntry>> = _leaderboardEntries.asStateFlow()

    val dailyStreaks: StateFlow<List<DailyStreakReward>> = rewardRepository.getDailyStreakRewards()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val achievements: StateFlow<List<AchievementItem>> = rewardRepository.getAchievements()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val storeRewards: StateFlow<List<RewardItem>> = rewardRepository.getStoreRewards()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val matchHistory: StateFlow<List<MatchHistoryItem>> = userRepository.getMatchHistory()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val adState: StateFlow<AdState> = tapsellManager.adState

    private val _activeChallenge = MutableStateFlow<ChallengeItem?>(null)
    val activeChallenge: StateFlow<ChallengeItem?> = _activeChallenge.asStateFlow()

    private val _userNoticeMessage = MutableStateFlow<String?>(null)
    val userNoticeMessage: StateFlow<String?> = _userNoticeMessage.asStateFlow()

    init {
        tapsellManager.initializeTapsell()
        refreshLeaderboard(LeaderboardType.GLOBAL)
        // Automatic restore check on startup to sync active purchases with Cafe Bazaar
        viewModelScope.launch {
            restoreBazaarPurchases()
        }
    }

    fun setLeaderboardType(type: LeaderboardType) {
        _leaderboardType.value = type
        refreshLeaderboard(type)
    }

    private fun refreshLeaderboard(type: LeaderboardType) {
        viewModelScope.launch {
            leaderboardRepository.getLeaderboard(type).collect { list ->
                _leaderboardEntries.value = list
            }
        }
    }

    fun setActiveChallenge(challenge: ChallengeItem?) {
        _activeChallenge.value = challenge
    }

    fun onMatchFinished(score: Int, xpEarned: Int, coinsEarned: Int, isWin: Boolean) {
        viewModelScope.launch {
            userRepository.addXpAndCoins(xpEarned, coinsEarned)

            val currentChallenge = _activeChallenge.value
            val challengeTitleEn = currentChallenge?.titleEn ?: "Speed Duel"
            val challengeTitleFa = currentChallenge?.titleFa ?: "دوئل سرعتی"

            if (currentChallenge != null) {
                challengeRepository.markChallengeCompletedWithScore(currentChallenge.id, score)
            }

            val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
            val dateStr = sdf.format(Date())

            userRepository.recordMatchResult(
                MatchHistoryItem(
                    id = "match_${System.currentTimeMillis()}",
                    challengeTitleEn = challengeTitleEn,
                    challengeTitleFa = challengeTitleFa,
                    score = score,
                    isWin = isWin,
                    dateFormatted = dateStr,
                    xpEarned = xpEarned,
                    coinsEarned = coinsEarned
                )
            )

            // Update achievements
            if (isWin) {
                rewardRepository.updateAchievementProgress("FIRST_WIN", 1)
                rewardRepository.updateAchievementProgress("10_WINS", 1)
            }
            rewardRepository.updateAchievementProgress("100_CHALLENGES", 1)
        }
    }

    fun claimDailyStreak(dayNumber: Int) {
        viewModelScope.launch {
            val reward = rewardRepository.claimDailyStreak(dayNumber)
            if (reward != null) {
                userRepository.addXpAndCoins(reward.rewardXp, reward.rewardCoins)
                userRepository.addTickets(reward.rewardTickets)
                _userNoticeMessage.value = "جایزه روز $dayNumber با موفقیت دریافت شد!"
            }
        }
    }

    fun buyStoreReward(reward: RewardItem) {
        viewModelScope.launch {
            val success = userRepository.spendCoins(reward.coinCost)
            if (success) {
                when (reward.id) {
                    "rew_tickets_5" -> userRepository.addTickets(5)
                    "rew_chest_bronze" -> userRepository.addXpAndCoins(100, 250)
                    "rew_chest_gold" -> {
                        userRepository.addXpAndCoins(500, 1500)
                        userRepository.addTickets(3)
                    }
                }
                _userNoticeMessage.value = "خرید ${reward.titleFa} با موفقیت انجام شد!"
            } else {
                _userNoticeMessage.value = "سکه کافی برای خرید این آیتم ندارید!"
            }
        }
    }

    /**
     * Shows real rewarded video ad via Tapsell Plus SDK.
     * Guaranteed: Only awards coins/XP when onRewarded callback is received.
     */
    fun showTapsellRewardedAd(activity: Activity) {
        tapsellManager.showRewardedAd(
            activity = activity,
            onAdStarted = {
                // Ad started
            },
            onRewardGranted = { coins, xp, tickets ->
                viewModelScope.launch {
                    userRepository.addXpAndCoins(xp, coins)
                    userRepository.addTickets(tickets)
                    _userNoticeMessage.value = "پاداش تماشای تبلیغ تپسل ($coins سکه و $xp XP) به حسابتان افزوده شد!"
                }
            },
            onError = { errMsg ->
                _userNoticeMessage.value = errMsg
            }
        )
    }

    /**
     * Initiates real purchase flow with Cafe Bazaar In-App Billing.
     */
    fun purchaseBazaarProduct(productId: String) {
        viewModelScope.launch {
            billingManager.launchPurchase(productId) { result ->
                when (result) {
                    is PurchaseResult.Success -> {
                        viewModelScope.launch {
                            val days = if (productId == BazaarConfig.PRODUCT_ID_VIP_YEARLY) 365 else 30
                            userRepository.activateVipWithToken(days, result.purchaseToken)
                            _userNoticeMessage.value = "اشتراک VIP با موفقیت فعال شد!"
                        }
                    }
                    is PurchaseResult.Error -> {
                        _userNoticeMessage.value = result.userFriendlyMessage
                    }
                    is PurchaseResult.Canceled -> {
                        _userNoticeMessage.value = "خرید توسط کاربر لغو گردید."
                    }
                }
            }
        }
    }

    /**
     * Queries Cafe Bazaar for existing owned purchases and syncs VIP state.
     */
    fun restoreBazaarPurchases() {
        viewModelScope.launch {
            billingManager.restorePurchases { success, purchases, error ->
                if (success) {
                    if (purchases.contains(BazaarConfig.PRODUCT_ID_VIP_MONTHLY) ||
                        purchases.contains(BazaarConfig.PRODUCT_ID_VIP_YEARLY)
                    ) {
                        viewModelScope.launch {
                            userRepository.activateVip(30)
                            _userNoticeMessage.value = "اشتراک VIP فعال شما از کافه‌بازار بازیابی شد."
                        }
                    } else {
                        _userNoticeMessage.value = "هیچ اشتراک فعال خریداری‌شده‌ای در حساب کافه‌بازار یافت نشد."
                    }
                } else if (error != null) {
                    _userNoticeMessage.value = error
                }
            }
        }
    }

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            settingsDataStore.setLanguage(language)
        }
    }

    fun setTheme(theme: ArenaTheme) {
        viewModelScope.launch {
            settingsDataStore.setTheme(theme)
        }
    }

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setSoundEnabled(enabled)
        }
    }

    fun setHapticEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setHapticEnabled(enabled)
        }
    }

    fun clearNotice() {
        _userNoticeMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        billingManager.release()
    }
}
