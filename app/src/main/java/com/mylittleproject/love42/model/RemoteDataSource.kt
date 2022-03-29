package com.mylittleproject.love42.model

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.mylittleproject.love42.data.AccessToken
import com.mylittleproject.love42.data.DetailedUserInfo
import com.mylittleproject.love42.network.IntraService
import com.mylittleproject.love42.tools.NAME_TAG
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException

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

    override suspend fun uploadProfileImage(
        intraID: String,
        imageURI: String,
        onCompleteListener: (Task<Uri>) -> Unit
    ) {
        val imageRef = storageRef.child("images/$intraID.jpg")
        try {
            val uploadTask = imageRef.putFile(Uri.parse(imageURI))
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                imageRef.downloadUrl
            }.addOnCompleteListener(onCompleteListener)
        } catch (e: FileNotFoundException) {
            Log.w(NAME_TAG, "File not found: $imageURI", e)
        }
    }

    override suspend fun uploadProfile(
        userInfo: DetailedUserInfo,
        onSuccessListener: () -> Unit
    ) {
        db.collection("users")
            .document(userInfo.intraID)
            .set(userInfo.toHashMap())
            .addOnSuccessListener { onSuccessListener.invoke() }
            .addOnFailureListener {
                Log.w(NAME_TAG, "Profile upload failed", it)
            }
    }
}