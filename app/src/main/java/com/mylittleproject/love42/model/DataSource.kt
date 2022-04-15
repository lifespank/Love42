package com.mylittleproject.love42.model

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.mylittleproject.love42.data.AccessToken
import com.mylittleproject.love42.data.DetailedUserInfo
import com.mylittleproject.love42.data.UserInfo
import kotlinx.coroutines.flow.Flow

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

        suspend fun uploadLocalProfile(userInfo: DetailedUserInfo): Boolean

        suspend fun addElementToArray(docName: String, arrayName: String, element: String): Boolean

        suspend fun deleteElementFromArray(
            docName: String,
            arrayName: String,
            element: String
        ): Boolean

        suspend fun downloadProfile(intraID: String): DocumentSnapshot?

        fun candidatesUpdateFlow(isMale: Boolean, campus: String): Flow<QuerySnapshot?>

        fun matchesInFlow(matches: HashSet<String>): Flow<QuerySnapshot?>

        fun myProfileUpdateFlow(intraID: String): Flow<DocumentSnapshot?>
    }

    interface LocalDataSource {

        suspend fun fetchAccessToken(): Result<AccessToken>

        suspend fun saveAccessToken(accessToken: AccessToken? = null): Result<Unit>

        suspend fun saveIntraID(intraID: String): Result<Unit>

        suspend fun fetchIntraID(): Result<String?>
    }
}