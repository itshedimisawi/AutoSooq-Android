package com.example.googlelogindemo.di

import android.content.Context
import android.content.SharedPreferences
import com.example.googlelogindemo.network.APIService
import com.example.googlelogindemo.repository.Repository
import com.example.googlelogindemo.repository.Repository_Impl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    @Named("token_inject")
    fun provideToken(@ApplicationContext context: Context): String? {
        return context.getSharedPreferences("STUFF_STORAGE", Context.MODE_PRIVATE)
            .getString("TOKEN", "")

    }

    @Singleton
    @Provides
    fun provideAuthRepository(
        apiservice: APIService,
        @Named("token_inject") token: String?
    ): Repository {
        return Repository_Impl(apiservice, token)
    }

    @Singleton
    @Provides
    @Named("preferences_inject")
    fun provideSharedPreference(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("STUFF_STORAGE", Context.MODE_PRIVATE)
    }


}