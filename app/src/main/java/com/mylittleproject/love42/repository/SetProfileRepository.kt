package com.mylittleproject.love42.repository

interface SetProfileRepository {

    suspend fun fetchAccessToken(code: String): String?
}