package com.mylittleproject.love42.ui

import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.RadioButton
import androidx.lifecycle.*
import com.mylittleproject.love42.R
import com.mylittleproject.love42.data.AccessToken
import com.mylittleproject.love42.data.DetailedUserInfo
import com.mylittleproject.love42.repository.PrivateInfoRepository
import com.mylittleproject.love42.repository.FirebaseRepository
import com.mylittleproject.love42.repository.IntraRepository
import com.mylittleproject.love42.tools.Event
import com.mylittleproject.love42.tools.NAME_TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class SetProfileViewModel @Inject constructor(
    private val privateInfoRepository: PrivateInfoRepository,
    private val intraRepository: IntraRepository,
    private val firebaseRepository: FirebaseRepository
) :
    ViewModel() {

    private var accessToken: AccessToken? = null

    private val _userInfo: MutableLiveData<DetailedUserInfo> by lazy { MutableLiveData() }
    val userInfo: LiveData<DetailedUserInfo> get() = _userInfo
    val preferredLanguages = userInfo.switchMap {
        liveData {
            emit(it.languages.toList())
        }
    }
    private val _redirectToSignInActivityEvent: MutableLiveData<Event<Unit>> by lazy { MutableLiveData() }
    val redirectToSignInActivityEvent: LiveData<Event<Unit>> get() = _redirectToSignInActivityEvent
    private val _loadProfileImageEvent: MutableLiveData<Event<Unit>> by lazy { MutableLiveData() }
    val loadProfileImageEvent: LiveData<Event<Unit>> get() = _loadProfileImageEvent
    private val _popUpSlackIDDescriptionEvent: MutableLiveData<Event<Unit>> by lazy { MutableLiveData() }
    val popUpSlackIDDescriptionEvent: LiveData<Event<Unit>> get() = _popUpSlackIDDescriptionEvent
    private val _manualLanguagePopUpEvent: MutableLiveData<Event<Unit>> by lazy { MutableLiveData() }
    val manualLanguagePopUpEvent: LiveData<Event<Unit>> get() = _manualLanguagePopUpEvent
    private val _fillOutSlackMemberIDEvent: MutableLiveData<Event<Unit>> by lazy { MutableLiveData() }
    val fillOutSlackMemberIDEvent: LiveData<Event<Unit>> get() = _fillOutSlackMemberIDEvent
    private val _showLoading: MutableLiveData<Boolean> by lazy { MutableLiveData() }
    val showLoading: LiveData<Boolean> get() = _showLoading
    private val _moveToMainEvent: MutableLiveData<Event<Unit>> by lazy { MutableLiveData() }
    val moveToMainEvent: LiveData<Event<Unit>> get() = _moveToMainEvent

    fun fetchAccessToken(code: String?) {
        viewModelScope.launch {
            accessToken =
                privateInfoRepository.fetchAccessToken(code = code)
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

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            when (view.id) {
                R.id.r_btn_male -> _userInfo.value?.isMale = true
                R.id.r_btn_female -> _userInfo.value?.isMale = false
            }
        }
    }

    fun addLanguage(language: String) {
        val languages = userInfo.value?.languages
        languages?.let {
            it.add(language)
            _userInfo.value = userInfo.value?.copy(languages = it)
        }
    }

    fun removeLanguage(language: String) {
        val languages = userInfo.value?.languages
        languages?.let {
            it.remove(language)
            _userInfo.value = userInfo.value?.copy(languages = it)
        }
    }

    fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        Log.d(NAME_TAG, "onTextChange: $s")
        if (s.toString() != "Something else") {
            addLanguage(s.toString())
        } else {
            _manualLanguagePopUpEvent.value = Event(Unit)
        }
    }

    private fun uploadProfileImage() {
        viewModelScope.launch {
            val user = userInfo.value
            user?.let {
                if (it.slackMemberID.isNotBlank()) {
                    _showLoading.value = true
                    val downloadURL =
                        firebaseRepository.uploadProfileImage(it.intraID, it.imageURI)
                    if (downloadURL.isNotBlank()) {
                        _userInfo.value = it.copy(imageURI = downloadURL)
                        Log.d(NAME_TAG, "Image uploaded: ${userInfo.value}")
                        uploadProfile()
                    } else if (it.imageURI.contains("intra.42")) {
                        uploadProfile()
                    }
                } else {
                    _fillOutSlackMemberIDEvent.value = Event(Unit)
                }
            }
        }
    }

    private fun uploadProfile() {
        viewModelScope.launch {
            userInfo.value?.let {
                if (firebaseRepository.uploadProfile(it)) {
                    Log.d(NAME_TAG, "Profile upload success")
                    _moveToMainEvent.value = Event(Unit)
                }
            }
            _showLoading.value = false
        }
    }

    fun onMenuClick(menuItem: MenuItem) =
        when (menuItem.itemId) {
            R.id.itm_save -> {
                uploadProfileImage()
                true
            }
            else -> false
        }

    private fun fetchUserInfo() {
        viewModelScope.launch {
            accessToken?.let {
                _showLoading.value = true
                val data = intraRepository.fetchUserInfo(it.accessToken)
                data.onSuccess { userInfo ->
                    Log.d(NAME_TAG, "Auth with access token success: $userInfo")
                    _userInfo.value = userInfo.toDetailedUserInfo()
                    saveIntraID(userInfo.login)
                    checkIntraID()
                }
                data.onFailure { throwable ->
                    Log.w(NAME_TAG, "Auth with access token failure", throwable)
                    if (throwable is HttpException) {
                        resetAccessToken()
                        accessToken = privateInfoRepository.fetchAccessToken(
                            refreshToken = it.refreshToken,
                            grantType = "refresh_token"
                        )
                        accessToken?.let { accessToken ->
                            val dataAgain = intraRepository.fetchUserInfo(accessToken.accessToken)
                            dataAgain.onSuccess { userInfo ->
                                Log.d(NAME_TAG, "Auth with second access token success: $userInfo")
                                _userInfo.value = userInfo.toDetailedUserInfo()
                                saveIntraID(userInfo.login)
                                checkIntraID()
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
                _showLoading.value = false
            }
        }
    }

    private fun checkIntraID() {
        viewModelScope.launch {
            val intraID = userInfo.value?.intraID
            intraID?.let {
                if (firebaseRepository.downloadProfile(intraID)?.data != null) {
                    //Move
                    _moveToMainEvent.value = Event(Unit)
                    Log.d(NAME_TAG, "Moving to Main")
                } else {
                    Log.w(NAME_TAG, "No such intra ID")
                }
            }
        }
    }

    private fun saveIntraID(intraID: String) {
        viewModelScope.launch {
            val result = privateInfoRepository.saveIntraID(intraID)
            result.onSuccess {
                Log.d(NAME_TAG, "Intra ID saved: $intraID")
            }
            result.onFailure {
                Log.w(NAME_TAG, "Intra ID save failed", it)
            }
        }
    }

    private suspend fun resetAccessToken() {
        privateInfoRepository.saveAccessToken()
    }
}