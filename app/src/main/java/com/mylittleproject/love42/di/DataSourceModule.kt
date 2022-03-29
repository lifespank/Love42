package com.mylittleproject.love42.di

import com.google.firebase.storage.FirebaseStorage
import com.mylittleproject.love42.data.room.AccessTokenDao
import com.mylittleproject.love42.model.DataSource
import com.mylittleproject.love42.model.LocalDataSource
import com.mylittleproject.love42.model.RemoteDataSource
import com.mylittleproject.love42.network.IntraService
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
    fun provideRemoteDataSource(intraService: IntraService, storage: FirebaseStorage): DataSource.RemoteDataSource =
        RemoteDataSource(intraService, storage)

    @Singleton
    @Provides
    fun provideLocalDataSource(accessTokenDao: AccessTokenDao): DataSource.LocalDataSource =
        LocalDataSource(accessTokenDao)
}