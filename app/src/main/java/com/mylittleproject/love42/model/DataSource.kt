package com.mylittleproject.love42.model

interface DataSource {

    interface RemoteDataSource {

        suspend fun fetchAccessToken(code: String): Result<String>
    }

    interface LocalDataSource {

        suspend fun fetchAccessToken(): Result<String?>
        suspend fun saveAccessToken(accessToken: String): Result<Unit>
    }
}