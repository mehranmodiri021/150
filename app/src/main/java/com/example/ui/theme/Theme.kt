package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

fun getArenaColorScheme(theme: ArenaTheme) = when (theme) {
    ArenaTheme.DEFAULT -> darkColorScheme(
        primary = CyanNeon,
        onPrimary = Color.Black,
        secondary = NeonPurple,
        onSecondary = Color.White,
        tertiary = GoldYellow,
        background = DarkBackground,
        surface = DarkSurface,
        surfaceVariant = DarkCard,
        onBackground = TextPrimary,
        onSurface = TextPrimary,
        onSurfaceVariant = TextSecondary,
        outline = BorderDark
    )
    ArenaTheme.MIDNIGHT -> darkColorScheme(
        primary = MidnightAccent,
        onPrimary = Color.White,
        secondary = CyanNeon,
        background = MidnightBg,
        surface = MidnightSurface,
        surfaceVariant = Color(0xFF131D33),
        onBackground = Color.White,
        onSurface = Color.White,
        outline = Color(0xFF1E2D4A)
    )
    ArenaTheme.OCEAN -> darkColorScheme(
        primary = OceanAccent,
        onPrimary = Color.Black,
        secondary = Color(0xFF0077B6),
        background = OceanBg,
        surface = OceanSurface,
        surfaceVariant = Color(0xFF0A3652),
        onBackground = Color.White,
        onSurface = Color.White,
        outline = Color(0xFF144D73)
    )
    ArenaTheme.PURPLE -> darkColorScheme(
        primary = PurpleAccent,
        onPrimary = Color.White,
        secondary = CyanNeon,
        background = PurpleBg,
        surface = PurpleSurface,
        surfaceVariant = Color(0xFF2E175B),
        onBackground = Color.White,
        onSurface = Color.White,
        outline = Color(0xFF452482)
    )
    ArenaTheme.SUNSET -> darkColorScheme(
        primary = SunsetAccent,
        onPrimary = Color.White,
        secondary = GoldYellow,
        background = SunsetBg,
        surface = SunsetSurface,
        surfaceVariant = Color(0xFF42210B),
        onBackground = Color.White,
        onSurface = Color.White,
        outline = Color(0xFF5E3113)
    )
    ArenaTheme.EMERALD -> darkColorScheme(
        primary = EmeraldAccent,
        onPrimary = Color.Black,
        secondary = CyanNeon,
        background = EmeraldBg,
        surface = EmeraldSurface,
        surfaceVariant = Color(0xFF0C3D27),
        onBackground = Color.White,
        onSurface = Color.White,
        outline = Color(0xFF145738)
    )
}

@Composable
fun ChallengeArenaTheme(
    arenaTheme: ArenaTheme = ArenaTheme.DEFAULT,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = getArenaColorScheme(arenaTheme)
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    ChallengeArenaTheme(
        arenaTheme = ArenaTheme.DEFAULT,
        darkTheme = darkTheme,
        content = content
    )
}
