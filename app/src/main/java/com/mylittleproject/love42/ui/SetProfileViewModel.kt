package com.mylittleproject.love42.ui

import android.util.Log
import androidx.lifecycle.*
import com.mylittleproject.love42.data.AccessToken
import com.mylittleproject.love42.data.UserInfo
import com.mylittleproject.love42.repository.AccessTokenRepository
import com.mylittleproject.love42.repository.IntraRepository
import com.mylittleproject.love42.tools.Event
import com.mylittleproject.love42.tools.NAME_TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class SetProfileViewModel @Inject constructor(
    private val accessTokenRepository: AccessTokenRepository,
    private val intraRepository: IntraRepository
) :
    ViewModel() {

    private var accessToken: AccessToken? = null

    private val _userInfo: MutableLiveData<UserInfo> by lazy { MutableLiveData() }
    val userInfo: LiveData<UserInfo> get() = _userInfo
    private val _redirectToSignInActivityEvent: MutableLiveData<Event<Unit>> by lazy { MutableLiveData() }
    val redirectToSignInActivityEvent: LiveData<Event<Unit>> get() = _redirectToSignInActivityEvent

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
                val data = intraRepository.fetchUserInfo(it.accessToken)
                data.onSuccess { userInfo ->
                    Log.d(NAME_TAG, "Auth with access token success: $userInfo")
                    _userInfo.value = userInfo
                }
                data.onFailure { throwable ->
                    Log.w(NAME_TAG, "Auth with access token failure", throwable)
                    if (throwable is HttpException) {
                        resetAccessToken()
                        _redirectToSignInActivityEvent.value = Event(Unit)
                    }
                }
            }
        }
    }

    private suspend fun resetAccessToken() {
        accessTokenRepository.saveAccessToken()
    }
}