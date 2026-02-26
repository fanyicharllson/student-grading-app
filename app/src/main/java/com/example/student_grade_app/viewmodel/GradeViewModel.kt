package com.example.student_grade_app.viewmodel


import android.content.Context
import android.net.Uri
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

/**
 * ## GradeViewModel
 *
 * Holds and manages all UI state for the app.
 * The UI screens observe [uiState] and react to changes automatically.
 *
 * This follows the MVVM pattern:
 * - **Model**     → Student data class
 * - **ViewModel** → this class (logic + state)
 * - **View**      → Compose screens (HomeScreen, PreviewScreen, ResultsScreen)
 */
class GradeViewModel : ViewModel() {

    // ── Calculator instance ────────────────────────────────────────────────
    private val calculator = GradeCalculator()

    // ── UI State ───────────────────────────────────────────────────────────

    /**
     * Single source of truth for all UI state.
     * Screens collect this flow and redraw whenever it changes.
     */
    private val _uiState = MutableStateFlow(GradeUiState())
    val uiState: StateFlow<GradeUiState> = _uiState.asStateFlow()

    // ── Actions called by the UI ───────────────────────────────────────────

    /**
     * Imports students from the selected Excel file.
     * Runs on IO thread to avoid blocking the UI.
     *
     * @param context Android context for file access.
     * @param uri     URI of the selected .xlsx file.
     */
    fun importExcel(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val students = ExcelHelper.readStudents(context, uri)

            _uiState.value = _uiState.value.copy(
                isLoading     = false,
                importedUri   = uri,
                students      = students,
                errorMessage  = if (students.isEmpty()) "No students found in file" else null
            )
        }
    }

    /**
     * Runs [GradeCalculator.calculateAll] on the imported students,
     * then updates state so the ResultsScreen can display them.
     */
    fun calculateGrades() {
        val current = _uiState.value.students
        if (current.isEmpty()) return

        val results = calculator.calculateAll(current)

        _uiState.value = _uiState.value.copy(
            calculatedStudents = results
        )
    }

    /**
     * Exports the calculated results back to a new Excel file.
     *
     * @param context   Android context.
     * @param outputUri URI chosen by user to save the result file.
     */
    fun exportResults(context: Context, outputUri: Uri) {
        val results = _uiState.value.calculatedStudents
        val inputUri = _uiState.value.importedUri ?: return

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = _uiState.value.copy(isExporting = true)

            ExcelHelper.writeResults(
                context    = context,
                inputUri   = inputUri,
                outputUri  = outputUri,
                students   = results
            )

            _uiState.value = _uiState.value.copy(
                isExporting  = false,
                exportSuccess = true
            )
        }
    }

    /** Clears the export success flag after the UI has shown the toast. */
    fun clearExportSuccess() {
        _uiState.value = _uiState.value.copy(exportSuccess = false)
    }
}

// ── UI State Data Class ────────────────────────────────────────────────────

/**
 * Represents the complete UI state at any point in time.
 *
 * @property isLoading          True while reading the Excel file.
 * @property isExporting        True while writing results back to Excel.
 * @property importedUri        URI of the file the user picked.
 * @property students           Raw students parsed from Excel (no grades yet).
 * @property calculatedStudents Students after [GradeCalculator] has run.
 * @property exportSuccess      True once export finishes — used to show a toast.
 * @property errorMessage       Non-null when something went wrong.
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