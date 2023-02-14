package com.saleem.gpacalc.ui.subject

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.saleem.gpacalc.data.Course
import com.saleem.gpacalc.data.CourseDao
import com.saleem.gpacalc.ui.ADD_TASK_RESULT_OK
import com.saleem.gpacalc.ui.EDIT_TASK_RESULT_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.Exception

class SubjectViewModel @ViewModelInject constructor(
    private val courseDao: CourseDao,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {

    // get the flow of courses and cast it to liveDate to observe it in the fragment
    val courses = courseDao.getCourses().asLiveData()


    // channel to deal with events
    private val subjectEventChannel = Channel<SubjectEvent>()
    val subjectEvent = subjectEventChannel.receiveAsFlow()

    // functions to handle actions
    fun onCourseSelected(course: Course) = viewModelScope.launch {
        subjectEventChannel.send(SubjectEvent.NavigateToEditSubjectScreen(course))
    }

    fun onAddNewCourseClick() = viewModelScope.launch {
        subjectEventChannel.send(SubjectEvent.NavigateToAddSubjectScreen)
    }

    fun onAddEditResult(result: Int) {
        when (result) {
            ADD_TASK_RESULT_OK -> showCourseSavedConfirmationMessage("Course Added")
            EDIT_TASK_RESULT_OK -> showCourseSavedConfirmationMessage("Course Edited")
        }

    }

    private fun showCourseSavedConfirmationMessage(text: String) = viewModelScope.launch {
        subjectEventChannel.send(SubjectEvent.ShowCourseSavedConfirmationMessage(text))
    }

    fun onCourseSwiped(course: Course) = viewModelScope.launch {
        courseDao.delete(course)
        subjectEventChannel.send(SubjectEvent.ShowUndoDeleteCourseMessage(course))
    }

    fun onUndoDeleteCourse(course: Course) = viewModelScope.launch {
        courseDao.insert(course)
    }

    fun onDeleteAllCoursesClick() = viewModelScope.launch {
        subjectEventChannel.send(SubjectEvent.NavigateToDeleteAllCoursesScreen)
    }


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

//    // calculate the gpa out of 4 and round it to two decimal points
//    fun calculateGpa(): Double {
//        var sum = 0.0
//        var allHours = 0
//
//
//        viewModelScope.launch {
//            courses.asFlow().collect {
//                it.forEach { c ->
//                    sum += c.gradeFormatted * c.credit_hours
//                    allHours += c.credit_hours * 4
//                }
//            }
//        }
//            courses.asFlow().first().toList().forEach { c ->
//                sum += c.gradeFormatted * c.credit_hours
//                allHours += c.credit_hours * 4
//            }
//        }
//        return if (allHours > 0) {
//            String.format("%.2f", sum / allHours * 4).toDouble()
//        } else {
//            0.0
//        }
//    }


    // to wrap all events
    sealed class SubjectEvent {
        object NavigateToAddSubjectScreen : SubjectEvent()
        data class NavigateToEditSubjectScreen(val course: Course) : SubjectEvent()
        data class ShowCourseSavedConfirmationMessage(val msg: String) : SubjectEvent()
        data class ShowUndoDeleteCourseMessage(val course: Course) : SubjectEvent()
        object NavigateToDeleteAllCoursesScreen : SubjectEvent()
    }
}