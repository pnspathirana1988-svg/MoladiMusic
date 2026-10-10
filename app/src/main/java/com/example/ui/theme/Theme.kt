package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = SpotifyGreen,
  onPrimary = Color.Black,
  primaryContainer = Color(0xFF1E3A28),
  onPrimaryContainer = SpotifyGreenBright,
  secondary = ProPurple,
  onSecondary = Color.White,
  tertiary = UltraGold,
  onTertiary = Color.Black,
  background = DarkBackground,
  onBackground = TextPrimary,
  surface = DarkSurface,
  onSurface = TextPrimary,
  surfaceVariant = DarkSurfaceCard,
  onSurfaceVariant = TextSecondary,
  outline = DarkBorder
)

private val LightColorScheme = lightColorScheme(
  primary = SpotifyGreen,
  onPrimary = Color.White,
  primaryContainer = Color(0xFFD4F5DD),
  onPrimaryContainer = Color(0xFF0F5A29),
  secondary = ProPurple,
  onSecondary = Color.White,
  tertiary = UltraGoldDark,
  onTertiary = Color.White,
  background = LightBackground,
  onBackground = Color(0xFF1A1A1A),
  surface = LightSurface,
  onSurface = Color(0xFF1A1A1A),
  surfaceVariant = LightSurfaceCard,
  onSurfaceVariant = Color(0xFF4B5563),
  outline = LightBorder
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
