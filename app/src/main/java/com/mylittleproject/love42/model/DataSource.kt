package com.mylittleproject.love42.model

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import com.mylittleproject.love42.data.AccessToken
import com.mylittleproject.love42.data.DetailedUserInfo
import com.mylittleproject.love42.data.UserInfo

interface DataSource {

    interface RemoteDataSource {

        suspend fun fetchAccessToken(
            code: String?,
            refreshToken: String?,
            grantType: String
        ): Result<AccessToken>

        suspend fun fetchUserInfo(accessToken: String): Result<UserInfo>

        suspend fun uploadProfileImage(
            intraID: String,
            imageURI: String,
            onCompleteListener: (Task<Uri>) -> Unit
        )

        suspend fun uploadProfile(
            userInfo: DetailedUserInfo,
            onSuccessListener: () -> Unit
        )
    }

    interface LocalDataSource {

        suspend fun fetchAccessToken(): Result<AccessToken>
        suspend fun saveAccessToken(accessToken: AccessToken? = null): Result<Unit>
    }
}