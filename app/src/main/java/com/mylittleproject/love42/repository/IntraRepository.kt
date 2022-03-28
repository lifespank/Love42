package com.mylittleproject.love42.repository

import com.mylittleproject.love42.data.UserInfo

interface IntraRepository {

    suspend fun fetchUserInfo(accessToken: String): Result<UserInfo>
}