package com.saleem.gpacalc.data.preferencesmanager

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.emptyPreferences
import androidx.datastore.preferences.preferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton


private const val TAG = "PreferencesManager"

enum class GpaSystem { FOUR, FIVE}

//data class FilterPreferences(val gpa: Int)

@Singleton
class PreferencesManager @Inject constructor(@ApplicationContext context: Context){

    private val dataStore = context.createDataStore("user_preferences")

    val preferencesFlow = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->

            val gpaSystem = GpaSystem.valueOf(
                preferences[PreferencesKeys.GPA_SYSTEM] ?: GpaSystem.FOUR.name
            )


            gpaSystem
        }


    suspend fun updateGpaSystem(gpaSystem: GpaSystem) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.GPA_SYSTEM] = gpaSystem.name
        }
    }
    private object PreferencesKeys {
        val GPA_SYSTEM = preferencesKey<String>("gpa_system")


    }

}