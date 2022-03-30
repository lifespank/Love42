package com.mylittleproject.love42.ui

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.mylittleproject.love42.repository.PrivateInfoRepository
import com.mylittleproject.love42.tools.Event
import com.mylittleproject.love42.tools.NAME_TAG
import com.mylittleproject.love42.tools.preventDoubleClick
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(private val privateInfoRepository: PrivateInfoRepository) :
    ViewModel() {

    private val _signInClickEvent: MutableLiveData<Event<Unit>> by lazy { MutableLiveData() }
    val signInClickEvent: LiveData<Event<Unit>> get() = _signInClickEvent
    private val _buttonEnabled: MutableLiveData<Boolean> by lazy { MutableLiveData() }
    val buttonEnabled: LiveData<Boolean> get() = _buttonEnabled
    val accessToken = liveData {
        _buttonEnabled.value = false
        val token = privateInfoRepository.fetchAccessToken()
        Log.d(NAME_TAG, "Token already exists: $token")
        emit(Event(token))
        _buttonEnabled.value = true
    }

    fun onSignInClick(button: View) {
        button.preventDoubleClick { _signInClickEvent.value = Event(Unit) }
    }
}