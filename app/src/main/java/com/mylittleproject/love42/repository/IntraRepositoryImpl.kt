package com.mylittleproject.love42.repository

import com.mylittleproject.love42.data.UserInfo
import com.mylittleproject.love42.model.DataSource

class IntraRepositoryImpl(private val remoteDataSource: DataSource.RemoteDataSource) :
    IntraRepository {

    override suspend fun fetchUserInfo(accessToken: String): Result<UserInfo> =
        remoteDataSource.fetchUserInfo(accessToken)
}