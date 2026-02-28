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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ## GradeViewModel
 *
 * Holds and manages all UI state for the app.
 * Screens observe [uiState] and redraw automatically on changes.
 *
 * MVVM pattern:
 * - Model     → Student data class
 * - ViewModel → this class (logic + state)
 * - View      → Compose screens
 */
class GradeViewModel : ViewModel() {

    private val calculator = GradeCalculator()

    private val _uiState = MutableStateFlow(GradeUiState())
    val uiState: StateFlow<GradeUiState> = _uiState.asStateFlow()

    // ── Actions ────────────────────────────────────────────────────────────

    /**
     * Reads students from the selected Excel file.
     * Runs on IO thread to avoid blocking the UI.
     * Sets a one-time navigation flag so Home can navigate to Preview exactly once.
     */
    fun importExcel(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val students = ExcelHelper.readStudents(context, uri)

            // Use atomic update and set navigateToPreview so Home will navigate once
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
     * Sets a one-time navigation flag so the UI can navigate to Results exactly once.
     */
    fun calculateGrades() {
        val current = _uiState.value.students
        if (current.isEmpty()) return

        _uiState.update {
            it.copy(
                calculatedStudents = calculator.calculateAll(current),
                navigateToResults = true // mark that UI should navigate
            )
        }
    }

    /**
     * Writes results to a file in cache dir, then opens Android ShareSheet
     * so the user can save it to Files, WhatsApp, Gmail, etc.
     * No storage permissions needed — FileProvider handles the sharing safely.
     */
    fun exportResults(context: Context) {
        val results = _uiState.value.calculatedStudents
        val inputUri = _uiState.value.importedUri ?: return

        viewModelScope.launch(Dispatchers.IO) {
            // Update UI to show loading
            _uiState.update { it.copy(isExporting = true) }

            val outputFile = ExcelHelper.writeResultsToCache(
                context = context,
                inputUri = inputUri,
                students = results
            )

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
                            type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                            putExtra(Intent.EXTRA_STREAM, fileUri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }

                        // Use Activity Context if possible, otherwise add NEW_TASK
                        val chooser = Intent.createChooser(shareIntent, "Save or Share Results")
                        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(chooser)

                        // Notify UI that export succeeded (used to show Toast)
                        _uiState.update { it.copy(exportSuccess = true, errorMessage = null) }

                    } catch (e: Exception) {
                        _uiState.update { it.copy(errorMessage = "Sharing failed: ${e.message}") }
                    }
                } else {
                    _uiState.update { it.copy(errorMessage = "Could not create Excel file") }
                }
            }
        }
    }

    // New helper: clear the one-time navigation flag
    fun clearNavigateToResults() {
        _uiState.update { it.copy(navigateToResults = false) }
    }

    // New helper: clear the one-time navigation flag from Home
    fun clearNavigateToPreview() {
        _uiState.update { it.copy(navigateToPreview = false) }
    }

    /** Clears export success flag after snackbar is shown. */
    fun clearExportSuccess() {
        _uiState.value = _uiState.value.copy(exportSuccess = false)
    }

    // New helper: reset export flag (atomic)
    fun resetExportState() {
        _uiState.update { it.copy(exportSuccess = false) }
    }

    // New helper: clear any error message
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

// ── UI State ───────────────────────────────────────────────────────────────

/**
 * Complete UI state at any point in time.
 */
data class GradeUiState(
    val isLoading          : Boolean       = false,
    val isExporting        : Boolean       = false,
    val importedUri        : Uri?          = null,
    val students           : List<Student> = emptyList(),
    val calculatedStudents : List<Student> = emptyList(),
    val exportSuccess      : Boolean       = false,
    val errorMessage       : String?       = null,
    // One-time navigation event: when true, UI should navigate to Results and then clear it
    val navigateToResults  : Boolean       = false,
    // One-time navigation event: when true, Home should navigate to Preview and then clear it
    val navigateToPreview  : Boolean       = false
)