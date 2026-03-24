package com.example.student_grade_app.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.student_grade_app.model.Student
import com.example.student_grade_app.utils.ExcelHelper
import com.example.student_grade_app.utils.GradeCalculator
import com.example.student_grade_app.utils.HtmlHelper
import com.example.student_grade_app.utils.PdfHelper
import com.example.student_grade_app.utils.XmlHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

enum class ExportFormat {
    EXCEL, PDF, XML, HTML
}

/**
 * ## GradeViewModel
 *
 * Holds and manages all UI state for the app.
 * Screens observe [uiState] and redraw automatically on changes.
 */
class GradeViewModel : ViewModel() {

    private val calculator = GradeCalculator()

    private val _uiState = MutableStateFlow(GradeUiState())
    val uiState: StateFlow<GradeUiState> = _uiState.asStateFlow()

    // ── Actions ────────────────────────────────────────────────────────────

    fun importExcel(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val students = ExcelHelper.readStudents(context, uri)

            _uiState.update {
                it.copy(
                    isLoading    = false,
                    importedUri  = uri,
                    students     = students,
                    errorMessage = if (students.isEmpty()) "No students found in file" else null,
                    navigateToPreview = students.isNotEmpty()
                )
            }
        }
    }

    /**
     * Runs GradeCalculator on all imported students.
     * Includes an artificial delay to show a captivating loading state.
     */
    fun calculateGrades() {
        val current = _uiState.value.students
        if (current.isEmpty()) return

        viewModelScope.launch(Dispatchers.Default) {
            _uiState.update { it.copy(isCalculating = true) }
            
            // Artificial delay for "captivating" UX
            delay(1500) 
            
            val results = calculator.calculateAll(current)

            _uiState.update {
                it.copy(
                    isCalculating = false,
                    calculatedStudents = results,
                    navigateToResults = true 
                )
            }
        }
    }

    /**
     * Writes results to a file in cache dir based on [format], then opens Android ShareSheet.
     */
    fun exportResults(context: Context, format: ExportFormat) {
        val results = _uiState.value.calculatedStudents
        val inputUri = _uiState.value.importedUri ?: return

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isExporting = true) }

            val (outputFile, mimeType) = when (format) {
                ExportFormat.EXCEL -> {
                    ExcelHelper.writeResultsToCache(context, inputUri, results) to 
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                }
                ExportFormat.PDF -> {
                    PdfHelper.writeResultsToCache(context, results) to "application/pdf"
                }
                ExportFormat.XML -> {
                    XmlHelper.writeResultsToCache(context, results) to "application/xml"
                }
                ExportFormat.HTML -> {
                    HtmlHelper.writeResultsToCache(context, results) to "text/html"
                }
            }

            withContext(Dispatchers.Main) {
                _uiState.update { it.copy(isExporting = false) }

                if (outputFile != null && outputFile.exists()) {
                    try {
                        val fileUri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.provider",
                            outputFile
                        )

                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = mimeType
                            putExtra(Intent.EXTRA_STREAM, fileUri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }

                        val chooser = Intent.createChooser(shareIntent, "Save or Share Results")
                        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(chooser)

                        _uiState.update { it.copy(exportSuccess = true, errorMessage = null) }

                    } catch (e: Exception) {
                        _uiState.update { it.copy(errorMessage = "Sharing failed: ${e.message}") }
                    }
                } else {
                    _uiState.update { it.copy(errorMessage = "Could not create ${format.name} file") }
                }
            }
        }
    }

    fun clearNavigateToResults() {
        _uiState.update { it.copy(navigateToResults = false) }
    }

    fun clearNavigateToPreview() {
        _uiState.update { it.copy(navigateToPreview = false) }
    }

    fun clearExportSuccess() {
        _uiState.update { it.copy(exportSuccess = false) }
    }

    fun resetExportState() {
        _uiState.update { it.copy(exportSuccess = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

// ── UI State ───────────────────────────────────────────────────────────────

data class GradeUiState(
    val isLoading          : Boolean       = false,
    val isCalculating      : Boolean       = false,
    val isExporting        : Boolean       = false,
    val importedUri        : Uri?          = null,
    val students           : List<Student> = emptyList(),
    val calculatedStudents : List<Student> = emptyList(),
    val exportSuccess      : Boolean       = false,
    val errorMessage       : String?       = null,
    val navigateToResults  : Boolean       = false,
    val navigateToPreview  : Boolean       = false
)