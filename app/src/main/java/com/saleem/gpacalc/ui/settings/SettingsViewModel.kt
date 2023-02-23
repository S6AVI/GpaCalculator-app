package com.saleem.gpacalc.ui.settings

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.saleem.gpacalc.data.CourseDao

class SettingsViewModel @ViewModelInject constructor(
    private val dao: CourseDao
): ViewModel() {
}