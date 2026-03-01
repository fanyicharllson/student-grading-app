package com.charlseempire.utils


import com.charlseempire.model.Student
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * ## ExcelHelper (Console Version)
 *
 * Same logic as Android version — uses plain [File] instead of
 * Android Uri/Context, so it runs on any JVM.
 */
object ExcelHelper {

    /**
     * Reads students from an .xlsx file.
     * @return List of [Student]s, empty on any error.
     */
    fun readStudents(file: File): List<Student> {
        if (!file.exists()) {
            println("  ERROR: File not found → ${file.absolutePath}")
            return emptyList()
        }
        if (!file.name.endsWith(".xlsx")) {
            println("  ERROR: Must be .xlsx format → got '${file.name}'")
            return emptyList()
        }

        val students = mutableListOf<Student>()
        var skippedRows = 0

        try {
            val workbook = XSSFWorkbook(FileInputStream(file))
            val sheet = workbook.getSheetAt(0)

            if (sheet.lastRowNum < 1) {
                println("  ERROR: Sheet is empty or only has a header row.")
                workbook.close()
                return emptyList()
            }

            for (rowIndex in 1..sheet.lastRowNum) {
                val row = sheet.getRow(rowIndex) ?: continue
                val name = row.getCell(0)?.stringCellValue?.trim()

                if (name.isNullOrBlank()) {
                    skippedRows++; continue
                }

                val scores = mutableListOf<Double>()
                for (colIndex in 1 until row.lastCellNum) {
                    val cell = row.getCell(colIndex) ?: break
                    when (cell.cellType) {
                        CellType.NUMERIC -> scores.add(cell.numericCellValue)
                        CellType.STRING ->
                            cell.stringCellValue.toDoubleOrNull()
                                ?.let { scores.add(it) }
                                ?: println("  WARN: Non-numeric score at row ${rowIndex + 1} — skipped.")

                        else -> {}
                    }
                }

                if (scores.isEmpty()) {
                    println("  WARN: '${name}' has no scores — skipped.")
                    skippedRows++
                    continue
                }

                students.add(Student(id = rowIndex, name = name, scores = scores))
            }

            workbook.close()
            if (skippedRows > 0) println("  WARN: $skippedRows row(s) skipped due to missing data.")

        } catch (e: Exception) {
            println("  ERROR: Could not read file → ${e.message}")
            return emptyList()
        }

        return students
    }

    /**
     * Writes results into a new "Results" sheet in the workbook.
     * Saves to [outputFile]. Returns true on success.
     */
    fun writeResults(inputFile: File, outputFile: File, students: List<Student>): Boolean {
        return try {
            val workbook = XSSFWorkbook(FileInputStream(inputFile))

            // Remove stale Results sheet if re-running
            workbook.getSheet("Results")?.let {
                workbook.removeSheetAt(workbook.getSheetIndex(it))
            }

            val sheet = workbook.createSheet("Results")

            val headerStyle = workbook.createCellStyle().apply {
                fillForegroundColor = IndexedColors.DARK_BLUE.index
                fillPattern = FillPatternType.SOLID_FOREGROUND
                setFont(workbook.createFont().apply {
                    bold = true
                    color = IndexedColors.WHITE.index
                })
            }

            val headerRow = sheet.createRow(0)
            listOf("Name", "Average", "Grade", "Status").forEachIndexed { col, title ->
                headerRow.createCell(col).apply {
                    setCellValue(title)
                    cellStyle = headerStyle
                }
            }

            students.forEachIndexed { index, s ->
                val row = sheet.createRow(index + 1)
                row.createCell(0).setCellValue(s.name)
                row.createCell(1).setCellValue(s.average ?: 0.0)
                row.createCell(2).setCellValue(s.grade ?: "-")
                row.createCell(3).setCellValue(if (s.passed == true) "PASS" else "FAIL")
            }

            for (col in 0..3) sheet.autoSizeColumn(col)

            FileOutputStream(outputFile).use { workbook.write(it) }
            workbook.close()
            true

        } catch (e: Exception) {
            println("  ERROR: Could not write results → ${e.message}")
            false
        }
    }
}