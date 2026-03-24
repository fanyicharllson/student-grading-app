package com.example.student_grade_app.ui.results

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.student_grade_app.model.Student
import com.example.student_grade_app.ui.theme.*
import com.example.student_grade_app.viewmodel.GradeViewModel
import com.example.student_grade_app.viewmodel.ExportFormat
import android.widget.Toast
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    viewModel : GradeViewModel,
    onBack    : () -> Unit
) {
    val context           = LocalContext.current
    val uiState           by viewModel.uiState.collectAsState()
    val scope             = rememberCoroutineScope()
    
    var showExportSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    // Show a toast when export completes
    LaunchedEffect(uiState.exportSuccess) {
        if (uiState.exportSuccess) {
            Toast.makeText(context, "Results exported successfully!", Toast.LENGTH_LONG).show()
            viewModel.resetExportState()
        }
    }

    // Show a toast when an error occurs
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title           = { Text("Results", color = OffWhite) },
                navigationIcon  = {
                    TextButton(onClick = onBack) {
                        Text("Back", color = BluePrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
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

            SummaryCard(students = uiState.calculatedStudents)

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier            = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding      = PaddingValues(bottom = 16.dp)
            ) {
                items(uiState.calculatedStudents) { student ->
                    ResultCard(student)
                }
            }

            // Export button — opens the bottom sheet
            if (uiState.isExporting) {
                Box(
                    modifier         = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BluePrimary)
                }
            } else {
                Button(
                    onClick  = { showExportSheet = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(vertical = 12.dp)
                        .height(54.dp),
                    shape  = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BlueDeep)
                ) {
                    Text(
                        "Export Results",
                        style = MaterialTheme.typography.labelLarge,
                        color = White
                    )
                }
            }
        }

        if (showExportSheet) {
            ModalBottomSheet(
                onDismissRequest = { showExportSheet = false },
                sheetState = sheetState,
                containerColor = DarkSurface
            ) {
                ExportOptionsContent(
                    onExport = { format ->
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showExportSheet = false
                                viewModel.exportResults(context, format)
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ExportOptionsContent(onExport: (ExportFormat) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
    ) {
        Text(
            text = "Select Export Format",
            style = MaterialTheme.typography.titleLarge,
            color = OffWhite,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        
        ExportOptionItem(
            title = "Excel Spreadsheet (.xlsx)",
            icon = Icons.Default.Description,
            color = Color(0xFF1D6F42),
            onClick = { onExport(ExportFormat.EXCEL) }
        )
        ExportOptionItem(
            title = "PDF Document (.pdf)",
            icon = Icons.Default.PictureAsPdf,
            color = Color(0xFFF44336),
            onClick = { onExport(ExportFormat.PDF) }
        )
        ExportOptionItem(
            title = "XML Data (.xml)",
            icon = Icons.Default.Code,
            color = Color(0xFFFF9800),
            onClick = { onExport(ExportFormat.XML) }
        )
        ExportOptionItem(
            title = "HTML Webpage (.html)",
            icon = Icons.Default.Web,
            color = Color(0xFF2196F3),
            onClick = { onExport(ExportFormat.HTML) }
        )
    }
}

@Composable
fun ExportOptionItem(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = OffWhite
            )
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
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = BlueLight)
    ) {
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(label = "Total", value = "${students.size}", color = OffWhite)
            StatItem(label = "Pass",  value = "$passCount",       color = AccentGreen)
            StatItem(label = "Fail",  value = "$failCount",       color = AccentRed)
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = color)
        Text(label, style = MaterialTheme.typography.bodyMedium, color = GrayMid)
    }
}

// ── Individual result card ─────────────────────────────────────────────────

@Composable
private fun ResultCard(student: Student) {
    val gradeColor = when (student.grade) {
        "A"  -> GradeA
        "B+" -> GradeB
        "B"  -> GradeB
        "C+" -> GradeC
        "C"  -> GradeC
        "D+" -> GradeD
        "D"  -> GradeD
        else -> GradeF
    }
    val statusColor = if (student.passed == true) AccentGreen else AccentRed

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = DarkSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Grade badge
            Box(
                modifier         = Modifier
                    .size(52.dp)
                    .background(gradeColor.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = student.grade ?: "-",
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color      = gradeColor
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    student.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = OffWhite
                )
                Text(
                    "Average: ${"%.1f".format(student.average ?: 0.0)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = GrayMid
                )
            }

            // Pass / Fail chip
            Surface(
                shape = RoundedCornerShape(50),
                color = statusColor.copy(alpha = 0.15f)
            ) {
                Text(
                    text       = if (student.passed == true) "PASS" else "FAIL",
                    color      = statusColor,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 12.sp,
                    modifier   = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                )
            }
        }
    }
}
