package com.mylittleproject.love42.di

import com.mylittleproject.love42.model.DataSource
import com.mylittleproject.love42.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun providePrivateInfoRepository(
        localDataSource: DataSource.LocalDataSource,
        remoteDataSource: DataSource.RemoteDataSource
    ): PrivateInfoRepository =
        PrivateInfoRepositoryImpl(localDataSource, remoteDataSource, Dispatchers.IO)

    @Singleton
    @Provides
    fun provideIntraRepository(remoteDataSource: DataSource.RemoteDataSource): IntraRepository =
        IntraRepositoryImpl(remoteDataSource)

    @Singleton
    @Provides
    fun provideFirebaseRepository(remoteDataSource: DataSource.RemoteDataSource): FirebaseRepository =
        FirebaseRepositoryImpl(remoteDataSource, Dispatchers.IO)
}