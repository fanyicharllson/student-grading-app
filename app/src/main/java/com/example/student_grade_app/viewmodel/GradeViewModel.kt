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
     */
    fun importExcel(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val students = ExcelHelper.readStudents(context, uri)

            _uiState.value = _uiState.value.copy(
                isLoading    = false,
                importedUri  = uri,
                students     = students,
                errorMessage = if (students.isEmpty()) "No students found in file" else null
            )
        }
    }

    /**
     * Runs GradeCalculator on all imported students.
     */
    fun calculateGrades() {
        val current = _uiState.value.students
        if (current.isEmpty()) return

        _uiState.value = _uiState.value.copy(
            calculatedStudents = calculator.calculateAll(current)
        )
    }

    /**
     * Writes results to a file in cache dir, then opens Android ShareSheet
     * so the user can save it to Files, WhatsApp, Gmail, etc.
     * No storage permissions needed — FileProvider handles the sharing safely.
     */
    fun exportResults(context: Context) {
        val results  = _uiState.value.calculatedStudents
        val inputUri = _uiState.value.importedUri ?: return

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = _uiState.value.copy(isExporting = true)

            val outputFile = ExcelHelper.writeResults(
                context  = context,
                inputUri = inputUri,
                students = results
            )

            withContext(Dispatchers.Main) {
                _uiState.value = _uiState.value.copy(isExporting = false)

                if (outputFile != null) {
                    // Use FileProvider to safely share the cache file
                    val fileUri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.provider",
                        outputFile
                    )

                    // Open Android ShareSheet — user picks where to save/send
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type        = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        putExtra(Intent.EXTRA_STREAM, fileUri)
                        putExtra(Intent.EXTRA_SUBJECT, "Grade Results")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }

                    context.startActivity(
                        Intent.createChooser(shareIntent, "Save or Share Results")
                    )

                    _uiState.value = _uiState.value.copy(exportSuccess = true)
                } else {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Export failed — please try again"
                    )
                }
            }
        }
    }

    /** Clears export success flag after snackbar is shown. */
    fun clearExportSuccess() {
        _uiState.value = _uiState.value.copy(exportSuccess = false)
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
    val errorMessage       : String?       = null
)