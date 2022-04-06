package com.mylittleproject.love42.model

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.mylittleproject.love42.data.AccessToken
import com.mylittleproject.love42.data.DetailedUserInfo
import com.mylittleproject.love42.network.IntraService
import com.mylittleproject.love42.tools.NAME_TAG
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class RemoteDataSource(
    private val intraService: IntraService,
    storage: FirebaseStorage,
    private val db: FirebaseFirestore
) :
    DataSource.RemoteDataSource {

    private val storageRef = storage.reference

    override suspend fun fetchAccessToken(
        code: String?,
        refreshToken: String?,
        grantType: String
    ): Result<AccessToken> = runCatching {
        intraService.fetchAccessToken(code, refreshToken, grantType)
    }

    override suspend fun fetchUserInfo(accessToken: String) = runCatching {
        intraService.fetchUserInfo(accessToken)
    }

    override suspend fun uploadProfileImage(intraID: String, imageURI: String): String {
        return try {
            val imageRef = storageRef.child("images/$intraID.jpg")
            imageRef.putFile(Uri.parse(imageURI))
                .await()
                .storage
                .downloadUrl
                .await()
                .toString()
        } catch (e: Exception) {
            Log.w(NAME_TAG, "File upload failed", e)
            ""
        }
    }

    override suspend fun uploadProfile(userInfo: DetailedUserInfo): Boolean =
        try {
            db.collection("users")
                .document(userInfo.intraID)
                .set(userInfo.toHashMap())
                .await()
            true
        } catch (e: Exception) {
            Log.w(NAME_TAG, "Profile upload failed", e)
            false
        }

    override suspend fun downloadProfile(intraID: String): DocumentSnapshot? =
        try {
            val data = db.collection("users")
                .document(intraID)
                .get()
                .await()
            data
        } catch (e: Exception) {
            Log.w(NAME_TAG, "Profile download failed", e)
            null
        }

    override fun candidatesInFlow(isMale: Boolean, campus: String) = flow {
        while (true) {
            val userRef = db.collection("users")
            val data = userRef.whereNotEqualTo("isMale", isMale)
                .whereEqualTo("campus", campus)
                .get()
                .await()
            Log.d(NAME_TAG, "Datasource candidates: $data")
            emit(data)
            delay(REFRESH_INTERVAL_MS)
        }
    }

    companion object {
        const val REFRESH_INTERVAL_MS = 5_000L
    }
}