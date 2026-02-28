package com.example.student_grade_app.utils

import android.content.Context
import android.net.Uri
import com.example.student_grade_app.model.Student
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * ## ExcelHelper
 *
 * Handles all reading and writing of `.xlsx` files using Apache POI.
 *
 * ### Expected Input Format
 * | Name  | Score 1 | Score 2 | Score 3 |
 * |-------|---------|---------|---------|
 * | Alice | 85      | 90      | 78      |
 *
 * ### Output
 * Saves "GradeResults.xlsx" to the app's cache directory — no storage
 * permissions needed on any Android version.
 */
object ExcelHelper {

    // ── Reading ────────────────────────────────────────────────────────────

    /**
     * Parses an Excel file from [uri] and returns a list of [Student]s.
     *
     * @param context Android context for opening the URI stream.
     * @param uri     URI of the selected .xlsx file.
     * @return List of students, or empty list if file is unreadable.
     */
    fun readStudents(context: Context, uri: Uri): List<Student> {
        val students = mutableListOf<Student>()
        try {
            val stream: InputStream =
                context.contentResolver.openInputStream(uri) ?: return emptyList()

            val workbook = XSSFWorkbook(stream)
            val sheet    = workbook.getSheetAt(0)  // always read first sheet
            stream.close()

            // Row 0 = header, skip it
            for (rowIndex in 1..sheet.lastRowNum) {
                val row  = sheet.getRow(rowIndex) ?: continue
                val name = row.getCell(0)?.stringCellValue?.trim() ?: continue
                if (name.isBlank()) continue

                // Collect all numeric scores from col 1 onwards
                val scores = mutableListOf<Double>()
                for (colIndex in 1 until row.lastCellNum) {
                    val cell = row.getCell(colIndex) ?: break
                    if (cell.cellType == CellType.NUMERIC) {
                        scores.add(cell.numericCellValue)
                    }
                }

                students.add(Student(id = rowIndex, name = name, scores = scores))
            }

            workbook.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return students
    }

    // ── Writing ────────────────────────────────────────────────────────────

    /**
     * Writes calculated results into a "Results" sheet and saves the file
     * to the app's cache directory (no storage permissions needed).
     *
     * The result file can then be shared via Android ShareSheet to
     * WhatsApp, Gmail, Files app, etc.
     *
     * @param context  Android context.
     * @param inputUri URI of the original imported .xlsx file.
     * @param students List of students with grades already calculated.
     * @return The output [File] if successful, null on failure.
     */
    fun writeResults(
        context  : Context,
        inputUri : Uri,
        students : List<Student>
    ): File? {
        return try {
            // Re-open original file to preserve the raw data sheet
            val stream: InputStream =
                context.contentResolver.openInputStream(inputUri) ?: return null
            val workbook = XSSFWorkbook(stream)
            stream.close()

            // Remove old Results sheet so re-exporting works cleanly
            workbook.getSheet("Results")?.let {
                workbook.removeSheetAt(workbook.getSheetIndex(it))
            }

            val sheet = workbook.createSheet("Results")

            // ── Header style — dark blue background, white bold text ───────
            val headerStyle = workbook.createCellStyle().apply {
                fillForegroundColor = IndexedColors.DARK_BLUE.index
                fillPattern         = FillPatternType.SOLID_FOREGROUND
                setFont(workbook.createFont().apply {
                    bold  = true
                    color = IndexedColors.WHITE.index
                })
            }

            // ── Header row ─────────────────────────────────────────────────
            val headerRow = sheet.createRow(0)
            listOf("Name", "Average", "Grade", "Status")
                .forEachIndexed { col, title ->
                    headerRow.createCell(col).apply {
                        setCellValue(title)
                        cellStyle = headerStyle
                    }
                }

            // ── Data rows — one per student ────────────────────────────────
            students.forEachIndexed { index, student ->
                val row = sheet.createRow(index + 1)
                row.createCell(0).setCellValue(student.name)
                row.createCell(1).setCellValue(student.average ?: 0.0)
                row.createCell(2).setCellValue(student.grade   ?: "-")
                row.createCell(3).setCellValue(
                    if (student.passed == true) "PASS" else "FAIL"
                )
            }

            // Auto-size all columns for readability
            for (col in 0..3) sheet.autoSizeColumn(col)

            // ── Write to cache dir (always writable, no permissions) ───────
            val outputFile   = File(context.cacheDir, "GradeResults.xlsx")
            val outputStream = FileOutputStream(outputFile)
            workbook.write(outputStream)
            outputStream.flush()
            outputStream.close()
            workbook.close()

            outputFile  // return the file so ViewModel can share it

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}