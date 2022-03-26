package com.mylittleproject.love42.repository

import com.mylittleproject.love42.data.AccessToken

interface SetProfileRepository {

    suspend fun fetchAccessToken(code: String): AccessToken?
}