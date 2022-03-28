package com.mylittleproject.love42.model

import com.mylittleproject.love42.data.AccessToken
import com.mylittleproject.love42.data.UserInfo

interface DataSource {

    interface RemoteDataSource {

        suspend fun fetchAccessToken(code: String): Result<AccessToken>
        suspend fun fetchUserInfo(accessToken: String): Result<UserInfo>
    }

    interface LocalDataSource {

        suspend fun fetchAccessToken(): Result<AccessToken>
        suspend fun saveAccessToken(accessToken: AccessToken? = null): Result<Unit>
    }
}