package com.mylittleproject.love42.repository

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.mylittleproject.love42.data.DetailedUserInfo

interface FirebaseRepository {

    suspend fun uploadProfile(userInfo: DetailedUserInfo): Boolean

    suspend fun uploadProfileImage(intraID: String, imageURI: String): String

    suspend fun downloadProfile(intraID: String): DocumentSnapshot?

    suspend fun downloadCandidates(
        intraID: String,
        isMale: Boolean,
        campus: String,
        onSuccessListener: (QuerySnapshot?) -> Unit,
        onFailureListener: (Exception) -> Unit
    )
}