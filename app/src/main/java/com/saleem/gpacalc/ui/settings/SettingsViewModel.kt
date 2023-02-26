package com.saleem.gpacalc.ui.settings

import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.preference.Preference
import com.saleem.gpacalc.data.CourseDao
import com.saleem.gpacalc.data.preferencesmanager.GpaSystem
import com.saleem.gpacalc.data.preferencesmanager.PreferencesManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsViewModel @ViewModelInject constructor(
    private val preferencesManger: PreferencesManager
): ViewModel() {

    val preferencesFlow = preferencesManger.preferencesFlow



    fun onGpaSystemChanged(gpa: GpaSystem) = viewModelScope.launch {
        preferencesManger.updateGpaSystem(gpa)
    }

}