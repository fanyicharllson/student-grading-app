package com.example.student_grade_app.utils


import android.content.Context
import android.net.Uri
import com.example.student_grade_app.model.Student
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.InputStream
import java.io.OutputStream

/**
 * ## ExcelHelper
 *
 * Handles all reading and writing of `.xlsx` files using Apache POI.
 *
 * ### Expected Excel Input Format
 * The first row must be a header row. Columns should follow this layout:
 * | A      | B       | C       | D       | ... |
 * |--------|---------|---------|---------|-----|
 * | Name   | Score 1 | Score 2 | Score 3 | ... |
 * | Alice  | 85      | 90      | 78      | ... |
 *
 * ### Output Sheet
 * A new sheet named "Results" is added to the same workbook with:
 * Name | Average | Grade | Status
 */
object ExcelHelper {

    // ── Reading ────────────────────────────────────────────────────────────

    /**
     * Parses an Excel file from the given [uri] and returns a list of [Student]s.
     *
     * @param context  Android context (needed to open the URI stream).
     * @param uri      URI of the selected .xlsx file.
     * @return List of students parsed from the first sheet, or empty list on error.
     */
    fun readStudents(context: Context, uri: Uri): List<Student> {
        val students = mutableListOf<Student>()

        try {
            val inputStream: InputStream = context.contentResolver.openInputStream(uri)
                ?: return emptyList()

            val workbook = XSSFWorkbook(inputStream)
            val sheet    = workbook.getSheetAt(0)      // always read first sheet

            // Skip row 0 (header) — start from row index 1
            for (rowIndex in 1..sheet.lastRowNum) {
                val row = sheet.getRow(rowIndex) ?: continue

                // Column 0 → student name
                val name = row.getCell(0)?.stringCellValue?.trim() ?: continue
                if (name.isBlank()) continue

                // Column 1 onwards → scores (read until empty cell)
                val scores = mutableListOf<Double>()
                for (colIndex in 1 until row.lastCellNum) {
                    val cell = row.getCell(colIndex) ?: break
                    if (cell.cellType == CellType.NUMERIC) {
                        scores.add(cell.numericCellValue)
                    }
                }

                students.add(
                    Student(id = rowIndex, name = name, scores = scores)
                )
            }

            workbook.close()
            inputStream.close()

        } catch (e: Exception) {
            e.printStackTrace()
            // Return whatever was parsed before the error
        }

        return students
    }

    // ── Writing ────────────────────────────────────────────────────────────

    /**
     * Writes calculated student results into a new "Results" sheet inside
     * the original workbook, then saves the file to [outputUri].
     *
     * @param context     Android context.
     * @param inputUri    URI of the original .xlsx file (we re-open to preserve data).
     * @param outputUri   URI where the updated file will be saved.
     * @param students    List of students with grades already calculated.
     */
    fun writeResults(
        context   : Context,
        inputUri  : Uri,
        outputUri : Uri,
        students  : List<Student>
    ) {
        try {
            // Re-open original file to preserve existing data
            val inputStream: InputStream = context.contentResolver.openInputStream(inputUri)
                ?: return
            val workbook = XSSFWorkbook(inputStream)
            inputStream.close()

            // Remove old results sheet if it exists (clean re-run)
            workbook.getSheet("Results")?.let {
                workbook.removeSheetAt(workbook.getSheetIndex(it))
            }

            val resultsSheet = workbook.createSheet("Results")

            // ── Header Row ────────────────────────────────────────────────
            val headerStyle = workbook.createCellStyle().apply {
                fillForegroundColor = IndexedColors.DARK_BLUE.index
                fillPattern         = FillPatternType.SOLID_FOREGROUND
                val font = workbook.createFont().apply {
                    bold      = true
                    color     = IndexedColors.WHITE.index
                }
                setFont(font)
            }

            val headerRow = resultsSheet.createRow(0)
            listOf("Name", "Average", "Grade", "Status").forEachIndexed { col, title ->
                headerRow.createCell(col).apply {
                    setCellValue(title)
                    cellStyle = headerStyle
                }
            }

            // ── Data Rows ─────────────────────────────────────────────────
            students.forEachIndexed { index, student ->
                val row = resultsSheet.createRow(index + 1)
                row.createCell(0).setCellValue(student.name)
                row.createCell(1).setCellValue(student.average ?: 0.0)
                row.createCell(2).setCellValue(student.grade   ?: "-")
                row.createCell(3).setCellValue(
                    if (student.passed == true) "PASS" else "FAIL"
                )
            }

            // Auto-size columns for readability
            for (col in 0..3) resultsSheet.autoSizeColumn(col)

            // ── Save to output URI ─────────────────────────────────────────
            val outputStream: OutputStream = context.contentResolver.openOutputStream(outputUri)
                ?: return
            workbook.write(outputStream)
            outputStream.flush()
            outputStream.close()
            workbook.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}