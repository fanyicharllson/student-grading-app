package com.example.student_grade_app.ui.home

import com.example.student_grade_app.ui.theme.*
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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

    LaunchedEffect(uiState.navigateToPreview) {
        if (uiState.navigateToPreview) {
            onImported()
            viewModel.clearNavigateToPreview()
        }
    }

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.importExcel(context, it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(BgDark, Color(0xFF1E1B4B))
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 32.dp),
            verticalArrangement   = Arrangement.Center,
            horizontalAlignment   = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(BrandPrimary, BrandSecondary)
                        )
                    )
                    .border(2.dp, White.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text      = "GradeFlow",
                style     = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                color     = TextPrimary,
                letterSpacing = (-1).sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text      = "Professional Student Grading & Export Tool",
                style     = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color     = TextSecondary
            )

            Spacer(modifier = Modifier.height(48.dp))

            if (uiState.isLoading) {
                CircularProgressIndicator(color = BrandPrimary)
            } else {
                Button(
                    onClick = {
                        filePicker.launch(
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape  = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                ) {
                    Icon(Icons.Default.FileUpload, contentDescription = null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Import Class Record",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                }
            }

            AnimatedVisibility(
                visible = uiState.errorMessage != null,
                enter = fadeIn() + slideInVertically()
            ) {
                uiState.errorMessage?.let { error ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        shape    = RoundedCornerShape(14.dp),
                        colors   = CardDefaults.cardColors(
                            containerColor = StatusFail.copy(alpha = 0.1f)
                        ),
                        border = BorderStroke(1.dp, StatusFail.copy(alpha = 0.2f))
                    ) {
                        Text(
                            text     = error,
                            color    = StatusFail,
                            modifier = Modifier.padding(16.dp),
                            style    = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        ExcelFormatHintCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        )
    }
}

@Composable
private fun ExcelFormatHintCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape    = RoundedCornerShape(24.dp),
        colors   = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = BorderStroke(1.dp, SurfaceLighter)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.FileUpload,
                    contentDescription = null,
                    tint = BrandPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text       = "Sheet Guidelines",
                    style      = MaterialTheme.typography.titleMedium,
                    color      = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceLighter.copy(alpha = 0.3f))
                    .border(1.dp, SurfaceLighter, RoundedCornerShape(14.dp))
            ) {
                TableRow(
                    cells    = listOf("Name", "Exam 1", "Exam 2", "Total"),
                    isHeader = true
                )
                HorizontalDivider(color = SurfaceLighter, thickness = 1.dp)
                TableRow(cells = listOf("Alice", "85", "90", "..."))
                TableRow(cells = listOf("Bob", "60", "55", "..."))
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text  = "Column A must contain Student Names. Column B onwards are used for scoring.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 18.sp
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
            .background(if (isHeader) SurfaceLighter.copy(alpha = 0.5f) else Color.Transparent)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        cells.forEach { cell ->
            Text(
                text       = cell,
                modifier   = Modifier.weight(1f),
                fontSize   = 11.sp,
                fontWeight = if (isHeader) FontWeight.ExtraBold else FontWeight.Medium,
                color      = if (isHeader) TextPrimary else TextSecondary
            )
        }
    }
}
