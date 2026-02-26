package com.example.student_grade_app.utils

import android.content.Context
import android.net.Uri
import com.example.student_grade_app.model.Student
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.InputStream
import java.io.OutputStream

/**
 * ## ExcelHelper
 * Handles reading and writing of `.xlsx` files using Apache POI.
 *
 * NOTE: These functions should be called from a Background Thread (Dispatchers.IO)
 * to prevent UI freezing.
 */
object ExcelHelper {

    // ── Reading ────────────────────────────────────────────────────────────

    fun readStudents(context: Context, uri: Uri): List<Student> {
        val students = mutableListOf<Student>()
        // DataFormatter handles different cell types (strings, numbers, formulas) gracefully
        val formatter = DataFormatter()

        try {
            val inputStream: InputStream = context.contentResolver.openInputStream(uri)
                ?: return emptyList()

            val workbook = XSSFWorkbook(inputStream)
            val sheet    = workbook.getSheetAt(0)

            for (rowIndex in 1..sheet.lastRowNum) {
                val row = sheet.getRow(rowIndex) ?: continue

                // Safely get name using formatter
                val name = formatter.formatCellValue(row.getCell(0)).trim()
                if (name.isBlank()) continue

                val scores = mutableListOf<Double>()
                // Start from column 1, read until the end of the row
                for (colIndex in 1 until row.lastCellNum) {
                    val cell = row.getCell(colIndex) ?: break

                    // Improved logic: Handle Numeric and Formula-based numbers
                    when (cell.cellType) {
                        CellType.NUMERIC -> scores.add(cell.numericCellValue)
                        CellType.FORMULA -> {
                            // Try to get the cached numeric value from the formula
                            try { scores.add(cell.numericCellValue) } catch (e: Exception) { /* skip */ }
                        }
                        else -> { /* Ignore non-numeric data like text in score columns */ }
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
        }

        return students
    }

    // ── Writing ────────────────────────────────────────────────────────────

    fun writeResults(
        context   : Context,
        inputUri  : Uri,
        outputUri : Uri,
        students  : List<Student>
    ) {
        try {
            // 1. Open original to preserve existing sheets
            val inputStream: InputStream = context.contentResolver.openInputStream(inputUri)
                ?: return
            val workbook = XSSFWorkbook(inputStream)
            inputStream.close()

            // 2. Clean old "Results" sheet if it exists
            val existingIndex = workbook.getSheetIndex("Results")
            if (existingIndex != -1) {
                workbook.removeSheetAt(existingIndex)
            }

            val resultsSheet = workbook.createSheet("Results")

            // 3. Setup Header Style (Dark Blue background, White Bold font)
            val headerStyle = workbook.createCellStyle().apply {
                fillForegroundColor = IndexedColors.DARK_BLUE.index
                fillPattern         = FillPatternType.SOLID_FOREGROUND
                val font = workbook.createFont().apply {
                    bold      = true
                    color     = IndexedColors.WHITE.index
                }
                setFont(font)
            }

            // 4. Create Header Row
            val headerRow = resultsSheet.createRow(0)
            listOf("Name", "Average", "Grade", "Status").forEachIndexed { col, title ->
                headerRow.createCell(col).apply {
                    setCellValue(title)
                    cellStyle = headerStyle
                }
            }

            // 5. Fill Data Rows
            students.forEachIndexed { index, student ->
                val row = resultsSheet.createRow(index + 1)
                row.createCell(0).setCellValue(student.name)
                row.createCell(1).setCellValue(student.average ?: 0.0)
                row.createCell(2).setCellValue(student.grade   ?: "-")
                row.createCell(3).setCellValue(
                    if (student.passed == true) "PASS" else "FAIL"
                )
            }

            // 6. Auto-size for mobile readability
            for (col in 0..3) {
                resultsSheet.autoSizeColumn(col)
            }

            // 7. Write to Output (using use blocks for automatic closing)
            context.contentResolver.openOutputStream(outputUri)?.use { outputStream ->
                workbook.write(outputStream)
                outputStream.flush()
            }
            workbook.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
