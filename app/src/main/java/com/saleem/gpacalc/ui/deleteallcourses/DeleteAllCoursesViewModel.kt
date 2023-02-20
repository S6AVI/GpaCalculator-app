package com.saleem.gpacalc.ui.deleteallcourses

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saleem.gpacalc.data.CourseDao
import com.saleem.gpacalc.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DeleteAllCoursesViewModel @ViewModelInject constructor(
    private val courseDao: CourseDao,
    @ApplicationScope private val applicationScope: CoroutineScope,
    @Assisted private val state: SavedStateHandle

) : ViewModel() {

    val termId = state.get<Int>("termId")

    fun onConfirmClick() = applicationScope.launch {
        courseDao.deleteAllCourses(termId!!)
    }
}