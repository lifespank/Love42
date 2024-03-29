package com.mylittleproject.love42.model

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.mylittleproject.love42.data.AccessToken
import com.mylittleproject.love42.data.DetailedUserInfo
import com.mylittleproject.love42.network.IntraService
import com.mylittleproject.love42.tools.ImageCompressor
import com.mylittleproject.love42.tools.NAME_TAG
import com.mylittleproject.love42.tools.getDataFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import java.io.FileInputStream

class RemoteDataSource(
    private val intraService: IntraService,
    storage: FirebaseStorage,
    private val db: FirebaseFirestore,
    private val imageCompressor: ImageCompressor
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

    override suspend fun addElementToArray(
        docName: String,
        arrayName: String,
        element: String
    ): Boolean {
        return try {
            val docRef = db.collection("users").document(docName)
            docRef.update(arrayName, FieldValue.arrayUnion(element)).await()
            true
        } catch (e: Exception) {
            Log.w(NAME_TAG, "Add element failed", e)
            false
        }
    }

    override suspend fun deleteElementFromArray(
        docName: String,
        arrayName: String,
        element: String
    ): Boolean {
        return try {
            val docRef = db.collection("users").document(docName)
            docRef.update(arrayName, FieldValue.arrayRemove(element)).await()
            true
        } catch (e: Exception) {
            Log.w(NAME_TAG, "Delete element failed")
            false
        }
    }

    override suspend fun uploadProfileImage(intraID: String, imageURI: String): String {
        return try {
            val imageRef = storageRef.child("images/$intraID.jpg")
            Log.d(NAME_TAG, "imageURI: $imageURI")
            val compressedFile = imageCompressor.compressImage(Uri.parse(imageURI))
            val stream = runCatching {
                return@runCatching FileInputStream(compressedFile)
            }.getOrNull()
            if (stream != null) {
                Log.d(NAME_TAG, "Uploading file...")
                imageRef.putStream(stream)
                    .await()
                    .storage
                    .downloadUrl
                    .await()
                    .toString()
            } else {
                Log.d(NAME_TAG, "File stream failed...")
                ""
            }
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
                transaction.update(userRef, "imageURI", userInfo.imageURI)
                transaction.update(userRef, "isGlobal", userInfo.isGlobal)
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

    override fun matchesInFlow(matches: HashSet<String>): Flow<QuerySnapshot?> =
        if (matches.isNotEmpty()) {
            db.collection("users")
                .whereIn("intraID", matches.toList())
                .getDataFlow { querySnapshot ->
                    querySnapshot
                }
        } else {
            flowOf(null)
        }


    override fun candidatesUpdateFlow(
        isMale: Boolean,
        campus: String,
        isGlobal: Boolean
    ): Flow<QuerySnapshot?> =
        if (isGlobal) {
            db.collection("users")
                .whereNotEqualTo("isMale", isMale)
                .getDataFlow { querySnapshot ->
                    querySnapshot
                }
        } else {
            db.collection("users")
                .whereNotEqualTo("isMale", isMale)
                .whereEqualTo("campus", campus)
                .getDataFlow { querySnapshot ->
                    querySnapshot
                }
        }


    override fun myProfileUpdateFlow(intraID: String) =
        db.collection("users").document(intraID)
            .getDataFlow { documentSnapshot ->
                documentSnapshot
            }

    override suspend fun deleteAccount(intraID: String): Boolean {
        return try {
            db.collection("users").document(intraID).delete().await()
            storageRef.child("images/$intraID.jpg").delete().await()
            true
        } catch (e: Exception) {
            Log.w(NAME_TAG, "Delete account failed", e)
            false
        }
    }
}