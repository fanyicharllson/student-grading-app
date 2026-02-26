package com.example.student_grade_app.ui.onboarding


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.student_grade_app.ui.theme.*

// ── Onboarding slide content ───────────────────────────────────────────────

private data class OnboardingPage(
    val emoji    : String,
    val title    : String,
    val subtitle : String
)

private val pages = listOf(
    OnboardingPage(
        emoji    = "📊",
        title    = "Import Student Scores",
        subtitle = "Pick any Excel (.xlsx) file containing your students' names and scores — we'll handle the rest."
    ),
    OnboardingPage(
        emoji    = "🎓",
        title    = "Instant Grade Calculation",
        subtitle = "Our Grade Calculator class automatically computes averages, assigns letter grades, and flags pass/fail."
    ),
    OnboardingPage(
        emoji    = "📁",
        title    = "Export Results",
        subtitle = "Get a clean Results sheet written back into your Excel file — ready to share or submit."
    )
)

// ── Screen ─────────────────────────────────────────────────────────────────

/**
 * Three-slide onboarding screen shown on first launch.
 *
 * @param onFinish Called when the user taps "Get Started" on the last slide
 *                 or "Skip" on any slide.
 */
@Composable
fun OnboardingScreen(onFinish: () -> Unit) {

    var currentPage by remember { mutableIntStateOf(0) }
    val page = pages[currentPage]
    val isLastPage = currentPage == pages.lastIndex

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OffWhite)
    ) {

        // ── Skip button (top right) ────────────────────────────────────────
        TextButton(
            onClick  = onFinish,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Text("Skip", color = GrayMid, fontSize = 14.sp)
        }

        // ── Slide content (centered) ───────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            verticalArrangement   = Arrangement.Center,
            horizontalAlignment   = Alignment.CenterHorizontally
        ) {

            // Big emoji illustration
            AnimatedVisibility(visible = true, enter = fadeIn(), exit = fadeOut()) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(BlueLight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = page.emoji, fontSize = 60.sp)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Title
            Text(
                text      = page.title,
                style     = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Subtitle
            Text(
                text      = page.subtitle,
                style     = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color     = GrayMid
            )
        }

        // ── Bottom controls ────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp, start = 32.dp, end = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Dot indicators
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                pages.indices.forEach { index ->
                    val isActive = index == currentPage
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(if (isActive) 24.dp else 8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(if (isActive) BluePrimary else GrayLight)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Next / Get Started button
            Button(
                onClick = {
                    if (isLastPage) onFinish()
                    else currentPage++
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape  = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
            ) {
                Text(
                    text  = if (isLastPage) "Get Started" else "Next",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}