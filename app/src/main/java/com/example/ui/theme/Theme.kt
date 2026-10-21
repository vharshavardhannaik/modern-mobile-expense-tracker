package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val SpendlyLightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    inversePrimary = PrimaryFixedDim,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    tertiary = Tertiary,
    onTertiary = OnTertiary,
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = OnTertiaryContainer,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceContainerHighest,
    onSurfaceVariant = OnSurfaceVariant,
    outline = Outline,
    outlineVariant = OutlineVariant,
    error = Error,
    onError = OnError,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer,
    inverseSurface = InverseSurface,
    inverseOnSurface = InverseOnSurface,
)

private val SpendlyDarkColorScheme = darkColorScheme(
    primary = PrimaryFixedDim,
    onPrimary = OnPrimaryFixed,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = SecondaryFixed,
    onSecondary = OnSecondaryFixed,
    secondaryContainer = Secondary,
    onSecondaryContainer = SecondaryFixed,
    tertiary = TertiaryFixedDim,
    onTertiary = TertiaryContainer,
    background = InverseSurface,
    onBackground = InverseOnSurface,
    surface = InverseSurface,
    onSurface = InverseOnSurface,
    surfaceVariant = OnSurfaceVariant,
    onSurfaceVariant = SurfaceContainerHigh,
    outline = Outline,
    outlineVariant = OutlineVariant,
    error = ErrorContainer,
    onError = OnErrorContainer
)

@Composable
fun SpendlyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) SpendlyDarkColorScheme else SpendlyLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
