package com.saleem.gpacalc.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.lang.Exception

@Entity(tableName = "course_table")
@Parcelize
data class Course(
    val name: String,
    val credit_hours: Int,
    val grade: String,
    val termId: Int,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
) : Parcelable {
    // transform letter grade into its corresponding points
    val gradeFormatted: Double
        get() =
            when (grade) {
                "A" -> 4.0
                "B+" -> 3.5
                "B" -> 3.0
                "C+" -> 2.5
                "C" -> 2.0
                "D" -> 1.5
                "F" -> 0.0
                else -> {
                    throw Exception("Not a valid grade")
                }

            }
}