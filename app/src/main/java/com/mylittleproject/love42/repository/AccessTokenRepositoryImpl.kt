package com.mylittleproject.love42.repository

import android.util.Log
import com.mylittleproject.love42.model.DataSource
import com.mylittleproject.love42.tools.NAME_TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AccessTokenRepositoryImpl(
    private val localDataSource: DataSource.LocalDataSource,
    private val remoteDataSource: DataSource.RemoteDataSource
) :
    AccessTokenRepository {

    override suspend fun fetchAccessToken(code: String?): String? {
        var token: Result<String?>
        withContext(Dispatchers.IO) {
            token = localDataSource.fetchAccessToken()
            token.onSuccess {
                Log.d(NAME_TAG, "Token fetched from local: $it")
            }
            token.onFailure {
                Log.w(NAME_TAG, "No token in local", it)
            }
            if (token.getOrNull() == null || token.isFailure) {
                code?.let {
                    token = remoteDataSource.fetchAccessToken(it)
                    token.onSuccess { fetchedToken ->
                        Log.d(NAME_TAG, "Token fetched from remote")
                        saveAccessToken(fetchedToken)
                    }
                    token.onFailure { throwable ->
                        Log.w(NAME_TAG, "Token fetch failed from remote", throwable)
                    }
                }
            }
        }
        return token.getOrNull()
    }

    override suspend fun saveAccessToken(accessToken: String?): Result<Unit> = runCatching {
        withContext(Dispatchers.IO) {
            localDataSource.saveAccessToken(accessToken)
        }
    }
}