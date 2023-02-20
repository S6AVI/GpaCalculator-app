package com.saleem.gpacalc.ui.addeditcourse

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saleem.gpacalc.data.Course
import com.saleem.gpacalc.data.CourseDao
import com.saleem.gpacalc.data.Term
import com.saleem.gpacalc.ui.ADD_RESULT_OK
import com.saleem.gpacalc.ui.EDIT_RESULT_OK
import com.saleem.gpacalc.util.possibleGrades
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch


class AddEditCourseViewModel @ViewModelInject constructor(
    private val courseDao: CourseDao,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {




    val course = state.get<Course>("course")
    val term = state.get<Term>("term")

    var courseName = state.get<String>("courseName") ?: course?.name ?: ""
        set(value) {
            field = value
            state.set("courseName", value)
        }

    var courseHours = state.get<String>("courseHours") ?: course?.credit_hours?.toString() ?: ""
        set(value) {
            field = value
            state.set("courseHours", value)
        }
    var courseGrade = state.get<String>("courseGrade") ?: course?.grade ?: ""
        set(value) {
            field = value
            state.set("courseGrade", value)
        }

    private val addEditCourseEventChannel = Channel<AddEditCourseEvent>()
    val addEditTaskEvent = addEditCourseEventChannel.receiveAsFlow()

    fun onSaveClick() {
        if (courseName.isBlank() || courseGrade.isBlank() || courseHours.isBlank()) {
            showInvalidInputMessage("Missing fields")
            return
        }

        if (!possibleGrades.contains(courseGrade) || courseHours.toInt() < 0 || courseHours.toInt() > 8) {
            showInvalidInputMessage("Invalid inputs")
            return
        }

        if (course != null) {
            val updatedCourse = course.copy(
                name = courseName,
                credit_hours = Integer.parseInt(courseHours),
                grade = courseGrade
            )
            updateCourse(updatedCourse)
        } else {
            val newCourse = Course(
                name = courseName,
                credit_hours = Integer.parseInt(courseHours),
                grade = courseGrade,
                termId = term!!.termId
            )
            createCourse(newCourse)
        }
    }

    private fun updateCourse(course: Course) = viewModelScope.launch {
        courseDao.update(course)
        addEditCourseEventChannel.send(AddEditCourseEvent.NavigateBackWithResult(EDIT_RESULT_OK))
    }



    private fun createCourse(newCourse: Course) = viewModelScope.launch {
        courseDao.insert(newCourse)
        addEditCourseEventChannel.send(AddEditCourseEvent.NavigateBackWithResult(ADD_RESULT_OK))
    }

    private fun showInvalidInputMessage(text: String) = viewModelScope.launch {
        addEditCourseEventChannel.send(AddEditCourseEvent.ShowInvalidInputMessage(text))
    }


    sealed class AddEditCourseEvent {
        data class NavigateBackWithResult(val result: Int): AddEditCourseEvent()
        data class ShowInvalidInputMessage(val msg: String): AddEditCourseEvent()
    }

}