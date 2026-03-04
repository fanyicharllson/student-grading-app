package com.charlseempire.calculator

import com.charlseempire.model.GradeResult
import com.charlseempire.model.Student


/**
 * ## Calculable — Interface
 *
 * Defines the contract every calculator must follow.
 * Any class that implements this MUST provide [calculate] and [calculateAll].
 * [summary] has a default implementation — can be overridden if needed.
 */
interface Calculable {

    /**
     * Calculate grade for a single student.
     * Returns a [GradeResult] — sealed, so all cases are handled.
     */
    fun calculate(student: Student): GradeResult

    /**
     * Calculate grades for a list of students.
     * Each student maps to a [GradeResult].
     */
    fun calculateAll(students: List<Student>): List<Pair<Student, GradeResult>>

    /**
     * Default implementation — prints a summary to console.
     * Can be overridden by any implementing class.
     */
    fun summary(results: List<Pair<Student, GradeResult>>) {
        val pass = results.count { it.second is GradeResult.Pass }
        val fail = results.count { it.second is GradeResult.Fail }
        val none = results.count { it.second is GradeResult.NoScores }
        println("  Total: ${results.size}  |  Pass: $pass  |  Fail: $fail  |  No Scores: $none")
    }
}