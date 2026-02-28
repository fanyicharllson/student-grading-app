package com.example.student_grade_app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// ── Dark Color Scheme ──────────────────────────────────────────────────────
private val AppColorScheme = darkColorScheme(
    primary = BluePrimary,
    onPrimary = DarkBg,
    primaryContainer = BlueLight,
    onPrimaryContainer = OffWhite,

    secondary = BlueDeep,
    onSecondary = White,

    background = DarkBg,
    onBackground = GrayDark,

    surface = DarkSurface,
    onSurface = GrayDark,

    surfaceVariant = DarkElevated,
    onSurfaceVariant = GrayMid,

    error = AccentRed,
    onError = White
)

/**
 * Root theme wrapper.
 * Wrap your entire app in [GradeCalculatorTheme] inside MainActivity.
 */
@Composable
fun GradeCalculatorTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = AppTypography,
        content = content
    )
}