package com.saleem.gpacalc.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.saleem.gpacalc.data.preferencesmanager.PreferencesManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "term_table")
@Parcelize
data class Term(
    val name: String,
    val gpa: Double,
    @PrimaryKey(autoGenerate = true) val termId: Int = 0
) : Parcelable {

    val gpa_5: Double
    get() = String.format("%.2f", (gpa * 5 / 4)).toDouble()
}
