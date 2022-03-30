package com.mylittleproject.love42.repository

import com.mylittleproject.love42.data.AccessToken

interface PrivateInfoRepository {

    suspend fun fetchAccessToken(
        code: String? = null,
        refreshToken: String? = null,
        grantType: String = "authorization_code"
    ): AccessToken?

    suspend fun saveAccessToken(accessToken: AccessToken? = null): Result<Unit>

    suspend fun saveIntraID(intraID: String): Result<Unit>
}