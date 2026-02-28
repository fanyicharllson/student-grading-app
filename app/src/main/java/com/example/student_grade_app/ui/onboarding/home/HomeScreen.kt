package com.example.student_grade_app.ui.onboarding.home

import com.example.student_grade_app.ui.theme.*
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.student_grade_app.viewmodel.GradeViewModel

@Composable
fun HomeScreen(
    viewModel  : GradeViewModel,
    onImported : () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.students) {
        if (uiState.students.isNotEmpty()) onImported()
    }

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.importExcel(context, it) }
    }

    // ── Root column — top to bottom, no overlap ────────────────────────────
    // Instead of Box with absolute positioning, we use a single Column
    // where the top section (logo + text + button) takes all available space
    // via Modifier.weight(1f), and the hint card sits naturally below it.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {

        // ── Top section: logo, title, button ──────────────────────────────
        // weight(1f) means "take all remaining space after the hint card"
        // so the hint card is never covered
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 28.dp),
            verticalArrangement   = Arrangement.Center,
            horizontalAlignment   = Alignment.CenterHorizontally
        ) {

            // Logo box
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(BlueLight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = "GC",
                    fontSize   = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color      = BluePrimary
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text      = "Grade Calculator",
                style     = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                color     = OffWhite
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text      = "Import an Excel file with student\nscores to get started.",
                style     = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color     = GrayMid
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Import button or loading spinner
            if (uiState.isLoading) {
                CircularProgressIndicator(color = BluePrimary)
            } else {
                Button(
                    onClick = {
                        filePicker.launch(
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape  = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                ) {
                    Text(
                        "Import Excel File",
                        style = MaterialTheme.typography.labelLarge,
                        color = DarkBg
                    )
                }
            }

            // Error message
            uiState.errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(10.dp),
                    colors   = CardDefaults.cardColors(
                        containerColor = AccentRed.copy(alpha = 0.15f)
                    )
                ) {
                    Text(
                        text     = error,
                        color    = AccentRed,
                        modifier = Modifier.padding(12.dp),
                        style    = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // ── Hint card — always at the bottom, never overlaps ──────────────
        ExcelFormatHintCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

// ── Excel format hint — visual mini table ─────────────────────────────────

@Composable
private fun ExcelFormatHintCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text       = "Expected Excel Format",
                    style      = MaterialTheme.typography.titleLarge,
                    color      = BluePrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = BlueLight
                ) {
                    Text(
                        text       = ".xlsx",
                        fontSize   = 11.sp,
                        color      = BluePrimary,
                        fontWeight = FontWeight.Bold,
                        modifier   = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Mini table
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkElevated)
            ) {
                TableRow(
                    cells    = listOf("Name", "Score 1", "Score 2", "Score 3"),
                    isHeader = true
                )
                HorizontalDivider(color = GrayLight, thickness = 0.5.dp)
                TableRow(cells = listOf("Alice", "85", "90", "78"))
                HorizontalDivider(color = GrayLight.copy(alpha = 0.4f), thickness = 0.5.dp)
                TableRow(cells = listOf("Bob", "60", "55", "70"))
                HorizontalDivider(color = GrayLight.copy(alpha = 0.4f), thickness = 0.5.dp)
                TableRow(cells = listOf("Carol", "92", "88", "95"))
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text  = "• Row 1 must be a header row   • Column A = student name   • Columns B+ = scores",
                style = MaterialTheme.typography.bodyMedium,
                color = GrayMid,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun TableRow(
    cells    : List<String>,
    isHeader : Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isHeader) BluePrimary.copy(alpha = 0.12f)
                else          DarkElevated
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        cells.forEach { cell ->
            Text(
                text       = cell,
                modifier   = Modifier.weight(1f),
                fontSize   = 12.sp,
                fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
                color      = if (isHeader) BluePrimary else OffWhite.copy(alpha = 0.8f)
            )
        }
    }
}