package com.charlseempire.calculator


/**
 * ## BaseCalculator — Abstract Class
 *
 * Holds shared config (passThreshold, scale name) and the
 * [computeAverage] helper that all calculators need.
 *
 * Cannot be instantiated directly — must be subclassed.
 *
 * @param passThreshold Minimum average to pass (default 40.0 — ICT University)
 * @param scaleName     Name of the grading scale used (shown in logs)
 */
abstract class BaseCalculator(
    val passThreshold: Double = 40.0,
    val scaleName: String = "ICT University Scale"
) {
    // ── Initializer block — runs on every construction ─────────────────────
    init {
        require(passThreshold >= 0.0) {
            "Pass threshold cannot be negative — got $passThreshold"
        }
        println("  [Calculator] Initialized — Scale: $scaleName | Pass threshold: $passThreshold")
    }

    /**
     * Shared helper — computes arithmetic mean.
     * Returns 0.0 on empty list to avoid division by zero.
     * Protected so only subclasses can use it.
     */
    protected fun computeAverage(scores: List<Double>): Double =
        if (scores.isEmpty()) 0.0 else scores.sum() / scores.size

    /**
     * Must be implemented by every subclass — maps average to letter grade.
     */
    protected abstract fun assignGrade(average: Double): String
}