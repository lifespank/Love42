package com.mylittleproject.love42.repository

interface AccessTokenRepository {

    suspend fun fetchAccessToken(code: String? = null): String?
    suspend fun saveAccessToken(accessToken: String? = null): Result<Unit>
}