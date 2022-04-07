package com.mylittleproject.love42.model

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.mylittleproject.love42.data.AccessToken
import com.mylittleproject.love42.data.DetailedUserInfo
import com.mylittleproject.love42.network.IntraService
import com.mylittleproject.love42.tools.NAME_TAG
import com.mylittleproject.love42.tools.getDataFlow
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
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

    override suspend fun uploadLocalProfile(userInfo: DetailedUserInfo): Boolean =
        try {
            val userRef = db.collection("users")
                .document(userInfo.intraID)
            db.runTransaction { transaction ->
                transaction.update(userRef, "gitHubID", userInfo.gitHubID)
                transaction.update(userRef, "slackMemberID", userInfo.slackMemberID)
                transaction.update(userRef, "languages", userInfo.languages.toList())
                transaction.update(userRef, "bio", userInfo.bio)
            }.await()
            true
        } catch (e: Exception) {
            Log.w(NAME_TAG, "Local profile upload failed", e)
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

    override fun matchesInFlow(likes: HashSet<String>): Flow<QuerySnapshot?> =
        db.collection("users")
            .whereIn("intraID", likes.toList())
            .getDataFlow { querySnapshot ->
                querySnapshot
            }


    override fun candidatesUpdateFlow(isMale: Boolean, campus: String): Flow<QuerySnapshot?> =
        db.collection("users")
            .whereNotEqualTo("isMale", isMale)
            .whereEqualTo("campus", campus)
            .getDataFlow { querySnapshot ->
                querySnapshot
            }


    override fun myProfileUpdateFlow(intraID: String) =
        db.collection("users").document(intraID)
            .getDataFlow { documentSnapshot ->
                documentSnapshot
            }

    companion object {
        const val REFRESH_INTERVAL_MS = 5_000L
    }
}