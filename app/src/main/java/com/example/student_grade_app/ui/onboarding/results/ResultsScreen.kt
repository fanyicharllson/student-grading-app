package com.example.student_grade_app.ui.onboarding.results


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.student_grade_app.model.Student
import com.example.student_grade_app.ui.theme.*
import com.example.student_grade_app.viewmodel.GradeViewModel


/**
 * Results screen — displays calculated grades for all students
 * and allows the user to export results back to Excel.
 *
 * @param viewModel  Shared [GradeViewModel].
 * @param onBack     Navigate back to PreviewScreen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    viewModel : GradeViewModel,
    onBack    : () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // Show a snackbar when export succeeds
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.exportSuccess) {
        if (uiState.exportSuccess) {
            snackbarHostState.showSnackbar("Results exported successfully ✅")
            viewModel.clearExportSuccess()
        }
    }

    // File save picker
    val savePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    ) { uri: Uri? ->
        uri?.let { viewModel.exportResults(context, it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title  = { Text("Results") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("← Back", color = BluePrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
            )
        },
        snackbarHost    = { SnackbarHost(snackbarHostState) },
        containerColor  = OffWhite
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Summary stats card
            SummaryCard(students = uiState.calculatedStudents)

            Spacer(modifier = Modifier.height(12.dp))

            // Results list
            LazyColumn(
                modifier            = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding      = PaddingValues(bottom = 16.dp)
            ) {
                items(uiState.calculatedStudents) { student ->
                    ResultCard(student)
                }
            }

            // Export button
            if (uiState.isExporting) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BluePrimary)
                }
            } else {
                Button(
                    onClick  = { savePicker.launch("GradeResults.xlsx") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .height(54.dp),
                    shape  = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BlueDeep)
                ) {
                    Text("📁  Export to Excel", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

// ── Summary stats card ─────────────────────────────────────────────────────

@Composable
private fun SummaryCard(students: List<Student>) {
    val passCount = students.count { it.passed == true }
    val failCount = students.size - passCount

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(14.dp),
        colors   = CardDefaults.cardColors(containerColor = BlueDeep)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(label = "Total",  value = "${students.size}", color = White)
            StatItem(label = "Pass",   value = "$passCount",       color = AccentGreen)
            StatItem(label = "Fail",   value = "$failCount",       color = AccentRed)
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = color)
        Text(label, style = MaterialTheme.typography.bodyMedium, color = White.copy(alpha = 0.7f))
    }
}

// ── Individual result card ─────────────────────────────────────────────────

@Composable
private fun ResultCard(student: Student) {
    val gradeColor = when (student.grade) {
        "A"  -> GradeA
        "B"  -> GradeB
        "C"  -> GradeC
        "D"  -> GradeD
        else -> GradeF
    }
    val statusColor = if (student.passed == true) AccentGreen else AccentRed

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Grade badge
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(gradeColor.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = student.grade ?: "-",
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color      = gradeColor
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Name + average
            Column(modifier = Modifier.weight(1f)) {
                Text(student.name, style = MaterialTheme.typography.titleLarge)
                Text(
                    "Average: ${"%.1f".format(student.average ?: 0.0)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Pass / Fail chip
            Surface(
                shape = RoundedCornerShape(50),
                color = statusColor.copy(alpha = 0.12f)
            ) {
                Text(
                    text     = if (student.passed == true) "PASS" else "FAIL",
                    color    = statusColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}