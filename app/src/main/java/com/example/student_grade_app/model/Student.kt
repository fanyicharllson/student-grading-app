package com.example.student_grade_app.model

/**
 * Represents a single student record imported from the Excel sheet.
 *
 * @property id      Row number from Excel (used as unique key in lists)
 * @property name    Full name of the student
 * @property scores  List of scores entered for the student (0.0 – 100.0)
 * @property average Computed average of all scores (null if no scores yet)
 * @property grade   Letter grade assigned after calculation (null before calc)
 * @property passed  Whether the student passed (average >= 40)
 */
data class Student(
    val id      : Int,
    val name    : String,
    val scores  : List<Double>,
    val average : Double?  = null,  // filled in by GradeCalculator
    val grade   : String?  = null,  // filled in by GradeCalculator
    val passed  : Boolean? = null   // filled in by GradeCalculator
)