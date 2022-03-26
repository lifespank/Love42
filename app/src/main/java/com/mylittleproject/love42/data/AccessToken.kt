package com.mylittleproject.love42.data

import com.google.gson.annotations.SerializedName

data class AccessToken(
    @SerializedName("access_token")
    val accessToken: String = "",
    @SerializedName("token_type")
    val tokenType: String = "",
    @SerializedName("expires_in")
    val expiresIn: Int = 0,
    @SerializedName("refresh_token")
    val refreshToken: String = "",
    @SerializedName("scope")
    val scope: String = "",
    @SerializedName("created_at")
    val createdAt: Int = 0
)