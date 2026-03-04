package com.charlseempire.model

/**
 * ## GradeResult — Sealed Class
 *
 * Represents every possible outcome of a grade calculation.
 * Sealed = only these 3 outcomes can ever exist — nothing else.
 *
 * Why sealed over a plain String?
 * The compiler forces every `when` block to handle ALL cases,
 * so nothing gets missed silently.
 */
sealed class GradeResult {

    /**
     * Student has scores and their average is >= pass threshold.
     * @param grade   Letter grade e.g. "A", "B+", "C"
     * @param average Computed average score
     */
    data class Pass(
        val grade   : String,
        val average : Double
    ) : GradeResult()

    /**
     * Student has scores but average is below pass threshold.
     * @param grade   Letter grade — will always be "F" in our scale
     * @param average Computed average score
     */
    data class Fail(
        val grade   : String,
        val average : Double
    ) : GradeResult()

    /**
     * Student row existed in Excel but had no numeric scores at all.
     * Calculation cannot proceed — reported separately.
     */
    object NoScores : GradeResult()
}