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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewScreen(
    viewModel: GradeViewModel,
    onCalculated: () -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.calculatedStudents) {
        if (uiState.calculatedStudents.isNotEmpty()) onCalculated()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Import Preview", color = OffWhite) },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("Back", color = BluePrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurface
                )
            )
        },
        containerColor = DarkBg
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {

            Spacer(modifier = Modifier.height(12.dp))

            // Summary card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = BlueLight)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "${uiState.students.size} Students Found",
                            style = MaterialTheme.typography.titleLarge,
                            color = BluePrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Review the data below before calculating",
                            style = MaterialTheme.typography.bodyMedium,
                            color = GrayMid
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(uiState.students) { student ->
                    StudentPreviewCard(student)
                }
            }

            Button(
                onClick = { viewModel.calculateGrades() },
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(vertical = 12.dp)
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
            ) {
                Text(
                    "Calculate Grades",
                    style = MaterialTheme.typography.labelLarge,
                    color = DarkBg
                )
            }
        }
    }
}

@Composable
private fun StudentPreviewCard(student: Student) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = student.name,
                style = MaterialTheme.typography.titleLarge,
                color = OffWhite
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Scores: ${student.scores.joinToString("   |   ")}",
                style = MaterialTheme.typography.bodyMedium,
                color = GrayMid
            )
        }
    }
}