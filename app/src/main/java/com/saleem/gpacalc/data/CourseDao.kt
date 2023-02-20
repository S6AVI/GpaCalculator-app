package com.saleem.gpacalc.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(course: Course)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(term: Term)

    @Update
    suspend fun update(course: Course)

    @Update
    suspend fun update(term: Term)

    @Delete
    suspend fun delete(course: Course)

    @Delete
    suspend fun delete(term: Term)

    @Query("SELECT * FROM course_table")
    fun getCourses(): Flow<List<Course>>

    @Query("SELECT * FROM term_table")
    fun getTerms(): Flow<List<Term>>

    @Transaction
    @Query("DELETE FROM course_table WHERE termId = :id")
    suspend fun deleteAllCourses(id: Int)

    @Transaction
    @Query("SELECT * FROM course_table WHERE termId = :id")
    suspend fun getDeletedCourses(id: Int): List<Course>

    @Transaction
    @Query("SELECT * FROM course_table WHERE termId = :id")
    fun getCourses(id: Int): Flow<List<Course>>
}