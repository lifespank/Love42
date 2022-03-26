package com.mylittleproject.love42.repository

import android.util.Log
import com.mylittleproject.love42.data.UserInfo
import com.mylittleproject.love42.model.DataSource
import com.mylittleproject.love42.tools.NAME_TAG

class IntraRepositoryImpl(private val remoteDataSource: DataSource.RemoteDataSource) :
    IntraRepository {

    override suspend fun fetchUserInfo(accessToken: String): UserInfo? {
        val userResult = remoteDataSource.fetchUserInfo(accessToken)
        userResult.onSuccess {
            Log.d(NAME_TAG, "User fetched: $it")
            return it
        }
        userResult.onFailure {
            Log.w(NAME_TAG, "User fetch failure", it)
        }
        return null
    }
}