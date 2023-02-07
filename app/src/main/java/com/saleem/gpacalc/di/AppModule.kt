package com.saleem.gpacalc.di

import android.app.Application
import androidx.room.Room
import com.saleem.gpacalc.data.CourseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
object AppModule {


    @Provides
    @Singleton
    fun provideDatabase(
        app: Application
    ) = Room.databaseBuilder(
        app,
        CourseDatabase::class.java,
        "course_database"
    )
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun provideCourseDao(db:CourseDatabase) = db.courseDao()


    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())

}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope