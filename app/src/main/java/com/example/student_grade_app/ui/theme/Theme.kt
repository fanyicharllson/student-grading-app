package com.example.student_grade_app.ui.theme


import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// ── Light Color Scheme ─────────────────────────────────────────────────────
private val AppColorScheme = lightColorScheme(
    primary = BluePrimary,
    onPrimary = White,
    primaryContainer = BlueLight,
    onPrimaryContainer = BlueDeep,

    secondary = BlueDeep,
    onSecondary = White,

    background = OffWhite,
    onBackground = GrayDark,

    surface = White,
    onSurface = GrayDark,

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