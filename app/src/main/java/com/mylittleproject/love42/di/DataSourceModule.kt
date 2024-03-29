package com.mylittleproject.love42.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.mylittleproject.love42.data.room.AccessTokenDao
import com.mylittleproject.love42.model.DataSource
import com.mylittleproject.love42.model.LocalDataSource
import com.mylittleproject.love42.model.RemoteDataSource
import com.mylittleproject.love42.network.IntraService
import com.mylittleproject.love42.tools.ImageCompressor
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
    fun provideRemoteDataSource(
        intraService: IntraService,
        storage: FirebaseStorage,
        db: FirebaseFirestore,
        imageCompressor: ImageCompressor
    ): DataSource.RemoteDataSource =
        RemoteDataSource(intraService, storage, db, imageCompressor)

    @Singleton
    @Provides
    fun provideLocalDataSource(
        accessTokenDao: AccessTokenDao,
        sharedPreferences: SharedPreferences
    ): DataSource.LocalDataSource =
        LocalDataSource(accessTokenDao, sharedPreferences)

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        return EncryptedSharedPreferences.create(
            context,
            FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private const val FILE_NAME = "IntraID"
}