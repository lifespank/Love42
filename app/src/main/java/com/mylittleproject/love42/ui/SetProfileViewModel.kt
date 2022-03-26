package com.mylittleproject.love42.ui

import android.util.Log
import androidx.lifecycle.*
import com.mylittleproject.love42.data.UserInfo
import com.mylittleproject.love42.repository.AccessTokenRepository
import com.mylittleproject.love42.repository.IntraRepository
import com.mylittleproject.love42.tools.NAME_TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetProfileViewModel @Inject constructor(
    private val accessTokenRepository: AccessTokenRepository,
    private val intraRepository: IntraRepository
) :
    ViewModel() {

    private var accessToken: String? = null
    private val _userInfo: MutableLiveData<UserInfo> by lazy { MutableLiveData() }
    val userInfo: LiveData<UserInfo> get() = _userInfo

    fun fetchAccessToken(code: String?) {
        viewModelScope.launch {
            accessToken = accessTokenRepository.fetchAccessToken(code)
            Log.d(NAME_TAG, "Access token received: $accessToken")
            fetchUserInfo()
        }
    }

    private fun fetchUserInfo() {
        viewModelScope.launch {
            accessToken?.let {
                _userInfo.value = intraRepository.fetchUserInfo(it)
            }
        }
    }
}