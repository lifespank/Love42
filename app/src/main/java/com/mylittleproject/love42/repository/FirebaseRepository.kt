package com.mylittleproject.love42.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.mylittleproject.love42.data.DetailedUserInfo
import kotlinx.coroutines.flow.Flow

interface FirebaseRepository {

    suspend fun uploadProfile(userInfo: DetailedUserInfo): Boolean

    suspend fun uploadProfileImage(intraID: String, imageURI: String): String

    suspend fun downloadProfile(intraID: String): DocumentSnapshot?

    fun candidatesInFlow(
        isMale: Boolean,
        campus: String,
        likes: HashSet<String>,
        dislikes: HashSet<String>,
        matches: HashSet<String>
    ): Flow<List<DetailedUserInfo>>

    fun matchesInFlow(likes: HashSet<String>): Flow<List<DetailedUserInfo>>
}