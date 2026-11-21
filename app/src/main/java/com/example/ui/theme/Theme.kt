package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = VoltAmber500,
    onPrimary = Obsidian950,
    primaryContainer = VoltAmber900,
    onPrimaryContainer = VoltAmber100,
    secondary = GridEmerald500,
    onSecondary = Obsidian950,
    secondaryContainer = GridEmerald900,
    onSecondaryContainer = GridEmerald100,
    tertiary = Slate300,
    onTertiary = Obsidian950,
    background = Obsidian950,
    onBackground = StudioWhite,
    surface = Obsidian900,
    onSurface = StudioWhite,
    surfaceVariant = Obsidian800,
    onSurfaceVariant = Slate300,
    surfaceContainerLowest = Obsidian950,
    surfaceContainerLow = Obsidian900,
    surfaceContainer = Obsidian800,
    surfaceContainerHigh = ColorTokens.DarkCardElevated,
    outline = Slate700,
    outlineVariant = Slate700,
    error = AlertRed500,
    onError = PureWhite,
    errorContainer = AlertRed900,
    onErrorContainer = AlertRed100
)

private val LightColorScheme = lightColorScheme(
    primary = Obsidian900,
    onPrimary = PureWhite,
    primaryContainer = VoltAmber100,
    onPrimaryContainer = VoltAmber900,
    secondary = GridEmerald600,
    onSecondary = PureWhite,
    secondaryContainer = GridEmerald100,
    onSecondaryContainer = GridEmerald900,
    tertiary = VoltAmber600,
    onTertiary = PureWhite,
    background = StudioWhite,
    onBackground = Obsidian900,
    surface = PureWhite,
    onSurface = Obsidian900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate500,
    surfaceContainerLowest = PureWhite,
    surfaceContainerLow = StudioWhite,
    surfaceContainer = Slate100,
    surfaceContainerHigh = Slate200,
    outline = Slate300,
    outlineVariant = Slate200,
    error = AlertRed500,
    onError = PureWhite,
    errorContainer = AlertRed100,
    onErrorContainer = AlertRed900
)

private object ColorTokens {
    val DarkCardElevated = androidx.compose.ui.graphics.Color(0xFF243247)
}

val DTCorpShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun DTCorpTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = DTCorpTypography,
        shapes = DTCorpShapes,
        content = content
    )
}
