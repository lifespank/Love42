package com.mylittleproject.love42.di

import android.content.Context
import com.mylittleproject.love42.tools.ImageCompressor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CompressorModule {

    @Singleton
    @Provides
    fun provideImageCompressor(@ApplicationContext context: Context): ImageCompressor {
        return ImageCompressor(context)
    }
}