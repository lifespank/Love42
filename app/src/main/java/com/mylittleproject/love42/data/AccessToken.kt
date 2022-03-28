package com.mylittleproject.love42.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "access token")
data class AccessToken(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @SerializedName("access_token")
    @ColumnInfo(name = "access_token")
    val accessToken: String = "",
    @SerializedName("token_type")
    @ColumnInfo(name = "token_type")
    val tokenType: String = "",
    @SerializedName("expires_in")
    @ColumnInfo(name = "expires_in")
    val expiresIn: Int = 0,
    @SerializedName("refresh_token")
    @ColumnInfo(name = "refresh_token")
    val refreshToken: String = "",
    @SerializedName("scope")
    @ColumnInfo(name = "scope")
    val scope: String = "",
    @SerializedName("created_at")
    @ColumnInfo(name = "created_at")
    val createdAt: Int = 0
)