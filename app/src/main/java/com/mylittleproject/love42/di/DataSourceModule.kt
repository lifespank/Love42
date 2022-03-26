package com.mylittleproject.love42.di

import com.mylittleproject.love42.model.DataSource
import com.mylittleproject.love42.model.RemoteDataSource
import com.mylittleproject.love42.network.AuthService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Singleton
    @Provides
    fun provideRemoteDataSource(authService: AuthService): DataSource.RemoteDataSource =
        RemoteDataSource(authService)
}