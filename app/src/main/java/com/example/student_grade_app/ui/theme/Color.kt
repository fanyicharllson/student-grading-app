package com.example.student_grade_app.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Modern, Professional "Deep Ocean" Branding
 */

// ── Primary Brand & Gradients ──────────────────────────────────────────────
val BrandPrimary = Color(0xFF6366F1)    // Indigo 500 (Main Action)
val BrandSecondary = Color(0xFF8B5CF6)  // Violet 500 (Gradients)
val BrandAccent = Color(0xFF10B981)     // Emerald 500 (Success/Pass)

// ── Dark Mode - Deep Ocean ────────────────────────────────────────────────
val BgDark = Color(0xFF0F172A)          // Slate 900 (Background)
val SurfaceDark = Color(0xFF1E293B)     // Slate 800 (Cards)
val SurfaceLighter = Color(0xFF334155)  // Slate 700 (Elevated/Borders)

// ── Text & Content ────────────────────────────────────────────────────────
val TextPrimary = Color(0xFFF8FAFC)     // Slate 50 (Headings)
val TextSecondary = Color(0xFF94A3B8)   // Slate 400 (Body/Subtitles)
val TextMuted = Color(0xFF64748B)      // Slate 500 (Captions)

// ── Status & Feedback ─────────────────────────────────────────────────────
val StatusPass = Color(0xFF10B981)      // Emerald (Pass)
val StatusFail = Color(0xFFEF4444)      // Rose (Fail)
val StatusWarning = Color(0xFFF59E0B)   // Amber (Warning)

// ── Result Grade Colors (Tailwind Palette) ────────────────────────────────
val GradeA = Color(0xFF10B981)   // Emerald 500
val GradeB = Color(0xFF3B82F6)   // Blue 500
val GradeC = Color(0xFFF59E0B)   // Amber 500
val GradeD = Color(0xFFF97316)   // Orange 500
val GradeF = Color(0xFFEF4444)   // Rose 500

// ── Legacy Compatibility (Old names mapped to new theme) ──────────────────
val BluePrimary = BrandPrimary
val BlueDeep = BrandSecondary
val BlueLight = SurfaceDark
val AccentGreen = StatusPass
val AccentRed = StatusFail
val AccentAmber = StatusWarning
val DarkBg = BgDark
val DarkSurface = SurfaceDark
val OffWhite = TextPrimary
val GrayMid = TextSecondary
val White = Color.White
val GrayLight = SurfaceLighter
