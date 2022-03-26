package com.mylittleproject.love42.di

import com.mylittleproject.love42.model.DataSource
import com.mylittleproject.love42.repository.AccessTokenRepository
import com.mylittleproject.love42.repository.AccessTokenRepositoryImpl
import com.mylittleproject.love42.repository.IntraRepository
import com.mylittleproject.love42.repository.IntraRepositoryImpl
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
    fun provideAccessTokenRepository(
        localDataSource: DataSource.LocalDataSource,
        remoteDataSource: DataSource.RemoteDataSource
    ): AccessTokenRepository =
        AccessTokenRepositoryImpl(localDataSource, remoteDataSource)

    @Singleton
    @Provides
    fun provideIntraRepository(remoteDataSource: DataSource.RemoteDataSource): IntraRepository =
        IntraRepositoryImpl(remoteDataSource)
}