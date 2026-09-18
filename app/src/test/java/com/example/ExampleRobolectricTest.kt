package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.config.EconomyConfig
import com.example.domain.model.RankTier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read app name string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Arena Clash", appName)
  }

  @Test
  fun `economy config multipliers and anti cheat rules valid`() {
    assertTrue(EconomyConfig.VIP_XP_MULTIPLIER > 1.0f)
    assertTrue(EconomyConfig.VIP_COIN_MULTIPLIER > 1.0f)
    assertTrue(EconomyConfig.MAX_POSSIBLE_SCORE_30S > 0)
    assertTrue(EconomyConfig.MIN_TARGET_REACTION_MS >= 50)
  }

  @Test
  fun `rank tiers progression order valid`() {
    val bronze = RankTier.fromXp(100)
    assertEquals(RankTier.BRONZE, bronze)

    val silver = RankTier.fromXp(600)
    assertEquals(RankTier.SILVER, silver)

    val legend = RankTier.fromXp(40000)
    assertEquals(RankTier.LEGEND, legend)
  }
}
