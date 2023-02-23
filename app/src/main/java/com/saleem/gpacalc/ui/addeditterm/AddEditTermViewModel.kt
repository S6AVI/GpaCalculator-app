package com.saleem.gpacalc.ui.addeditterm

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saleem.gpacalc.R
import com.saleem.gpacalc.data.CourseDao
import com.saleem.gpacalc.data.Term
import com.saleem.gpacalc.ui.ADD_RESULT_OK
import com.saleem.gpacalc.ui.EDIT_RESULT_OK
import com.saleem.gpacalc.util.UiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AddEditTermViewModel @ViewModelInject constructor(
    val dao: CourseDao,
    @Assisted val state: SavedStateHandle
) : ViewModel() {

    val term = state.get<Term>("term")

    var termName = state.get<String>("termName") ?: term?.name ?: ""
        set(value) {
            field = value
            state.set("termName", value)
        }


    private val addEditTermChannel = Channel<AddEditTermEvent>()
    val addEditTermEvent = addEditTermChannel.receiveAsFlow()

    fun onSaveClick() {
        if (termName.isBlank()) {
            showInvalidInputMessage(UiText.StringResource(
                R.string.name_required
            ))
            return
        }
        if (term != null) {
            val updatedTerm = term.copy(name = termName)
            updateTerm(updatedTerm)
        } else {
            val newTerm = Term(
                name = termName,
                gpa = 0.0
            )
            createTerm(newTerm)
        }
    }

    private fun updateTerm(term: Term) = viewModelScope.launch {
        dao.update(term)
        addEditTermChannel.send(AddEditTermEvent.NavigateBackWithResult(EDIT_RESULT_OK))
    }

    private fun createTerm(newTerm: Term) = viewModelScope.launch {
        dao.insert(newTerm)
        addEditTermChannel.send(AddEditTermEvent.NavigateBackWithResult(ADD_RESULT_OK))
    }

    private fun showInvalidInputMessage(uiText: UiText) = viewModelScope.launch {
        addEditTermChannel.send(AddEditTermEvent.ShowInvalidInputMessage(uiText))
    }

    sealed class AddEditTermEvent {
        data class NavigateBackWithResult(val result: Int) : AddEditTermEvent()
        data class ShowInvalidInputMessage(val uiText: UiText) : AddEditTermEvent()
    }


}