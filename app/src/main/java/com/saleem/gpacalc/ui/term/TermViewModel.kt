package com.saleem.gpacalc.ui.term

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.saleem.gpacalc.data.Course
import com.saleem.gpacalc.data.CourseDao
import com.saleem.gpacalc.data.Term
import com.saleem.gpacalc.data.TermWithCourses
import com.saleem.gpacalc.ui.ADD_RESULT_OK
import com.saleem.gpacalc.ui.EDIT_RESULT_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TermViewModel @ViewModelInject constructor(
    private val dao: CourseDao,
    @Assisted private val state: SavedStateHandle
): ViewModel() {


    val terms = dao.getTerms().asLiveData()
    val courses = dao.getCourses().asLiveData()


    private val termEventChannel = Channel<TermEvent>()
    val termEvent = termEventChannel.receiveAsFlow()

    fun onAddNewTermClick() = viewModelScope.launch {
        termEventChannel.send(TermEvent.NavigateToAddTermScreen)
    }

    fun onAddEditResult(result: Int) {
        when(result) {
            ADD_RESULT_OK -> showTermSavedConfirmationMessage("Term Added")
            EDIT_RESULT_OK -> showTermSavedConfirmationMessage("Term Name Edited")
        }
    }

    private fun showTermSavedConfirmationMessage(text: String) = viewModelScope.launch {
        termEventChannel.send(TermEvent.ShowTermSavedConfirmationMessage(text))
    }

    fun onTermSelected(term: Term) = viewModelScope.launch {
        termEventChannel.send(TermEvent.NavigateToCoursesScreen(term, term.name))
    }

    fun onDeleteAllTermsClick() = viewModelScope.launch {
        termEventChannel.send(TermEvent.NavigateToDeleteAllTermsScreen)
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


    sealed class TermEvent {
        object NavigateToAddTermScreen: TermEvent()
        data class NavigateToEditTermScreen(val term: Term): TermEvent()
        data class ShowTermSavedConfirmationMessage(val msg: String): TermEvent()
        data class NavigateToCoursesScreen(val term: Term, val label: String): TermEvent()
        object NavigateToDeleteAllTermsScreen: TermEvent()
        data class ShowUndoDeleteTermMessage(val term: Term, val courses: List<Course>): TermEvent()
    }
}