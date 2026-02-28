package com.example.student_grade_app.ui.theme

import androidx.compose.ui.graphics.Color

// ── Primary Brand Colors ───────────────────────────────────────────────────
val BluePrimary = Color(0xFF4E9BFF)   // Lighter blue — pops on dark bg
val BlueDeep = Color(0xFF1A73E8)   // Buttons, active elements
val BlueLight = Color(0xFF1E2D45)   // Dark blue-tinted card surfaces

// ── Accent ────────────────────────────────────────────────────────────────
val AccentGreen = Color(0xFF00E676)   // Pass / success states
val AccentRed = Color(0xFFFF5252)   // Fail / error states
val AccentAmber = Color(0xFFFFD740)   // Warning / average states

// ── Dark Theme Neutrals ───────────────────────────────────────────────────
val White = Color(0xFFFFFFFF)
val OffWhite = Color(0xFFE8EAED)   // Primary text on dark bg
val DarkBg = Color(0xFF0F1117)   // Main screen background
val DarkSurface = Color(0xFF1A1D27)   // Card / surface background
val DarkElevated = Color(0xFF222536)   // Slightly elevated cards
val GrayLight = Color(0xFF3A3D4A)   // Dividers, borders
val GrayMid = Color(0xFF8A8FA8)   // Hints, subtitles
val GrayDark = Color(0xFFE8EAED)   // Body text (light on dark)
val Black = Color(0xFF000000)

// ── Grade Colors (used on result cards) ───────────────────────────────────
val GradeA = Color(0xFF00E676)   // A  → green
val GradeB = Color(0xFF4E9BFF)   // B  → blue
val GradeC = Color(0xFFFFD740)   // C  → amber
val GradeD = Color(0xFFFF9100)   // D  → orange
val GradeF = Color(0xFFFF5252)   // F  → red