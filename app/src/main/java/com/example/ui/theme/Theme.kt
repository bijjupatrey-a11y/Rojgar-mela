package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = TealPrimary,
    onPrimary = TealOnPrimary,
    primaryContainer = TealOnPrimaryContainer, // inverted darker for dark mode
    onPrimaryContainer = TealPrimaryContainer,
    secondary = TealSecondary,
    secondaryContainer = TealSecondary,
    background = OnBackgroundSlate, // dark mode switches backgrounds
    surface = OnSurfaceSlate,
    onBackground = BackgroundSlate,
    onSurface = SurfaceWhite,
    surfaceVariant = OnSurfaceVariantSlate,
    onSurfaceVariant = BackgroundSlate,
    outline = OutlineSlate,
    outlineVariant = OnSurfaceVariantSlate
  )

private val LightColorScheme =
  lightColorScheme(
    primary = TealPrimary,
    onPrimary = TealOnPrimary,
    primaryContainer = TealPrimaryContainer,
    onPrimaryContainer = TealOnPrimaryContainer,
    secondary = TealSecondary,
    secondaryContainer = TealSecondaryContainer,
    onSecondary = TealOnSecondary,
    background = BackgroundSlate,
    surface = SurfaceWhite,
    onBackground = OnBackgroundSlate,
    onSurface = OnSurfaceSlate,
    surfaceVariant = SurfaceVariantSlate,
    onSurfaceVariant = OnSurfaceVariantSlate,
    outline = OutlineSlate,
    outlineVariant = OutlineVariantSlate
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Disordered dynamic color by default for Rozgar Mela to keep beautiful Vibrant Teal branding
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
