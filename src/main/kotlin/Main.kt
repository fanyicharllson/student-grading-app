package com.charlseempire


import com.charlseempire.calculator.GradeCalculator
import com.charlseempire.excel.ExcelHelper
import com.charlseempire.model.GradeResult
import com.charlseempire.model.Student
import java.io.File

private val SEPARATOR = "─".repeat(55)
private const val OUTPUT_SUFFIX = "_results"

fun main() {
    printBanner()

    // ── Step 1: Get file from user ─────────────────────────────────────────
    val inputFile = promptForFile() ?: return

    // ── Step 2: Read Excel ─────────────────────────────────────────────────
    println("\n$SEPARATOR")
    println("  Reading: ${inputFile.name}")
    println(SEPARATOR)

    val students = ExcelHelper.readStudents(inputFile)
    if (students.isEmpty()) {
        println("  No students loaded. Check your file and try again.")
        return
    }

    println("  SUCCESS: ${students.size} student(s) loaded.\n")

    // Lambda — print each student preview
    students.forEach { s ->
        val scores = s.scores.joinToString(", ") { "%.0f".format(it) }
        println("  ${s.name.padEnd(20)} $scores")
    }

    // ── Step 3: Calculate using OOP chain ──────────────────────────────────
    // GradeCalculator → extends BaseCalculator → implements Calculable
    println("\n$SEPARATOR")
    println("  Calculating grades...")
    println(SEPARATOR)

    val calculator = GradeCalculator()
    val results = calculator.calculateAll(students)  // returns List<Pair<Student, GradeResult>>

    // ── Step 4: Print results — sealed class when ──────────────────────────
    println("\n  ${"Name".padEnd(20)} ${"Average".padEnd(10)} ${"Grade".padEnd(6)} Status")
    println("  ${"─".repeat(50)}")

    // Lambda — process each result pair
    results.forEach { (student, result) ->
        // when on sealed class — compiler forces ALL cases handled
        when (result) {
            is GradeResult.Pass -> println(
                "  ${student.name.padEnd(20)} " +
                        "${"%.1f".format(result.average).padEnd(10)} " +
                        "${result.grade.padEnd(6)} PASS"
            )

            is GradeResult.Fail -> println(
                "  ${student.name.padEnd(20)} " +
                        "${"%.1f".format(result.average).padEnd(10)} " +
                        "${result.grade.padEnd(6)} FAIL"
            )

            is GradeResult.NoScores -> println(
                "  ${student.name.padEnd(20)} ${"N/A".padEnd(10)} ${"─".padEnd(6)} NO SCORES"
            )
        }
    }

    println("  ${"─".repeat(50)}")

    // Default interface method — prints summary
    calculator.summary(results)

    // ── Step 5: Export to Excel ────────────────────────────────────────────
    println("\n$SEPARATOR")
    println("  Exporting results...")
    println(SEPARATOR)

    // Convert results back to Student list for ExcelHelper
    // Lambda — map each pair, filling Student fields from GradeResult
    val studentsWithGrades: List<Student> = results.map { (student, result) ->
        when (result) {
            is GradeResult.Pass -> student.copy(
                average = result.average,
                grade = result.grade,
                passed = true
            )

            is GradeResult.Fail -> student.copy(
                average = result.average,
                grade = result.grade,
                passed = false
            )

            is GradeResult.NoScores -> student.copy(
                average = 0.0,
                grade = "-",
                passed = false
            )
        }
    }

    val outputFile = File(inputFile.parent, "${inputFile.nameWithoutExtension}${OUTPUT_SUFFIX}.xlsx")
    val success = ExcelHelper.writeResults(inputFile, outputFile, studentsWithGrades)

    if (success) {
        println("  SUCCESS: Saved to → ${outputFile.absolutePath}")
        println("  Open the file and check the 'Results' sheet.")
    } else {
        println("  FAILED: Could not write results. Check errors above.")
    }

    println("\n$SEPARATOR")
    println("  Done. Goodbye!")
    println(SEPARATOR)
}

// ── Prompt for file — same logic as before ────────────────────────────────

private fun promptForFile(): File? {
    println("\nEnter the full path to your Excel file (.xlsx)")
    println("Example: C:\\Users\\You\\Desktop\\students.xlsx")
    println("Type 'exit' to quit.\n")

    repeat(3) { attempt ->
        print("  Path: ")
        val input = readlnOrNull()?.trim()
        when {
            input.isNullOrBlank() -> println("  ERROR: Path cannot be empty.\n")
            input == "exit" -> {
                println("  Exiting..."); return null
            }

            else -> {
                val file = File(input)
                return when {
                    !file.exists() -> {
                        println("  ERROR: File not found. (${2 - attempt} attempt(s) left)\n")
                        null
                    }

                    !file.name.endsWith(".xlsx") -> {
                        println("  ERROR: Must be .xlsx format.\n")
                        null
                    }

                    else -> file
                }
            }
        }
    }
    println("  Too many failed attempts. Exiting.")
    return null
}

private fun printBanner() {
    println(
        """
$SEPARATOR
   STUDENT GRADE CALCULATOR — Console Version
   ICT University Grading Scale
$SEPARATOR
   A  : 80-100  |  B+ : 70-79  |  B  : 60-69
   C+ : 55-59   |  C  : 50-54  |  D+ : 45-49
   D  : 40-44   |  F  : 0-39   |  Pass: >= 40
$SEPARATOR""".trimIndent()
    )
}