package com.example.student_grade_app.ui.results

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

    LaunchedEffect(uiState.exportSuccess) {
        if (uiState.exportSuccess) {
            Toast.makeText(context, "Results exported successfully!", Toast.LENGTH_LONG).show()
            viewModel.resetExportState()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Performance Report", 
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = BrandPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BgDark)
            )
        },
        containerColor = BgDark
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            SummaryCard(students = uiState.calculatedStudents)

            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                "DETAILED RESULTS",
                style = MaterialTheme.typography.labelLarge,
                color = TextMuted,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
            )

            LazyColumn(
                modifier            = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding      = PaddingValues(bottom = 24.dp)
            ) {
                items(uiState.calculatedStudents) { student ->
                    ResultCard(student)
                }
            }

            if (uiState.isExporting) {
                Box(
                    modifier         = Modifier.fillMaxWidth().height(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BrandPrimary)
                }
            } else {
                Button(
                    onClick  = { showExportSheet = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(vertical = 16.dp)
                        .height(60.dp),
                    shape  = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                ) {
                    Text(
                        "Export Records",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        if (showExportSheet) {
            ModalBottomSheet(
                onDismissRequest = { showExportSheet = false },
                sheetState = sheetState,
                containerColor = SurfaceDark,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
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
            .padding(bottom = 48.dp, start = 24.dp, end = 24.dp)
    ) {
        Text(
            text = "Export As",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(vertical = 20.dp)
        )
        
        ExportOptionItem(
            title = "Excel Spreadsheet",
            subtitle = "Best for data analysis",
            icon = Icons.Default.Description,
            color = Color(0xFF10B981),
            onClick = { onExport(ExportFormat.EXCEL) }
        )
        ExportOptionItem(
            title = "PDF Document",
            subtitle = "Perfect for printing",
            icon = Icons.Default.PictureAsPdf,
            color = Color(0xFFEF4444),
            onClick = { onExport(ExportFormat.PDF) }
        )
        ExportOptionItem(
            title = "XML Data",
            subtitle = "Machine readable format",
            icon = Icons.Default.Code,
            color = Color(0xFFF59E0B),
            onClick = { onExport(ExportFormat.XML) }
        )
        ExportOptionItem(
            title = "HTML Webpage",
            subtitle = "View in any browser",
            icon = Icons.Default.Web,
            color = Color(0xFF3B82F6),
            onClick = { onExport(ExportFormat.HTML) }
        )
    }
}

@Composable
fun ExportOptionItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = SurfaceLighter.copy(alpha = 0.2f),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .border(BorderStroke(1.dp, SurfaceLighter.copy(alpha = 0.5f)), RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(color.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }
    }
}

@Composable
private fun SummaryCard(students: List<Student>) {
    val passCount = students.count { it.passed == true }
    val failCount = students.size - passCount

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(24.dp),
        colors   = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = BorderStroke(1.dp, SurfaceLighter)
    ) {
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(label = "Total", value = "${students.size}", color = BrandPrimary)
            StatItem(label = "Pass",  value = "$passCount", color = StatusPass)
            StatItem(label = "Fail",  value = "$failCount", color = StatusFail)
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = color)
        Text(label, style = MaterialTheme.typography.labelMedium, color = TextSecondary, fontWeight = FontWeight.Bold)
    }
}

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
    val statusColor = if (student.passed == true) StatusPass else StatusFail

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = BorderStroke(1.dp, SurfaceLighter.copy(alpha = 0.5f))
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier         = Modifier
                    .size(56.dp)
                    .background(gradeColor.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                    .border(1.dp, gradeColor.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = student.grade ?: "-",
                    style      = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color      = gradeColor
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    student.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    "Average: ${"%.1f".format(student.average ?: 0.0)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = statusColor.copy(alpha = 0.1f),
                border = BorderStroke(1.dp, statusColor.copy(alpha = 0.2f))
            ) {
                Text(
                    text       = if (student.passed == true) "PASS" else "FAIL",
                    color      = statusColor,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize   = 11.sp,
                    modifier   = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}
