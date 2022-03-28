package com.mylittleproject.love42.model

import com.mylittleproject.love42.data.UserInfo

interface DataSource {

    interface RemoteDataSource {

        suspend fun fetchAccessToken(code: String): Result<String>
        suspend fun fetchUserInfo(accessToken: String): Result<UserInfo>
    }

    interface LocalDataSource {

        suspend fun fetchAccessToken(): Result<String?>
        suspend fun saveAccessToken(accessToken: String?): Result<Unit>
    }
}