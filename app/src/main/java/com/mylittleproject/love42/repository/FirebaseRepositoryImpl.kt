package com.mylittleproject.love42.repository

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.toObject
import com.mylittleproject.love42.data.DetailedUserInfo
import com.mylittleproject.love42.model.DataSource
import com.mylittleproject.love42.tools.NAME_TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
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

    override fun candidatesInFlow(
        isMale: Boolean,
        campus: String,
        likes: HashSet<String>,
        dislikes: HashSet<String>,
        matches: HashSet<String>
    ): Flow<List<DetailedUserInfo>> {
        val candidates = remoteDataSource.candidatesInFlow(isMale, campus)
            .mapNotNull { querySnapShot ->
                querySnapShot?.documents?.mapNotNull { documentSnapshot ->
                    documentSnapshot.toObject<DetailedUserInfo.FirebaseUserInfo>()?.let { fbUser ->
                        DetailedUserInfo.fromFirebase(fbUser)
                    }
                }?.filter { userInfo ->
                    !likes.contains(userInfo.intraID) && !dislikes.contains(userInfo.intraID)
                            && !matches.contains(userInfo.intraID)
                }
            }.flowOn(Dispatchers.IO)
        Log.d(NAME_TAG, "Repository Candidates: $candidates")
        return candidates
    }

    override fun matchesInFlow(likes: HashSet<String>) = remoteDataSource.matchesInFlow(likes)
        .map { documentSnapshots ->
            documentSnapshots.mapNotNull { documentSnapshot ->
                documentSnapshot?.toObject<DetailedUserInfo.FirebaseUserInfo>()?.let { fbUser ->
                    DetailedUserInfo.fromFirebase(fbUser)
                }
            }
        }.flowOn(Dispatchers.IO)
}
