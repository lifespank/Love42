package com.mylittleproject.love42.repository

import android.util.Log
import com.mylittleproject.love42.data.AccessToken
import com.mylittleproject.love42.model.DataSource
import com.mylittleproject.love42.tools.NAME_TAG

class SetProfileRepositoryImpl(private val dataSource: DataSource.RemoteDataSource) :
    SetProfileRepository {

    override suspend fun fetchAccessToken(code: String): AccessToken? {
        val token = dataSource.fetchAccessToken(code)
        token.onSuccess {
            return it
        }
        token.onFailure {
            Log.e(NAME_TAG, "Auth token fetch failure", it)
        }
        return null
    }
}