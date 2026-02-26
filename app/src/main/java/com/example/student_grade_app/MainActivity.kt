package com.example.student_grade_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.student_grade_app.ui.onboarding.OnboardingScreen
import com.example.student_grade_app.ui.onboarding.home.HomeScreen
import com.example.student_grade_app.ui.onboarding.preview.PreviewScreen
import com.example.student_grade_app.ui.onboarding.results.ResultsScreen
import com.example.student_grade_app.ui.theme.GradeCalculatorTheme
import com.example.student_grade_app.viewmodel.GradeViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GradeCalculatorTheme {
                GradeCalculatorApp()
            }
        }
    }
}

// ── Navigation ─────────────────────────────────────────────────────────────

/** All screens in the app. */
private enum class Screen {
    ONBOARDING,
    HOME,
    PREVIEW,
    RESULTS
}

@Composable
private fun GradeCalculatorApp() {

    // Single ViewModel shared across all screens
    val viewModel: GradeViewModel = viewModel()

    // Current screen state — starts at onboarding
    var currentScreen by remember { mutableStateOf(Screen.ONBOARDING) }

    when (currentScreen) {

        Screen.ONBOARDING -> OnboardingScreen(
            onFinish = { currentScreen = Screen.HOME }
        )

        Screen.HOME -> HomeScreen(
            viewModel = viewModel,
            onImported = { currentScreen = Screen.PREVIEW }
        )

        Screen.PREVIEW -> PreviewScreen(
            viewModel = viewModel,
            onCalculated = { currentScreen = Screen.RESULTS },
            onBack = { currentScreen = Screen.HOME }
        )

        Screen.RESULTS -> ResultsScreen(
            viewModel = viewModel,
            onBack = { currentScreen = Screen.PREVIEW }
        )
    }
}
