package com.saleem.gpacalc.util

import com.saleem.gpacalc.data.Course

// turn a statement into an expression
val <T> T.exhaustive: T
    get() = this

val possibleGrades = listOf<String>("A", "B+", "B", "C+", "C", "D", "F")

fun calculateGpa(courses: List<Course>): Double {
    var sum = 0.0
    var allHours = 0

    courses.forEach { c ->
        sum += c.gradeFormatted * c.credit_hours
        allHours += c.credit_hours * 4
    }

    return if (allHours > 0) {
        String.format("%.2f", sum / allHours * 4).toDouble()
    } else {
        0.0
    }
}