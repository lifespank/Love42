package com.mylittleproject.love42.repository

import com.mylittleproject.love42.data.AccessToken

interface AccessTokenRepository {

    suspend fun fetchAccessToken(code: String? = null): AccessToken?
    suspend fun saveAccessToken(accessToken: AccessToken? = null): Result<Unit>
}