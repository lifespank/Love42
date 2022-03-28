package com.mylittleproject.love42.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mylittleproject.love42.data.AccessToken

@Dao
interface AccessTokenDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(accessToken: AccessToken): Long

    @Query("SELECT * FROM `access token` WHERE id = 0")
    suspend fun fetchAccessToken(): AccessToken

    @Query("DELETE FROM `access token` WHERE id = 0")
    suspend fun deleteAccessToken(): Int
}