package com.charlseempire

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

import com.charlseempire.model.Student
import com.charlseempire.utils.ExcelHelper
import com.charlseempire.utils.GradeCalculator
import java.io.File

// ── Constants ──────────────────────────────────────────────────────────────
private val SEPARATOR = "─".repeat(55)
private const val OUTPUT_SUFFIX = "_results"

fun main() {
    printBanner()

    // ── Step 1: Get file path from user ────────────────────────────────────
    val inputFile = promptForFile() ?: return

    // ── Step 2: Read students from Excel ──────────────────────────────────
    println("\n$SEPARATOR")
    println("  Reading file: ${inputFile.name}")
    println(SEPARATOR)

    val students = ExcelHelper.readStudents(inputFile)

    if (students.isEmpty()) {
        println("\n  No students loaded. Please check your file and try again.")
        return
    }

    println("  SUCCESS: ${students.size} student(s) loaded.\n")
    printStudentPreview(students)

    // ── Step 3: Calculate grades ───────────────────────────────────────────
    println("\n$SEPARATOR")
    println("  Calculating grades...")
    println(SEPARATOR)

    val calculator = GradeCalculator()
    val results = calculator.calculateAll(students)

    printResults(results)

    // ── Step 4: Write results back to Excel ────────────────────────────────
    println("\n$SEPARATOR")
    println("  Exporting results to Excel...")
    println(SEPARATOR)

    val outputFile = buildOutputFile(inputFile)
    val success = ExcelHelper.writeResults(inputFile, outputFile, results)

    if (success) {
        println("  SUCCESS: Results written to → ${outputFile.absolutePath}")
        println("  Open the file and check the 'Results' sheet.")
    } else {
        println("  FAILED: Could not write results. Check errors above.")
    }

    println("\n$SEPARATOR")
    println("  Done. Goodbye!")
    println(SEPARATOR)
}

// ── Prompt user for a valid file path ─────────────────────────────────────

/**
 * Keeps asking the user for a file path until they enter a valid .xlsx file
 * or type 'exit'. Returns null if user wants to quit.
 */
private fun promptForFile(): File? {
    println("\nEnter the full path to your Excel file (.xlsx)")
    println("Example: C:\\Users\\You\\Desktop\\students.xlsx")
    println("Type 'exit' to quit.\n")

    repeat(3) { attempt ->
        print("  Path: ")
        val input = readlnOrNull()?.trim()

        when {
            input.isNullOrBlank() -> println("  ERROR: Path cannot be empty. Try again.\n")
            input == "exit" -> {
                println("  Exiting..."); return null
            }

            else -> {
                val file = File(input)
                return when {
                    !file.exists() -> {
                        println("  ERROR: File not found. Check the path.\n"); null.also {
                            if (attempt < 2) println("  Try again (${2 - attempt} attempt(s) left).\n")
                        }
                    }

                    !file.name.endsWith(".xlsx") -> {
                        println("  ERROR: File must be .xlsx\n"); null
                    }

                    else -> file
                }
            }
        }
    }

    println("  Too many failed attempts. Exiting.")
    return null
}

// ── Console output helpers ─────────────────────────────────────────────────

private fun printBanner() {
    println(
        """
$SEPARATOR
   STUDENT GRADE CALCULATOR — Console Version
   ICT University Grading Scale
$SEPARATOR
   A  : 80-100  |  B+ : 70-79  |  B  : 60-69
   C+ : 55-59   |  C  : 50-54  |  D+ : 45-49
   D  : 40-44   |  F  : 0-39
   Pass threshold: 40.0
$SEPARATOR""".trimIndent()
    )
}

private fun printStudentPreview(students: List<Student>) {
    println("  Preview of imported students:")
    println("  ${"Name".padEnd(20)} Scores")
    println("  ${"─".repeat(45)}")
    students.forEach { s ->
        val scoresStr = s.scores.joinToString(", ") { "%.0f".format(it) }
        println("  ${s.name.padEnd(20)} $scoresStr")
    }
}

private fun printResults(results: List<Student>) {
    val passCount = results.count { it.passed == true }
    val failCount = results.size - passCount

    println("\n  RESULTS:")
    println("  ${"─".repeat(55)}")
    println("  ${"Name".padEnd(20)} ${"Average".padEnd(10)} ${"Grade".padEnd(6)} Status")
    println("  ${"─".repeat(55)}")

    results.forEach { s ->
        val avg = "%.1f".format(s.average ?: 0.0)
        val grade = s.grade ?: "-"
        val status = if (s.passed == true) "PASS" else "FAIL"
        println("  ${s.name.padEnd(20)} ${avg.padEnd(10)} ${grade.padEnd(6)} $status")
    }

    println("  ${"─".repeat(55)}")
    println("  Total: ${results.size}   Pass: $passCount   Fail: $failCount")
}

/**
 * Builds output file name from input.
 * E.g. "students.xlsx" → "students_results.xlsx" in same folder.
 */
private fun buildOutputFile(inputFile: File): File {
    val nameWithoutExt = inputFile.nameWithoutExtension
    return File(inputFile.parent, "${nameWithoutExt}${OUTPUT_SUFFIX}.xlsx")
}