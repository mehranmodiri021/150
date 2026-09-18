package com.example.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.example.config.TapsellConfig
import ir.tapsell.plus.AdRequestCallback
import ir.tapsell.plus.AdShowListener
import ir.tapsell.plus.TapsellPlus
import ir.tapsell.plus.TapsellPlusInitListener
import ir.tapsell.plus.model.AdNetworkError
import ir.tapsell.plus.model.AdNetworks
import ir.tapsell.plus.model.TapsellPlusAdModel
import ir.tapsell.plus.model.TapsellPlusErrorModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicBoolean

sealed class AdState {
    data object Idle : AdState()
    data object Loading : AdState()
    data class Ready(val responseId: String, val zoneId: String) : AdState()
    data class Showing(val zoneId: String) : AdState()
    data class Completed(val rewardVerified: Boolean, val coinsEarned: Int, val xpEarned: Int) : AdState()
    data class Error(val errorMessage: String) : AdState()
}

/**
 * TapsellManager manages official Tapsell Plus SDK integration for Rewarded Video Ads.
 * Handles the complete real lifecycle:
 * [Initialize] -> [Request Ad with ZoneId] -> [Receive ResponseId] -> [Show Ad]
 * -> [onRewarded callback] -> [Deliver single verified reward]
 *
 * Fully protected against:
 * - Fake delays or artificial completions
 * - Multiple clicks / concurrent requests
 * - Duplicate reward callbacks per ad impression
 * - Dismissals before ad completion
 */
class TapsellManager private constructor(private val appContext: Context) {
    private val _adState = MutableStateFlow<AdState>(AdState.Idle)
    val adState: StateFlow<AdState> = _adState.asStateFlow()

    private var isInitialized = false
    private var isAdInProgress = AtomicBoolean(false)
    private var currentResponseId: String? = null

