package com.mylittleproject.love42.model

import com.mylittleproject.love42.network.AuthService

class RemoteDataSource(private val authService: AuthService) : DataSource.RemoteDataSource {

    override suspend fun fetchAccessToken(code: String): Result<String> = runCatching {
        authService.fetchAccessToken(code).accessToken
    }
}