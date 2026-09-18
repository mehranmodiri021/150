package com.example.config

import com.example.BuildConfig

/**
 * Cafe Bazaar In-App Billing Configuration.
 * Centralized Bazaar Product IDs and Keys.
 * Securely reads BAZAAR_PUBLIC_KEY from BuildConfig (.env / Secrets panel).
 */
object BazaarConfig {
    const val STATUS_NOT_CONFIGURED = "NOT CONFIGURED"

    val BAZAAR_PUBLIC_KEY: String
        get() = try {
            val key = BuildConfig.BAZAAR_PUBLIC_KEY
            if (key.isNotBlank() && key != STATUS_NOT_CONFIGURED) key else STATUS_NOT_CONFIGURED
        } catch (_: Throwable) {
            STATUS_NOT_CONFIGURED
        }

    // Preserve original Product IDs from project
    const val PRODUCT_ID_VIP_MONTHLY = "challenge_arena_vip_monthly"
    const val PRODUCT_ID_VIP_YEARLY = "challenge_arena_vip_yearly"
    const val PRODUCT_ID_COINS_PACK_SMALL = "challenge_arena_coins_1000"
    const val PRODUCT_ID_COINS_PACK_LARGE = "challenge_arena_coins_5000"
    const val PRODUCT_ID_TICKETS_PACK = "challenge_arena_tickets_10"

    val isConfigured: Boolean
        get() = BAZAAR_PUBLIC_KEY != STATUS_NOT_CONFIGURED && BAZAAR_PUBLIC_KEY.isNotBlank()
}
