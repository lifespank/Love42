package com.mylittleproject.love42.model

import com.mylittleproject.love42.data.AccessToken

interface DataSource {

    interface RemoteDataSource {

        suspend fun fetchAccessToken(code: String): Result<AccessToken>
    }
}