package com.example.config

import com.example.BuildConfig

/**
 * Tapsell Advertisement Network Configuration.
 * Centralized Zone IDs and keys for real Tapsell Plus integration.
 * Securely reads credentials from BuildConfig (injected via .env / Secrets panel)
 * with zero hardcoded secret data.
 */
object TapsellConfig {
    const val STATUS_NOT_CONFIGURED = "NOT CONFIGURED"

    val TAPSELL_APP_KEY: String
        get() = try {
            val key = BuildConfig.TAPSELL_APP_KEY
            if (key.isNotBlank() && key != STATUS_NOT_CONFIGURED) key else STATUS_NOT_CONFIGURED
        } catch (_: Throwable) {
            STATUS_NOT_CONFIGURED
        }

    val TAPSELL_REWARDED_ZONE_ID: String
        get() = try {
            val zone = BuildConfig.TAPSELL_REWARDED_ZONE_ID
            if (zone.isNotBlank() && zone != STATUS_NOT_CONFIGURED) zone else STATUS_NOT_CONFIGURED
        } catch (_: Throwable) {
            STATUS_NOT_CONFIGURED
        }

    val TAPSELL_BANNER_ZONE_ID: String
        get() = try {
            val zone = BuildConfig.TAPSELL_BANNER_ZONE_ID
            if (zone.isNotBlank() && zone != STATUS_NOT_CONFIGURED) zone else STATUS_NOT_CONFIGURED
        } catch (_: Throwable) {
            STATUS_NOT_CONFIGURED
        }

    val TAPSELL_INTERSTITIAL_ZONE_ID: String
        get() = try {
            val zone = BuildConfig.TAPSELL_INTERSTITIAL_ZONE_ID
            if (zone.isNotBlank() && zone != STATUS_NOT_CONFIGURED) zone else STATUS_NOT_CONFIGURED
        } catch (_: Throwable) {
            STATUS_NOT_CONFIGURED
        }

    const val REWARDED_AD_COIN_BONUS = 150
    const val REWARDED_AD_XP_BONUS = 50
    const val REWARDED_AD_TICKET_BONUS = 1

    val isConfigured: Boolean
        get() = TAPSELL_APP_KEY != STATUS_NOT_CONFIGURED && TAPSELL_APP_KEY.isNotBlank() &&
                TAPSELL_REWARDED_ZONE_ID != STATUS_NOT_CONFIGURED && TAPSELL_REWARDED_ZONE_ID.isNotBlank()
}
