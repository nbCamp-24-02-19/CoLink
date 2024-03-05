package com.seven.colink.di

import com.algolia.search.saas.Client
import com.algolia.search.saas.Index
import com.seven.colink.BuildConfig
import com.seven.colink.data.firebase.type.DataBaseType
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AlgoliaModule {

    @Provides
    @Singleton
    fun provideAlgoliaClient(): Client {
        return Client(BuildConfig.ALGOLIA_APP_ID, BuildConfig.ALGOLIA_API_KEY)
    }

    @Provides
    fun provideAlgoliaIndex(client: Client): Index {
        return client.getIndex(DataBaseType.POST.title)
    }
}
