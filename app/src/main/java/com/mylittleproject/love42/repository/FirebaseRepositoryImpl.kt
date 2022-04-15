package com.mylittleproject.love42.repository

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.toObject
import com.mylittleproject.love42.data.DetailedUserInfo
import com.mylittleproject.love42.model.DataSource
import com.mylittleproject.love42.tools.NAME_TAG
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext

class FirebaseRepositoryImpl(
    private val remoteDataSource: DataSource.RemoteDataSource,
    private val defaultDispatcher: CoroutineDispatcher
) :
    FirebaseRepository {

    override suspend fun uploadProfile(userInfo: DetailedUserInfo): Boolean =
        withContext(defaultDispatcher) {
            remoteDataSource.uploadProfile(userInfo)
        }

    override suspend fun uploadLocalProfile(userInfo: DetailedUserInfo): Boolean =
        withContext(defaultDispatcher) {
            remoteDataSource.uploadLocalProfile(userInfo)
        }

    override suspend fun addElementToArray(
        docName: String,
        arrayName: String,
        element: String
    ): Boolean =
        withContext(defaultDispatcher) {
            remoteDataSource.addElementToArray(docName, arrayName, element)
        }

    override suspend fun deleteElementFromArray(
        docName: String,
        arrayName: String,
        element: String
    ): Boolean =
        withContext(defaultDispatcher) {
            remoteDataSource.deleteElementFromArray(docName, arrayName, element)
        }

    override suspend fun uploadProfileImage(intraID: String, imageURI: String): String =
        withContext(defaultDispatcher) {
            remoteDataSource.uploadProfileImage(intraID, imageURI)
        }

    override suspend fun downloadProfile(intraID: String): DocumentSnapshot? =
        withContext(defaultDispatcher) {
            remoteDataSource.downloadProfile(intraID)
        }

    override fun candidatesInFlow(
        isMale: Boolean,
        campus: String,
        likes: HashSet<String>,
        dislikes: HashSet<String>,
        matches: HashSet<String>
    ): Flow<List<DetailedUserInfo>> {
        val candidates = remoteDataSource.candidatesUpdateFlow(isMale, campus)
            .mapNotNull { querySnapShot ->
                querySnapShot?.documents?.mapNotNull { documentSnapshot ->
                    documentSnapshot.toObject<DetailedUserInfo.FirebaseUserInfo>()?.let { fbUser ->
                        DetailedUserInfo.fromFirebase(fbUser)
                    }
                }?.filter { userInfo ->
                    !likes.contains(userInfo.intraID) && !dislikes.contains(userInfo.intraID)
                            && !matches.contains(userInfo.intraID)
                }
            }.flowOn(defaultDispatcher)
        Log.d(NAME_TAG, "Repository Candidates: $candidates")
        return candidates
    }

    override fun matchesInFlow(matches: HashSet<String>): Flow<List<DetailedUserInfo>?> =
        remoteDataSource.matchesInFlow(matches)
            .map { querySnapshot ->
                querySnapshot?.documents?.mapNotNull { documentSnapshot ->
                    documentSnapshot.toObject<DetailedUserInfo.FirebaseUserInfo>()?.let { fbUser ->
                        DetailedUserInfo.fromFirebase(fbUser)
                    }
                }
            }.flowOn(defaultDispatcher)

    override fun myProfileInFlow(intraID: String): Flow<DetailedUserInfo> =
        remoteDataSource.myProfileUpdateFlow(intraID)
            .mapNotNull { documentSnapshot ->
                documentSnapshot?.toObject<DetailedUserInfo.FirebaseUserInfo>()?.let { fbUser ->
                    DetailedUserInfo.fromFirebase(fbUser)
                }
            }.flowOn(defaultDispatcher)
}
