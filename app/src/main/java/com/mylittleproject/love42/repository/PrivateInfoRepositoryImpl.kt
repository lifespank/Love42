package com.mylittleproject.love42.repository

import android.util.Log
import com.mylittleproject.love42.data.AccessToken
import com.mylittleproject.love42.model.DataSource
import com.mylittleproject.love42.tools.NAME_TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PrivateInfoRepositoryImpl(
    private val localDataSource: DataSource.LocalDataSource,
    private val remoteDataSource: DataSource.RemoteDataSource
) :
    PrivateInfoRepository {

    override suspend fun fetchAccessToken(
        code: String?,
        refreshToken: String?,
        grantType: String
    ): AccessToken? {
        var token: Result<AccessToken>
        withContext(Dispatchers.IO) {
            token = localDataSource.fetchAccessToken()
            token.onSuccess {
                Log.d(NAME_TAG, "Token fetched from local: $it")
            }
            token.onFailure {
                Log.w(NAME_TAG, "No token in local", it)
            }
            if (token.getOrNull() == null || token.isFailure) {
                token = remoteDataSource.fetchAccessToken(code, refreshToken, grantType)
                token.onSuccess { fetchedToken ->
                    Log.d(NAME_TAG, "Token fetched from remote: $fetchedToken")
                    saveAccessToken(fetchedToken)
                }
                token.onFailure { throwable ->
                    Log.w(NAME_TAG, "Token fetch failed from remote", throwable)
                }

            }
        }
        return token.getOrNull()
    }

    override suspend fun saveAccessToken(accessToken: AccessToken?): Result<Unit> = runCatching {
        withContext(Dispatchers.IO) {
            val result = localDataSource.saveAccessToken(accessToken)
            result.onSuccess {
                Log.d(NAME_TAG, "Token save success")
            }
            result.onFailure {
                Log.d(NAME_TAG, "Token save failure", it)
            }
        }
    }

    override suspend fun saveIntraID(intraID: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            localDataSource.saveIntraID(intraID)
        }

}