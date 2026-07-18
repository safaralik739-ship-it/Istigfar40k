package com.istighfar.app40k.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = EmeraldGreen,
    onPrimary = PureWhite,
    primaryContainer = EmeraldSoft,
    onPrimaryContainer = PureWhite,
    secondary = GoldAccent,
    onSecondary = TextPrimaryDark,
    background = OffWhite,
    onBackground = TextPrimaryDark,
    surface = CardLight,
    onSurface = TextPrimaryDark,
    surfaceVariant = OffWhite,
    onSurfaceVariant = TextSecondaryDark,
    error = ErrorRed,
    outline = TextSecondaryDark
)

private val DarkColors = darkColorScheme(
    primary = EmeraldSoft,
    onPrimary = PureWhite,
    primaryContainer = EmeraldLight,
    onPrimaryContainer = PureWhite,
    secondary = GoldAccent,
    onSecondary = TextPrimaryDark,
    background = DeepForestGreen,
    onBackground = TextPrimaryLight,
    surface = CardDark,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceDark,
    onSurfaceVariant = TextSecondaryLight,
    error = ErrorRed,
    outline = TextSecondaryLight
)

@Composable
fun Istighfar40KTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
