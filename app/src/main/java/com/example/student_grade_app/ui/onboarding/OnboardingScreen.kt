package com.example.student_grade_app.ui.onboarding


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.student_grade_app.ui.theme.*

private data class OnboardingPage(
    val title: String,
    val subtitle: String,
    val tag: String
)

private val pages = listOf(
    OnboardingPage(
        title = "Import Student Scores",
        subtitle = "Pick any Excel (.xlsx) file containing your students' names and scores — we handle the rest.",
        tag = "XLSX"
    ),
    OnboardingPage(
        title = "Instant Grade Calculation",
        subtitle = "Our Grade Calculator automatically computes averages, assigns letter grades, and flags pass or fail.",
        tag = "A – F"
    ),
    OnboardingPage(
        title = "Export Results",
        subtitle = "A clean Results sheet is written back into your Excel file, ready to share or submit.",
        tag = "EXPORT"
    )
)

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {

    var currentPage by remember { mutableIntStateOf(0) }
    val page = pages[currentPage]
    val isLastPage = currentPage == pages.lastIndex

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {

        TextButton(
            onClick = onFinish,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(end = 16.dp, top = 8.dp)
        ) {
            Text("Skip", color = GrayMid)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 36.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(width = 180.dp, height = 120.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(BlueLight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = page.tag,
                    style = MaterialTheme.typography.headlineMedium,
                    color = BluePrimary
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = page.title,
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                color = OffWhite
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = page.subtitle,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = GrayMid
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 40.dp, start = 32.dp, end = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                pages.indices.forEach { index ->
                    val isActive = index == currentPage
                    Box(
                        modifier = Modifier
                            .height(6.dp)
                            .width(if (isActive) 28.dp else 6.dp)
                            .clip(RoundedCornerShape(50))
                            .background(if (isActive) BluePrimary else GrayLight)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = { if (isLastPage) onFinish() else currentPage++ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
            ) {
                Text(
                    text = if (isLastPage) "Get Started" else "Next",
                    style = MaterialTheme.typography.labelLarge,
                    color = DarkBg
                )
            }
        }
    }
}