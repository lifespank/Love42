package com.mylittleproject.love42.repository

import android.util.Log
import com.mylittleproject.love42.model.DataSource
import com.mylittleproject.love42.tools.NAME_TAG

class AccessTokenRepositoryImpl(
    private val localDataSource: DataSource.LocalDataSource,
    private val remoteDataSource: DataSource.RemoteDataSource
) :
    AccessTokenRepository {

    private var token: String? = null

    override suspend fun fetchAccessToken(code: String?): String? {
        if (token != null) {
            return token
        }
        var tokenResult = localDataSource.fetchAccessToken()
        tokenResult.onSuccess {
            if (it != null) {
                Log.d(NAME_TAG, "Token fetched from local")
                return it
            }
        }
        tokenResult.onFailure {
            Log.e(NAME_TAG, "Auth token fetch failure", it)
        }
        if (code != null) {
            tokenResult = remoteDataSource.fetchAccessToken(code)
            tokenResult.onSuccess {
                token = it
                Log.d(NAME_TAG, "Token fetched from remote")
                saveAccessToken()
                return token
            }
            tokenResult.onFailure {
                Log.e(NAME_TAG, "Auth token fetch failure", it)
            }
        }
        return null
    }

    private suspend fun saveAccessToken() {
        val saveTokenResult = token?.let { localDataSource.saveAccessToken(it) }
        saveTokenResult?.onSuccess {
            Log.d(NAME_TAG, "Save token success")
        }
        saveTokenResult?.onFailure {
            Log.w(NAME_TAG, "Save token failure", it)
        }
    }
}