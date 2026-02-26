package com.example.student_grade_app.ui.theme


import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Using system default — swap with a custom font if desired
val AppTypography = Typography(

    // Large headings — onboarding titles, screen headers
    headlineLarge = TextStyle(
        fontFamily  = FontFamily.Default,
        fontWeight  = FontWeight.Bold,
        fontSize    = 30.sp,
        lineHeight  = 38.sp,
        color       = BlueDeep
    ),

    // Section titles inside screens
    headlineMedium = TextStyle(
        fontFamily  = FontFamily.Default,
        fontWeight  = FontWeight.SemiBold,
        fontSize    = 22.sp,
        lineHeight  = 30.sp,
        color       = GrayDark
    ),

    // Card titles, student names
    titleLarge = TextStyle(
        fontFamily  = FontFamily.Default,
        fontWeight  = FontWeight.SemiBold,
        fontSize    = 18.sp,
        lineHeight  = 26.sp,
        color       = GrayDark
    ),

    // Body text — descriptions, scores
    bodyLarge = TextStyle(
        fontFamily  = FontFamily.Default,
        fontWeight  = FontWeight.Normal,
        fontSize    = 16.sp,
        lineHeight  = 24.sp,
        color       = GrayDark
    ),

    // Subtitles, hints
    bodyMedium = TextStyle(
        fontFamily  = FontFamily.Default,
        fontWeight  = FontWeight.Normal,
        fontSize    = 14.sp,
        lineHeight  = 20.sp,
        color       = GrayMid
    ),

    // Labels on buttons
    labelLarge = TextStyle(
        fontFamily  = FontFamily.Default,
        fontWeight  = FontWeight.Bold,
        fontSize    = 15.sp,
        letterSpacing = 0.5.sp,
        color       = White
    )
)