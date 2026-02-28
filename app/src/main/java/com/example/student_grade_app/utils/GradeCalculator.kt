package com.example.student_grade_app.utils


import com.example.student_grade_app.model.Student

/**
 * ## GradeCalculator
 *
 * Core business logic class responsible for computing student grades.
 * This class is intentionally kept pure — it has NO dependency on Android
 * or UI frameworks, making it easy to test and reuse in the Dart version.
 * ### Pass / Fail Threshold
 * A student passes if their average is **>= 50**.
 */
class GradeCalculator {

    // ── Public API ─────────────────────────────────────────────────────────

    /**
     * Calculates the average, letter grade, and pass/fail status for a
     * single [Student] and returns an updated copy of that student.
     *
     * @param student The student whose scores will be evaluated.
     * @return A new [Student] object with [Student.average], [Student.grade],
     *         and [Student.passed] filled in.
     */
    fun calculate(student: Student): Student {
        val avg = computeAverage(student.scores)
        return student.copy(
            average = avg,
            grade = assignGrade(avg),
            passed = avg >= PASS_THRESHOLD
        )
    }

    /**
     * Convenience function — runs [calculate] over a whole list of students.
     *
     * @param students Raw list of students (scores filled, grades empty).
     * @return New list where every student has average, grade and passed set.
     */
    fun calculateAll(students: List<Student>): List<Student> =
        students.map { calculate(it) }

    // ── Private Helpers ────────────────────────────────────────────────────

    /**
     * Computes the arithmetic mean of a list of scores.
     * Returns 0.0 if the list is empty to avoid division by zero.
     */
    private fun computeAverage(scores: List<Double>): Double =
        if (scores.isEmpty()) 0.0 else scores.sum() / scores.size

    /**
     * Maps a numeric average to a letter grade using a [when] expression.
     * This is idiomatic Kotlin — equivalent to a switch/case in Java.
     */
    private fun assignGrade(average: Double): String = when {
        average >= 80 -> "A"
        average >= 70 -> "B+"
        average >= 60 -> "B"
        average >= 55 -> "C+"
        average >= 50 -> "C"
        average >= 45 -> "D+"
        average >= 40 -> "D"
        else -> "F"
    }

    // ── Constants ──────────────────────────────────────────────────────────

    companion object {
        /** Minimum average required for a student to be considered passing. */
        const val PASS_THRESHOLD = 40.0

        /** Highest possible score a student can receive. */
        const val MAX_SCORE = 100.0

        /** Lowest possible score a student can receive. */
        const val MIN_SCORE = 0.0
    }
}