package com.mylittleproject.love42.repository

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.mylittleproject.love42.data.DetailedUserInfo

interface FirebaseRepository {

    suspend fun uploadProfileImage(
        intraID: String,
        imageURI: String,
        onCompleteListener: (Task<Uri>) -> Unit
    )

    suspend fun uploadProfile(userInfo: DetailedUserInfo, onSuccessListener: () -> Unit)
}