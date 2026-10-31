package com.haze.servicesprovider.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = ProfessionalBlue,
    onPrimary = PureWhite,
    primaryContainer = ProfessionalBlueLight,
    onPrimaryContainer = ProfessionalBlueDark,
    secondary = DarkNavyText,
    onSecondary = PureWhite,
    background = BackgroundLight,
    onBackground = DarkNavyText,
    surface = SurfaceLight,
    onSurface = DarkNavyText,
    surfaceVariant = BackgroundLight,
    onSurfaceVariant = SecondaryText,
    outline = CardBorder,
    error = ErrorRed,
    onError = PureWhite
)

private val DarkColorScheme = darkColorScheme(
    primary = ProfessionalBlue,
    onPrimary = PureWhite,
    primaryContainer = ProfessionalBlueDark,
    onPrimaryContainer = PureWhite,
    secondary = PureWhite,
    onSecondary = DarkNavyText,
    background = BackgroundDark,
    onBackground = TextDark,
    surface = SurfaceDark,
    onSurface = TextDark,
    surfaceVariant = SurfaceDark,
    onSurfaceVariant = SecondaryTextDark,
    outline = CardBorderDark,
    error = ErrorRed,
    onError = PureWhite
)

@Composable
fun HazeServiceProviderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
