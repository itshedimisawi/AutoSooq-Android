package com.example.googlelogindemo.presentation.theme

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@SuppressLint("ConflictingOnColor")
private val LightThemeColors = lightColors(
    primary = AccentColor,
    primaryVariant = AccentColor,
    onPrimary = Color.White,
    secondary = AccentColor,
    secondaryVariant = AccentColor,
    onSecondary = Color.White,
    error = RedErrorLight,
    onError = RedErrorLight,
    background = ColorBackgroundLight,
    onBackground = Color.Black,
    surface = ColorSurfaceLight,
    onSurface = Color.Black,
)

private val DarkThemeColors = darkColors(
    primary = AccentColor,
    primaryVariant = AccentColor,
    onPrimary = Color.White,
    secondary = AccentColor,
    secondaryVariant = AccentColor,
    onSecondary = Color.White,
    error = RedErrorDark,
    onError = Color.White,
    background = ColorBackgroundDark,
    onBackground = Color.White,
    surface = ColorSurfaceDark,
    onSurface = Color.White,

    )

@Composable
fun AppTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colors = if (isSystemInDarkTheme()) DarkThemeColors else LightThemeColors,
        typography = OpenSansTypography,
    ) {
        content()
    }
}

