package com.mylittleproject.love42.model

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
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

        suspend fun uploadProfileImage(intraID: String, imageURI: String): String

        suspend fun uploadProfile(userInfo: DetailedUserInfo): Boolean

        suspend fun downloadProfile(intraID: String): DocumentSnapshot?

        suspend fun downloadCandidates(isMale: Boolean, campus: String): QuerySnapshot?
    }

    interface LocalDataSource {

        suspend fun fetchAccessToken(): Result<AccessToken>

        suspend fun saveAccessToken(accessToken: AccessToken? = null): Result<Unit>

        suspend fun saveIntraID(intraID: String): Result<Unit>

        suspend fun fetchIntraID(): Result<String?>
    }
}