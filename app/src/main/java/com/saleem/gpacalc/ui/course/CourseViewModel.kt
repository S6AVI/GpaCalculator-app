package com.saleem.gpacalc.ui.course

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.saleem.gpacalc.R
import com.saleem.gpacalc.data.Course
import com.saleem.gpacalc.data.CourseDao
import com.saleem.gpacalc.data.Term
import com.saleem.gpacalc.data.preferencesmanager.GpaSystem
import com.saleem.gpacalc.data.preferencesmanager.PreferencesManager
import com.saleem.gpacalc.ui.ADD_RESULT_OK
import com.saleem.gpacalc.ui.EDIT_RESULT_OK
import com.saleem.gpacalc.util.UiText
import com.saleem.gpacalc.util.calculateGpa
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CourseViewModel @ViewModelInject constructor(
    private val courseDao: CourseDao,
    @Assisted private val state: SavedStateHandle,
    private val preferencesManger: PreferencesManager
) : ViewModel() {


    val preferencesFlow = preferencesManger.preferencesFlow.asLiveData()

    var gpa: Double = 0.0
        get()  {
            if (preferencesFlow.value == GpaSystem.FIVE) {
                return field * 5/4
            }
            return field
        }

    // get the flow of courses and cast it to liveDate to observe it in the fragment
    val term = state.get<Term>("term")

    val courses = courseDao.getCourses(term!!.termId).asLiveData()


    // channel to deal with events
    private val courseEventChannel = Channel<CourseEvent>()
    val courseEvent = courseEventChannel.receiveAsFlow()

    // functions to handle actions
    fun onCourseSelected(course: Course) = viewModelScope.launch {
        courseEventChannel.send(CourseEvent.NavigateToEditCourseScreen(course, term!!))
    }

    fun onAddNewCourseClick() = viewModelScope.launch {
        courseEventChannel.send(CourseEvent.NavigateToAddCourseScreen(term!!))
    }

    fun onAddEditResult(result: Int) {
        when (result) {
            ADD_RESULT_OK -> showCourseSavedConfirmationMessage(UiText.StringResource(
                R.string.course_added
            ))
            EDIT_RESULT_OK -> showCourseSavedConfirmationMessage(UiText.StringResource(
                R.string.course_edited
            ))
        }

    }

    private fun showCourseSavedConfirmationMessage(uiText: UiText) = viewModelScope.launch {
        courseEventChannel.send(CourseEvent.ShowCourseSavedConfirmationMessage(uiText))
    }

    fun onCourseSwiped(course: Course) = viewModelScope.launch {
        courseDao.delete(course)
        courseEventChannel.send(CourseEvent.ShowUndoDeleteCourseMessage(course))
    }

    fun onUndoDeleteCourse(course: Course) = viewModelScope.launch {
        courseDao.insert(course)
    }

    fun onDeleteAllCoursesClick() = viewModelScope.launch {
        courseEventChannel.send(CourseEvent.NavigateToDeleteAllCoursesScreen(term!!.termId))
    }

    fun updateTermGpa(termGpa: Double) = viewModelScope.launch {
        courseDao.update(term!!.copy(
            gpa = termGpa
        ))
    }


    // to wrap all events
    sealed class CourseEvent {
        data class NavigateToAddCourseScreen(val term: Term) : CourseEvent()
        data class NavigateToEditCourseScreen(val course: Course, val term: Term) : CourseEvent()
        data class ShowCourseSavedConfirmationMessage(val uiText: UiText) : CourseEvent()
        data class ShowUndoDeleteCourseMessage(val course: Course) : CourseEvent()
        data class NavigateToDeleteAllCoursesScreen(val id: Int) : CourseEvent()
    }
}