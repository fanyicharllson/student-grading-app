package com.charlseempire.utils

import com.charlseempire.model.Student


/**
 * ## GradeCalculator
 *
 * Core business logic class responsible for computing student grades.
 * This class is intentionally kept pure — no Android/UI dependencies,
 * making it easy to unit test and reuse in the Dart version.
 *
 * ### ICT University Grading Scale
 * | Average Score | Letter Grade |
 * |---------------|--------------|
 * | 80 – 100      | A            |
 * | 70 – 79       | B+           |
 * | 60 – 69       | B            |
 * | 55 – 59       | C+           |
 * | 50 – 54       | C            |
 * | 45 – 49       | D+           |
 * | 40 – 44       | D            |
 * | 0  – 39       | F            |
 *
 * ### Pass / Fail Threshold
 * A student passes if their average is >= 40 (grade D or above).
 */
class GradeCalculator {

    // ── Public API ─────────────────────────────────────────────────────────

    /**
     * Calculates the average, letter grade, and pass/fail for a single
     * [Student] and returns an updated copy with all fields filled in.
     *
     * @param student The student whose scores will be evaluated.
     * @return A new [Student] with [Student.average], [Student.grade],
     *         and [Student.passed] populated.
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
     * Convenience — runs [calculate] over a whole list of students.
     *
     * @param students Raw list (scores filled, grades empty).
     * @return New list where every student has average, grade, passed set.
     */
    fun calculateAll(students: List<Student>): List<Student> =
        students.map { calculate(it) }

    // ── Private Helpers ────────────────────────────────────────────────────

    /**
     * Computes arithmetic mean of a score list.
     * Returns 0.0 if the list is empty to avoid division by zero.
     */
    private fun computeAverage(scores: List<Double>): Double =
        if (scores.isEmpty()) 0.0 else scores.sum() / scores.size

    /**
     * Maps a numeric average to an ICT University letter grade.
     * Uses a [when] expression — idiomatic Kotlin switch/case.
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
        /** Minimum average to pass — grade D (40.0) per ICT University scale. */
        const val PASS_THRESHOLD = 40.0

        /** Highest possible score. */
        const val MAX_SCORE = 100.0

        /** Lowest possible score. */
        const val MIN_SCORE = 0.0
    }
}