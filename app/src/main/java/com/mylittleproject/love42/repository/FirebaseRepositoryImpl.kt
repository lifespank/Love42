package com.mylittleproject.love42.repository

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.mylittleproject.love42.model.DataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FirebaseRepositoryImpl(private val remoteDataSource: DataSource.RemoteDataSource) :
    FirebaseRepository {

    override suspend fun uploadProfileImage(
        intraID: String,
        imageURI: String,
        onCompleteListener: (Task<Uri>) -> Unit
    ) =
        withContext(Dispatchers.IO) {
            remoteDataSource.uploadProfileImage(intraID, imageURI, onCompleteListener)
        }

}