package com.mylittleproject.love42.di

import android.content.Context
import androidx.room.Room
import com.mylittleproject.love42.data.room.AccessTokenDao
import com.mylittleproject.love42.data.room.AccessTokenDatabase
import com.mylittleproject.love42.data.room.AccessTokenDatabase.Companion.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Singleton
    @Provides
    fun provideAccessTokenDB(@ApplicationContext context: Context): AccessTokenDatabase {
        return Room.databaseBuilder(
            context,
            AccessTokenDatabase::class.java,
            DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideAccessTokenDao(accessTokenDatabase: AccessTokenDatabase): AccessTokenDao {
        return accessTokenDatabase.accessTokenDao()
    }
}