package com.mylittleproject.love42.ui

import android.util.Log
import androidx.lifecycle.*
import com.mylittleproject.love42.data.AccessToken
import com.mylittleproject.love42.data.DetailedUserInfo
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

    private val _userInfo: MutableLiveData<DetailedUserInfo> by lazy { MutableLiveData() }
    val userInfo: LiveData<DetailedUserInfo> get() = _userInfo
    private val _redirectToSignInActivityEvent: MutableLiveData<Event<Unit>> by lazy { MutableLiveData() }
    val redirectToSignInActivityEvent: LiveData<Event<Unit>> get() = _redirectToSignInActivityEvent
    private val _loadProfileImageEvent: MutableLiveData<Event<Unit>> by lazy { MutableLiveData() }
    val loadProfileImageEvent: LiveData<Event<Unit>> get() = _loadProfileImageEvent
    private val _popUpSlackIDDescriptionEvent: MutableLiveData<Event<Unit>> by lazy { MutableLiveData() }
    val popUpSlackIDDescriptionEvent: LiveData<Event<Unit>> get() = _popUpSlackIDDescriptionEvent

    fun fetchAccessToken(code: String?) {
        viewModelScope.launch {
            accessToken =
                accessTokenRepository.fetchAccessToken(code = code)
            Log.d(NAME_TAG, "Access token received: $accessToken")
            fetchUserInfo()
        }
    }

    fun onWhatIsSlackIDClick() {
        _popUpSlackIDDescriptionEvent.value = Event(Unit)
    }

    fun onProfileImageEditClick() {
        _loadProfileImageEvent.value = Event(Unit)
    }

    fun setImageURI(imageURI: String) {
        _userInfo.value = userInfo.value?.copy(imageURI = imageURI)
        Log.d(NAME_TAG, "User image changed: ${userInfo.value}")
    }

    fun fetchUserInfo() {
        viewModelScope.launch {
            _userInfo.value = null
            accessToken?.let {
                val data = intraRepository.fetchUserInfo(it.accessToken)
                data.onSuccess { userInfo ->
                    Log.d(NAME_TAG, "Auth with access token success: $userInfo")
                    _userInfo.value = userInfo.toDetailedUserInfo()
                }
                data.onFailure { throwable ->
                    Log.w(NAME_TAG, "Auth with access token failure", throwable)
                    if (throwable is HttpException) {
                        resetAccessToken()
                        accessToken = accessTokenRepository.fetchAccessToken(
                            refreshToken = it.refreshToken,
                            grantType = "refresh_token"
                        )
                        accessToken?.let { accessToken ->
                            val dataAgain = intraRepository.fetchUserInfo(accessToken.accessToken)
                            dataAgain.onSuccess { userInfo ->
                                Log.d(NAME_TAG, "Auth with second access token success: $userInfo")
                                _userInfo.value = userInfo.toDetailedUserInfo()
                            }
                            dataAgain.onFailure { throwable ->
                                Log.w(NAME_TAG, "Auth with second access token failure", throwable)
                                _userInfo.value = null
                            }
                        }
                        if (accessToken == null || userInfo.value == null) {
                            _redirectToSignInActivityEvent.value = Event(Unit)
                        }
                    }
                }
            }
        }
    }

    private suspend fun resetAccessToken() {
        accessTokenRepository.saveAccessToken()
    }
}