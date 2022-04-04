package com.mylittleproject.love42.data

import com.google.gson.annotations.SerializedName

data class UserInfo(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("email")
    val email: String = "",
    @SerializedName("login")
    val login: String = "",
    @SerializedName("url")
    val url: String = "",
    @SerializedName("displayname")
    val displayName: String = "",
    @SerializedName("image_url")
    val imageUrl: String = "",
    @SerializedName("campus")
    val campus: List<Campus>
) {

    fun toDetailedUserInfo() =
        if (campus.firstOrNull() == null) {
            DetailedUserInfo(displayName, login, url, imageUrl, email, "")
        } else {
            DetailedUserInfo(displayName, login, url, imageUrl, email, campus.first().name)
        }
}

data class Campus(
    @SerializedName("name")
    val name: String = "",
)