package com.mylittleproject.love42.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mylittleproject.love42.data.AccessToken

@Database(entities = [AccessToken::class], version = 1)
abstract class AccessTokenDatabase : RoomDatabase() {
    abstract fun accessTokenDao(): AccessTokenDao

    companion object {

        const val DATABASE_NAME = "access_token_db"
    }
}