package com.example.student_grade_app.ui.theme

import androidx.compose.ui.graphics.Color

// ── Primary Brand Colors ───────────────────────────────────────────────────
val BluePrimary    = Color(0xFF1A73E8)   // Main blue — buttons, highlights
val BlueDeep       = Color(0xFF0D47A1)   // Darker blue — headers, nav bar
val BlueLight      = Color(0xFFE8F0FE)   // Soft blue — card backgrounds

// ── Accent ────────────────────────────────────────────────────────────────
val AccentGreen    = Color(0xFF00C853)   // Pass / success states
val AccentRed      = Color(0xFFD50000)   // Fail / error states
val AccentAmber    = Color(0xFFFFAB00)   // Warning / average states

// ── Neutrals ──────────────────────────────────────────────────────────────
val White          = Color(0xFFFFFFFF)
val OffWhite       = Color(0xFFF8F9FA)   // Screen backgrounds
val GrayLight      = Color(0xFFE0E0E0)   // Dividers, borders
val GrayMid        = Color(0xFF9E9E9E)   // Hints, subtitles
val GrayDark       = Color(0xFF424242)   // Body text
val Black          = Color(0xFF000000)

// ── Grade Colors (used on result cards) ───────────────────────────────────
val GradeA         = Color(0xFF00C853)   // A  → green
val GradeB         = Color(0xFF1A73E8)   // B  → blue
val GradeC         = Color(0xFFFFAB00)   // C  → amber
val GradeD         = Color(0xFFFF6D00)   // D  → orange
val GradeF         = Color(0xFFD50000)   // F  → red