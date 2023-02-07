package com.saleem.gpacalc.data

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [Course::class], version = 1)
abstract class CourseDatabase: RoomDatabase() {

    abstract fun courseDao(): CourseDao

}