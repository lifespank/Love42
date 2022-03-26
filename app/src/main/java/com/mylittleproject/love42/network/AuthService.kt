package com.mylittleproject.love42.network

import com.mylittleproject.love42.data.AccessToken
import com.mylittleproject.love42.keys.REDIRECT_LINK
import com.mylittleproject.love42.keys.SEOUL_CLIENT_ID
import com.mylittleproject.love42.keys.SEOUL_SECRET
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthService {

    @POST("oauth/token")
    suspend fun fetchAccessToken(
        @Query("code") code: String,
        @Query("grant_type") grantType: String = "authorization_code",
        @Query("client_id") clientID: String = SEOUL_CLIENT_ID,
        @Query("client_secret") clientSecret: String = SEOUL_SECRET,
        @Query("redirect_uri") redirectURI: String = REDIRECT_LINK
    ): AccessToken
}