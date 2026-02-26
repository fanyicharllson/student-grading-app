package com.example.student_grade_app.ui.onboarding.home


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.student_grade_app.ui.theme.*
import com.example.student_grade_app.viewmodel.GradeViewModel


/**
 * Home screen — entry point after onboarding.
 * The user picks an Excel file here and we kick off the import.
 *
 * @param viewModel  Shared [GradeViewModel].
 * @param onImported Called once students are successfully loaded — navigates to PreviewScreen.
 */
@Composable
fun HomeScreen(
    viewModel  : GradeViewModel,
    onImported : () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // Navigate forward as soon as students are loaded
    LaunchedEffect(uiState.students) {
        if (uiState.students.isNotEmpty()) onImported()
    }

    // File picker — only allows .xlsx files
    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.importExcel(context, it) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OffWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            verticalArrangement   = Arrangement.Center,
            horizontalAlignment   = Alignment.CenterHorizontally
        ) {

            // Icon circle
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(BlueLight),
                contentAlignment = Alignment.Center
            ) {
                Text("📂", fontSize = 52.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text  = "Grade Calculator",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text      = "Import an Excel file with student scores\nto get started.",
                style     = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color     = GrayMid
            )

            Spacer(modifier = Modifier.height(48.dp))

            // ── Import button ──────────────────────────────────────────────
            if (uiState.isLoading) {
                CircularProgressIndicator(color = BluePrimary)
            } else {
                Button(
                    onClick  = { filePicker.launch("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape  = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                ) {
                    Text("📊  Import Excel File", style = MaterialTheme.typography.labelLarge)
                }
            }

            // ── Error message ──────────────────────────────────────────────
            uiState.errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(10.dp),
                    colors   = CardDefaults.cardColors(containerColor = AccentRed.copy(alpha = 0.1f))
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

        // ── Format hint at bottom ──────────────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            shape  = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = BlueLight)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text  = "📋  Expected Excel Format",
                    style = MaterialTheme.typography.titleLarge,
                    color = BlueDeep
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text  = "Row 1: Header (Name | Score 1 | Score 2 | ...)\nRow 2+: One student per row",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}