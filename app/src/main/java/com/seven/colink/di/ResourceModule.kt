package com.seven.colink.di

import android.content.Context
import com.google.android.datatransport.runtime.dagger.Provides
import com.seven.colink.data.source.repository.ResourceRepositoryImpl
import com.seven.colink.domain.repository.ResourceRepository
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ViewModelComponent::class)
object ResourceModule {

    @Provides
    fun provideContext(@ApplicationContext appContext: Context) = appContext
    @Provides
    fun provideStringsRepository(context: Context): ResourceRepository =
        ResourceRepositoryImpl(context)

}