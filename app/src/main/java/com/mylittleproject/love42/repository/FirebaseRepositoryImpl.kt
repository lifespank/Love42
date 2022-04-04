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

    override suspend fun downloadProfile(intraID: String): DocumentSnapshot? =
        withContext(Dispatchers.IO) {
            remoteDataSource.downloadProfile(intraID)
        }

    override suspend fun downloadCandidates(isMale: Boolean, campus: String): QuerySnapshot? =
        withContext(Dispatchers.IO) {
            remoteDataSource.downloadCandidates(isMale, campus)
        }
}