package com.mylittleproject.love42.di

import com.mylittleproject.love42.model.DataSource
import com.mylittleproject.love42.repository.SetProfileRepository
import com.mylittleproject.love42.repository.SetProfileRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideSetProfileRepository(remoteDataSource: DataSource.RemoteDataSource): SetProfileRepository =
        SetProfileRepositoryImpl(remoteDataSource)
}