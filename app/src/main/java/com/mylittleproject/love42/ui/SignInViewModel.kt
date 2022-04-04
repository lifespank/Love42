package com.mylittleproject.love42.ui

import android.util.Log
import android.view.View
import androidx.lifecycle.*
import com.mylittleproject.love42.data.AccessToken
import com.mylittleproject.love42.repository.FirebaseRepository
import com.mylittleproject.love42.repository.PrivateInfoRepository
import com.mylittleproject.love42.tools.Event
import com.mylittleproject.love42.tools.NAME_TAG
import com.mylittleproject.love42.tools.preventDoubleClick
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val privateInfoRepository: PrivateInfoRepository,
    private val firebaseRepository: FirebaseRepository
) :
    ViewModel() {

    private val _signInClickEvent: MutableLiveData<Event<Unit>> by lazy { MutableLiveData() }
    val signInClickEvent: LiveData<Event<Unit>> get() = _signInClickEvent
    private val _buttonEnabled: MutableLiveData<Boolean> by lazy { MutableLiveData() }
    val buttonEnabled: LiveData<Boolean> get() = _buttonEnabled
    private val _accessToken: MutableLiveData<Event<AccessToken>> by lazy { MutableLiveData() }
    val accessToken: LiveData<Event<AccessToken>> get() = _accessToken
    private val _moveToMainEvent: MutableLiveData<Event<Unit>> by lazy { MutableLiveData() }
    val moveToMainEvent: LiveData<Event<Unit>> get() = _moveToMainEvent

    fun initialCheck() {
        viewModelScope.launch {
            _buttonEnabled.value = false
            val intraID = privateInfoRepository.fetchIntraID()
            intraID.onSuccess {
                if (it != null) {
                    firebaseRepository.downloadProfile(it,
                        { documentSnapshot ->
                            if (documentSnapshot != null) {
                                //Move
                                _moveToMainEvent.value = Event(Unit)
                                Log.d(NAME_TAG, "DocumentSnapshot: ${documentSnapshot.data}")
                            } else {
                                Log.w(NAME_TAG, "No such intra ID")
                            }
                        },
                        { exception ->
                            Log.w(NAME_TAG, "get profile failed with", exception)
                        })
                }
            }
            if (intraID.getOrNull() == null) {
                val token = privateInfoRepository.fetchAccessToken()
                Log.d(NAME_TAG, "Token already exists: $token")
                token?.let {
                    _accessToken.value = Event(it)
                }
            }
            _buttonEnabled.value = true
        }
    }

    fun onSignInClick(button: View) {
        button.preventDoubleClick { _signInClickEvent.value = Event(Unit) }
    }
}