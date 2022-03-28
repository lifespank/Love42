package com.mylittleproject.love42.repository

import android.util.Log
import com.mylittleproject.love42.data.UserInfo
import com.mylittleproject.love42.model.DataSource
import com.mylittleproject.love42.tools.NAME_TAG
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

class IntraRepositoryImpl(private val remoteDataSource: DataSource.RemoteDataSource) :
    IntraRepository {

    override suspend fun fetchUserInfo(accessToken: String): Result<UserInfo> =
        remoteDataSource.fetchUserInfo(accessToken)
}