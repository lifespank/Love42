package com.mylittleproject.love42.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mylittleproject.love42.repository.AccessTokenRepository
import com.mylittleproject.love42.tools.NAME_TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetProfileViewModel @Inject constructor(private val accessTokenRepository: AccessTokenRepository) :
    ViewModel() {

    fun fetchAccessToken(code: String) {
        viewModelScope.launch {
            val accessToken = accessTokenRepository.fetchAccessToken(code)
            Log.d(NAME_TAG, "Access token received: $accessToken")
        }
    }
}