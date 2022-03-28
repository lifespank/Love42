package com.mylittleproject.love42.model

import com.mylittleproject.love42.network.IntraService

class RemoteDataSource(private val intraService: IntraService) :
    DataSource.RemoteDataSource {

    override suspend fun fetchAccessToken(code: String): Result<String> = runCatching {
        intraService.fetchAccessToken(code).accessToken
    }

    override suspend fun fetchUserInfo(accessToken: String) = runCatching {
        intraService.fetchUserInfo(accessToken)
    }
}