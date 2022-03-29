package com.mylittleproject.love42.repository

import android.net.Uri
import com.google.android.gms.tasks.Task

interface FirebaseRepository {

    suspend fun uploadProfileImage(
        intraID: String,
        imageURI: String,
        onCompleteListener: (Task<Uri>) -> Unit
    )
}