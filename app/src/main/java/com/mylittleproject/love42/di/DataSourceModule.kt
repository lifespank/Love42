package com.mylittleproject.love42.di

import android.content.Context
import com.mylittleproject.love42.model.DataSource
import com.mylittleproject.love42.model.LocalDataSource
import com.mylittleproject.love42.model.RemoteDataSource
import com.mylittleproject.love42.network.IntraService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Singleton
    @Provides
    fun provideRemoteDataSource(intraService: IntraService): DataSource.RemoteDataSource =
        RemoteDataSource(intraService)

    @Singleton
    @Provides
    fun provideLocalDataSource(@ApplicationContext context: Context): DataSource.LocalDataSource =
        LocalDataSource(context)
}