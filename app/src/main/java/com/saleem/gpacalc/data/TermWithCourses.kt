package com.saleem.gpacalc.data

import androidx.room.Embedded
import androidx.room.Relation

// to specify how to join tables
data class TermWithCourses(
    @Embedded val term: Term,
    @Relation(
        parentColumn = "termId",
        entityColumn = "termId"
    )
    val courses: List<Course>
) {}
