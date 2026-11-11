package com.batteryiq.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Green40,
    onPrimary = Color.White,
    primaryContainer = Green90,
    onPrimaryContainer = Green10,
    secondary = Teal40,
    onSecondary = Color.White,
    secondaryContainer = Teal90,
    onSecondaryContainer = Teal30,
    tertiary = Green60,
    onTertiary = Color.White,
    tertiaryContainer = Green95,
    onTertiaryContainer = Green20,
    error = BatteryDanger,
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Gray99,
    onBackground = Gray10,
    surface = Color.White,
    onSurface = Gray10,
    surfaceVariant = Gray95,
    onSurfaceVariant = Gray40,
    outline = Gray60,
    outlineVariant = Gray80,
    inverseSurface = Gray20,
    inverseOnSurface = Gray95,
    inversePrimary = Green80,
    surfaceTint = Green40
)

private val DarkColorScheme = darkColorScheme(
    primary = Green80,
    onPrimary = Green20,
    primaryContainer = Green30,
    onPrimaryContainer = Green90,
    secondary = Teal80,
    onSecondary = Teal30,
    secondaryContainer = Teal30,
    onSecondaryContainer = Teal90,
    tertiary = Green90,
    onTertiary = Green10,
    tertiaryContainer = Green30,
    onTertiaryContainer = Green95,
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Gray10,
    onBackground = Gray90,
    surface = Gray20,
    onSurface = Gray90,
    surfaceVariant = Gray30,
    onSurfaceVariant = Gray80,
    outline = Gray60,
    outlineVariant = Gray40,
    inverseSurface = Gray90,
    inverseOnSurface = Gray20,
    inversePrimary = Green40,
    surfaceTint = Green80
)

@Composable
fun BatteryIQTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = BatteryIQTypography,
        content = content
    )
}
