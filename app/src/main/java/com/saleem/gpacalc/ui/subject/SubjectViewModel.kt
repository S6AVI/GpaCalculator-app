package com.saleem.gpacalc.ui.subject

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.saleem.gpacalc.data.CourseDao

class SubjectViewModel @ViewModelInject constructor(
    private val courseDao: CourseDao
) : ViewModel(){

    val courses = courseDao.getCourses().asLiveData()
}