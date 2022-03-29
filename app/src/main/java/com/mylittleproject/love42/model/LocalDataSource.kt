package com.mylittleproject.love42.model

import android.util.Log
import com.mylittleproject.love42.data.AccessToken
import com.mylittleproject.love42.data.room.AccessTokenDao
import com.mylittleproject.love42.tools.NAME_TAG

class LocalDataSource(private val accessTokenDao: AccessTokenDao) : DataSource.LocalDataSource {

    override suspend fun fetchAccessToken(): Result<AccessToken> = runCatching {
        accessTokenDao.fetchAccessToken()
    }

    override suspend fun saveAccessToken(accessToken: AccessToken?): Result<Unit> = runCatching {
        Log.d(NAME_TAG, "Saving token...:$accessToken")
        if (accessToken != null) {
            accessTokenDao.insert(accessToken)
        } else {
            Log.d(NAME_TAG, "Token deleted")
            accessTokenDao.deleteAccessToken()
        }
    }
}