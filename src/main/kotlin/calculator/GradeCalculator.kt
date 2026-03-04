package com.charlseempire.calculator

import com.charlseempire.model.GradeResult
import com.charlseempire.model.Student


/**
 * ## GradeCalculator
 *
 * Extends [BaseCalculator] and implements [Calculable].
 * Same logic as before — now properly shaped with OOP.
 *
 * Primary constructor passes ICT University values up to [BaseCalculator].
 * Secondary constructor allows a custom threshold (useful for testing).
 */
class GradeCalculator : BaseCalculator(
    passThreshold = PASS_THRESHOLD,
    scaleName = "ICT University"
), Calculable {

    // ── Calculable implementation ──────────────────────────────────────────

    /**
     * Calculates grade for one student.
     * Returns a [GradeResult] sealed class — Pass, Fail, or NoScores.
     */
    override fun calculate(student: Student): GradeResult {
        if (student.scores.isEmpty()) return GradeResult.NoScores

        val avg = computeAverage(student.scores)   // from BaseCalculator
        val grade = assignGrade(avg)                  // implemented below

        return if (avg >= passThreshold)
            GradeResult.Pass(grade = grade, average = avg)
        else
            GradeResult.Fail(grade = grade, average = avg)
    }

    /**
     * Runs [calculate] over a full list using a lambda (map).
     * Returns pairs of Student → GradeResult.
     */
    override fun calculateAll(
        students: List<Student>
    ): List<Pair<Student, GradeResult>> =
        students.map { student -> student to calculate(student) }

    // ── BaseCalculator abstract implementation ─────────────────────────────

    /**
     * ICT University grading scale.
     * Sealed into a [when] — same logic as original, just moved here.
     */
    override fun assignGrade(average: Double): String = when {
        average >= 80 -> "A"
        average >= 70 -> "B+"
        average >= 60 -> "B"
        average >= 55 -> "C+"
        average >= 50 -> "C"
        average >= 45 -> "D+"
        average >= 40 -> "D"
        else -> "F"
    }

    companion object {
        const val PASS_THRESHOLD = 40.0
        const val MAX_SCORE = 100.0
        const val MIN_SCORE = 0.0
    }
}