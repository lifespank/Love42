package com.mylittleproject.love42.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.mylittleproject.love42.data.DetailedUserInfo
import com.mylittleproject.love42.model.DataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FirebaseRepositoryImpl(private val remoteDataSource: DataSource.RemoteDataSource) :
    FirebaseRepository {

    override suspend fun uploadProfile(userInfo: DetailedUserInfo): Boolean =
        withContext(Dispatchers.IO) {
            remoteDataSource.uploadProfile(userInfo)
        }

    override suspend fun uploadProfileImage(intraID: String, imageURI: String): String =
        withContext(Dispatchers.IO) {
            remoteDataSource.uploadProfileImage(intraID, imageURI)
        }


    override suspend fun downloadProfile(
        intraID: String,
        onSuccessListener: (DocumentSnapshot?) -> Unit,
        onFailureListener: (Exception) -> Unit
    ) =
        withContext(Dispatchers.IO) {
            remoteDataSource.downloadProfile(intraID, onSuccessListener, onFailureListener)
        }

    override suspend fun downloadCandidates(
        intraID: String,
        isMale: Boolean,
        campus: String,
        onSuccessListener: (QuerySnapshot?) -> Unit,
        onFailureListener: (Exception) -> Unit
    ) =
        withContext(Dispatchers.IO) {
            remoteDataSource.downloadCandidates(
                intraID,
                isMale,
                campus,
                onSuccessListener,
                onFailureListener
            )
        }
}