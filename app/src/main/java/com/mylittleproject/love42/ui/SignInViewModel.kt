package com.mylittleproject.love42.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mylittleproject.love42.tools.Event

class SignInViewModel : ViewModel() {

    private val _signInClickEvent: MutableLiveData<Event<Unit>> by lazy { MutableLiveData() }
    val signInClickEvent: LiveData<Event<Unit>> get() = _signInClickEvent

    fun onSignInClick() {
        _signInClickEvent.value = Event(Unit)
    }
}