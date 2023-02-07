package com.saleem.gpacalc.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "course_table")
@Parcelize
data class Course(

    val name: String,
    val credit_hours: Int,
    val grade: String,

    @PrimaryKey(autoGenerate = true)  val id: Int = 0
): Parcelable {
}