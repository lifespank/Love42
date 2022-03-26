package com.mylittleproject.love42.repository

interface AccessTokenRepository {

    suspend fun fetchAccessToken(code: String? = null): String?
}