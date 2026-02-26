package com.example.student_grade_app.ui.onboarding.preview


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.student_grade_app.model.Student
import com.example.student_grade_app.ui.theme.*
import com.example.student_grade_app.viewmodel.GradeViewModel


/**
 * Preview screen — shows all students loaded from Excel before calculation.
 * The user reviews the data here and taps "Calculate Grades" to proceed.
 *
 * @param viewModel     Shared [GradeViewModel].
 * @param onCalculated  Called after grades are computed — navigates to ResultsScreen.
 * @param onBack        Navigates back to HomeScreen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewScreen(
    viewModel    : GradeViewModel,
    onCalculated : () -> Unit,
    onBack       : () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Navigate forward once calculation is done
    LaunchedEffect(uiState.calculatedStudents) {
        if (uiState.calculatedStudents.isNotEmpty()) onCalculated()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title  = { Text("Import Preview") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("← Back", color = BluePrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = White
                )
            )
        },
        containerColor = OffWhite
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            // Summary card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(14.dp),
                colors   = CardDefaults.cardColors(containerColor = BlueLight)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("👥", style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            "${uiState.students.size} Students Found",
                            style      = MaterialTheme.typography.titleLarge,
                            color      = BlueDeep,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Review the data below before calculating",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Student list
            LazyColumn(
                modifier              = Modifier.weight(1f),
                verticalArrangement   = Arrangement.spacedBy(10.dp),
                contentPadding        = PaddingValues(bottom = 16.dp)
            ) {
                items(uiState.students) { student ->
                    StudentPreviewCard(student)
                }
            }

            // Calculate button
            Button(
                onClick  = { viewModel.calculateGrades() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .height(54.dp),
                shape  = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
            ) {
                Text("🎓  Calculate Grades", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

// ── Student preview card ───────────────────────────────────────────────────

@Composable
private fun StudentPreviewCard(student: Student) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(12.dp),
        colors   = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text  = student.name,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text  = "Scores: ${student.scores.joinToString("  |  ")}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}