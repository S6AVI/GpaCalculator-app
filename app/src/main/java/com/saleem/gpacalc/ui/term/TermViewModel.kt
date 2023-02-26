package com.saleem.gpacalc.ui.term

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.saleem.gpacalc.R
import com.saleem.gpacalc.data.Course
import com.saleem.gpacalc.data.CourseDao
import com.saleem.gpacalc.data.Term
import com.saleem.gpacalc.data.TermWithCourses
import com.saleem.gpacalc.data.preferencesmanager.GpaSystem
import com.saleem.gpacalc.data.preferencesmanager.PreferencesManager
import com.saleem.gpacalc.ui.ADD_RESULT_OK
import com.saleem.gpacalc.ui.EDIT_RESULT_OK
import com.saleem.gpacalc.util.UiText
import com.saleem.gpacalc.util.calculateGpa
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TermViewModel @ViewModelInject constructor(
    private val dao: CourseDao,
    @Assisted private val state: SavedStateHandle,
    private val preferencesManger: PreferencesManager
): ViewModel() {



    val preferencesFlow = preferencesManger.preferencesFlow.asLiveData()

    var gpa: Double = 0.0
        get()  {
            if (preferencesFlow.value == GpaSystem.FIVE) {
                return field * 5/4
            }
            return field
        }

    val terms = dao.getTerms().asLiveData()
    val courses = dao.getCourses().asLiveData()


    private val termEventChannel = Channel<TermEvent>()
    val termEvent = termEventChannel.receiveAsFlow()

    fun onAddNewTermClick() = viewModelScope.launch {
        termEventChannel.send(TermEvent.NavigateToAddTermScreen)
    }

    fun onAddEditResult(result: Int) {
        when(result) {
            ADD_RESULT_OK -> showTermSavedConfirmationMessage(UiText.StringResource(
                resId = R.string.term_added
            ))
            EDIT_RESULT_OK -> showTermSavedConfirmationMessage(UiText.StringResource(
            resId = R.string.term_edited
        ))
        }
    }

    private fun showTermSavedConfirmationMessage(uiText: UiText) = viewModelScope.launch {
        termEventChannel.send(TermEvent.ShowTermSavedConfirmationMessage(uiText))
    }

    fun onTermSelected(term: Term) = viewModelScope.launch {
        termEventChannel.send(TermEvent.NavigateToCoursesScreen(term, term.name))
    }



    fun onTermSwipedLift(term: Term) = viewModelScope.launch {
        val deletedCourses = dao.getDeletedCourses(term.termId)
        dao.deleteAllCourses(term.termId)
        dao.delete(term)

        termEventChannel.send(TermEvent.ShowUndoDeleteTermMessage(term,deletedCourses))
    }

    fun onTermSwipedRight(term: Term) = viewModelScope.launch {
        termEventChannel.send(TermEvent.NavigateToEditTermScreen(term))
    }

    fun onUndoDeleteTerm(term: Term, courses: List<Course>) = viewModelScope.launch {
        dao.insert(term)
        courses.forEach {c -> dao.insert(c)}
    }

//    fun toCalculateGpa(it: List<Course>): Double {
//        val gpa = calculateGpa(it)
//        return gpa * 5/4
//    }

    sealed class TermEvent {
        object NavigateToAddTermScreen: TermEvent()
        data class NavigateToEditTermScreen(val term: Term): TermEvent()
        data class ShowTermSavedConfirmationMessage(val uiText: UiText): TermEvent()
        data class NavigateToCoursesScreen(val term: Term, val label: String): TermEvent()
        data class ShowUndoDeleteTermMessage(val term: Term, val courses: List<Course>): TermEvent()
    }
}