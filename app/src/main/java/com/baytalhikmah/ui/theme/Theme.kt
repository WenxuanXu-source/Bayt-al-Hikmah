package com.baytalhikmah.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Density
import androidx.core.view.WindowCompat
import com.baytalhikmah.data.model.ThemeMode

@Immutable
data class BaytPalette(
    val success: Color,
    val successContainer: Color,
    val warning: Color,
    val warningContainer: Color,
    val cardBorder: Color,
    val highlight: Color,
    val surfaceLow: Color,
    val outlineStrong: Color
)

val LightPalette = BaytPalette(
    success = LightSuccess,
    successContainer = LightSuccessContainer,
    warning = LightWarning,
    warningContainer = LightWarningContainer,
    cardBorder = LightCardBorder,
    highlight = LightHighlight,
    surfaceLow = LightSurfaceLow,
    outlineStrong = LightOutlineStrong
)

val DarkPalette = BaytPalette(
    success = DarkSuccess,
    successContainer = DarkSuccessContainer,
    warning = DarkWarning,
    warningContainer = DarkWarningContainer,
    cardBorder = DarkCardBorder,
    highlight = DarkHighlight,
    surfaceLow = DarkSurfaceLow,
    outlineStrong = DarkOutlineStrong
)

val LocalBaytPalette = staticCompositionLocalOf { LightPalette }

private val LightScheme = lightColorScheme(
    primary = LightAccent,
    onPrimary = LightOnAccent,
    primaryContainer = LightAccentContainer,
    onPrimaryContainer = LightOnAccentContainer,
    secondary = Color(0xFF4A6A60),
    onSecondary = Color(0xFFF5F7F1),
    secondaryContainer = Color(0xFFE4EDE7),
    onSecondaryContainer = Color(0xFF1E3831),
    tertiary = Color(0xFF7A5B3A),
    onTertiary = Color(0xFFFBF6EC),
    background = LightBackground,
    onBackground = LightInk,
    surface = LightSurface,
    onSurface = LightInk,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightSecondaryInk,
    outline = LightOutline,
    outlineVariant = Color(0xFFEDE7DB),
    error = LightError,
    onError = Color(0xFFFFFFFF),
    errorContainer = LightErrorContainer,
    onErrorContainer = LightOnErrorContainer
)

private val DarkScheme = darkColorScheme(
    primary = DarkAccent,
    onPrimary = DarkOnAccent,
    primaryContainer = DarkAccentContainer,
    onPrimaryContainer = DarkOnAccentContainer,
    secondary = Color(0xFF9FBFAF),
    onSecondary = Color(0xFF13241D),
    secondaryContainer = Color(0xFF2A3A33),
    onSecondaryContainer = Color(0xFFD7E6DD),
    tertiary = Color(0xFFC9A87F),
    onTertiary = Color(0xFF2A1E0F),
    background = DarkBackground,
    onBackground = DarkInk,
    surface = DarkSurface,
    onSurface = DarkInk,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkSecondaryInk,
    outline = DarkOutline,
    outlineVariant = Color(0xFF252829),
    error = DarkError,
    onError = Color(0xFF3A1713),
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkOnErrorContainer
)

@Composable
fun BaytTheme(
    themeMode: ThemeMode,
    fontScale: Float,
    content: @Composable () -> Unit
) {
    val dark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val palette = if (dark) DarkPalette else LightPalette
    val density = LocalDensity.current
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? android.app.Activity)?.window ?: return@SideEffect
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !dark
                isAppearanceLightNavigationBars = !dark
            }
        }
    }

    CompositionLocalProvider(
        LocalDensity provides Density(density.density, fontScale),
        LocalBaytPalette provides palette
    ) {
        MaterialTheme(
            colorScheme = if (dark) DarkScheme else LightScheme,
            typography = BaytTypography,
            content = content
        )
    }
}