    companion object {
        private const val TAG = "TapsellManager"

        @Volatile
        private var INSTANCE: TapsellManager? = null

        fun getInstance(context: Context): TapsellManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: TapsellManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    /**
     * Real initialization of Tapsell Plus SDK.
     */
    fun initializeTapsell() {
        val appKey = TapsellConfig.TAPSELL_APP_KEY
        if (appKey == TapsellConfig.STATUS_NOT_CONFIGURED || appKey.isBlank()) {
            Log.w(TAG, "Tapsell App Key not configured in .env / Secrets panel. Initialization deferred.")
            return
        }

        try {
            TapsellPlus.initialize(appContext, appKey, object : TapsellPlusInitListener {
                override fun onInitializeSuccess(adNetworks: AdNetworks?) {
                    Log.d(TAG, "Tapsell Plus SDK initialized successfully.")
                    isInitialized = true
                }

                override fun onInitializeFailed(adNetworks: AdNetworks?, error: AdNetworkError?) {
                    Log.e(TAG, "Tapsell Plus SDK initialization failed: ${error?.errorMessage}")
                    isInitialized = false
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Exception initializing Tapsell Plus SDK: ${e.message}")
            isInitialized = false
        }
    }

    /**
     * Real request and display of Rewarded Video Ad via Tapsell Plus SDK.
     * Guaranteed:
     * - Only grants reward if onRewarded callback is received from Tapsell Plus SDK.
     * - Exactly one reward per impression (using AtomicBoolean rewardClaimed flag).
     * - Ignores rapid re-clicks while ad is active.
     */
    fun showRewardedAd(
        activity: Activity,
        onAdStarted: () -> Unit = {},
        onRewardGranted: (coins: Int, xp: Int, tickets: Int) -> Unit,
        onError: (String) -> Unit = {}
    ) {
        // Prevent concurrent requests or multiple clicks
        if (!isAdInProgress.compareAndSet(false, true)) {
            Log.w(TAG, "Ad request dropped: An ad is already in progress.")
            return
        }

        val zoneId = TapsellConfig.TAPSELL_REWARDED_ZONE_ID
        if (zoneId == TapsellConfig.STATUS_NOT_CONFIGURED || zoneId.isBlank()) {
            isAdInProgress.set(false)
            val msg = "شناسه جایگاه ویدیوی پاداش‌دار (Zone ID) تپسل در فایل .env یا پنل سکرت‌ها تنظیم نشده است."
            _adState.value = AdState.Error(msg)
            onError(msg)
            return
        }

        if (!isInitialized) {
            initializeTapsell()
        }

        _adState.value = AdState.Loading

        try {
            // Step 1: Real Request Rewarded Ad from Tapsell Plus
            TapsellPlus.requestRewardedVideoAd(activity, zoneId, object : AdRequestCallback() {
                override fun response(adModel: TapsellPlusAdModel?) {
                    val responseId = adModel?.responseId
                    if (responseId.isNullOrBlank()) {
                        isAdInProgress.set(false)
                        val errorMsg = "تبلیغ آماده نمایش نشد (پاسخ معتبر دریافت نشد)."
                        _adState.value = AdState.Error(errorMsg)
                        onError(errorMsg)
                        return
                    }

                    currentResponseId = responseId
                    _adState.value = AdState.Ready(responseId, zoneId)

                    // Step 2: Real Show Rewarded Ad
                    val rewardClaimed = AtomicBoolean(false)

                    _adState.value = AdState.Showing(zoneId)
                    onAdStarted()

                    TapsellPlus.showRewardedVideoAd(activity, responseId, object : AdShowListener() {
                        override fun onOpened(adModel: TapsellPlusAdModel?) {
                            Log.d(TAG, "Tapsell rewarded ad opened: ${adModel?.responseId}")
                        }

                        override fun onClosed(adModel: TapsellPlusAdModel?) {
                            Log.d(TAG, "Tapsell rewarded ad closed.")
                            isAdInProgress.set(false)
                            if (!rewardClaimed.get()) {
                                // Ad was dismissed without reaching reward completion
                                _adState.value = AdState.Error("تبلیغ پیش از اتمام بسته شد. پاداشی تعلق نگرفت.")
                                onError("تبلیغ قبل از اتمام بسته شد.")
                            }
                        }

                        override fun onRewarded(adModel: TapsellPlusAdModel?) {
                            Log.d(TAG, "Tapsell rewarded ad completed and rewarded!")
                            // Ensure strictly ONE reward issued per impression
                            if (rewardClaimed.compareAndSet(false, true)) {
                                val coins = TapsellConfig.REWARDED_AD_COIN_BONUS
                                val xp = TapsellConfig.REWARDED_AD_XP_BONUS
                                val tickets = TapsellConfig.REWARDED_AD_TICKET_BONUS

                                _adState.value = AdState.Completed(
                                    rewardVerified = true,
                                    coinsEarned = coins,
                                    xpEarned = xp
                                )

                                onRewardGranted(coins, xp, tickets)
                            }
                        }

                        override fun onError(errorModel: TapsellPlusErrorModel?) {
                            isAdInProgress.set(false)
                            val errorMsg = errorModel?.errorMessage ?: "خطای ناشناخته در پخش تبلیغ تپسل"
                            Log.e(TAG, "Tapsell rewarded ad show error: $errorMsg")
                            _adState.value = AdState.Error(errorMsg)
                            onError(errorMsg)
                        }
                    })
                }

                override fun error(message: String?) {
                    isAdInProgress.set(false)
                    val errorMsg = message ?: "خطا در درخواست تبلیغ از تپسل."
                    Log.e(TAG, "Tapsell request error: $errorMsg")
                    _adState.value = AdState.Error(errorMsg)
                    onError(errorMsg)
                }
            })
        } catch (e: Exception) {
            isAdInProgress.set(false)
            val errorMsg = "استثنا در برقراری ارتباط با شبکه تبلیغاتی تپسل: ${e.message}"
            Log.e(TAG, errorMsg)
            _adState.value = AdState.Error(errorMsg)
            onError(errorMsg)
        }
    }

    fun resetState() {
        _adState.value = AdState.Idle
        isAdInProgress.set(false)
    }
}
