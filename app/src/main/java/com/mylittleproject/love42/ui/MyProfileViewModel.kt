package com.mylittleproject.love42.ui

import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.firestore.ktx.toObject
import com.mylittleproject.love42.data.DetailedUserInfo
import com.mylittleproject.love42.repository.FirebaseRepository
import com.mylittleproject.love42.repository.PrivateInfoRepository
import com.mylittleproject.love42.tools.NAME_TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyProfileViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val privateInfoRepository: PrivateInfoRepository
) :
    ViewModel() {

    private val _myProfile: MutableLiveData<DetailedUserInfo> by lazy { MutableLiveData() }
    val myProfile: LiveData<DetailedUserInfo> get() = _myProfile

    fun downloadProfile() {
        viewModelScope.launch {
            val intraID = privateInfoRepository.fetchIntraID()
            intraID.getOrNull()?.let {
                firebaseRepository.downloadProfile(it,
                    { documentSnapshot ->
                        documentSnapshot?.toObject<DetailedUserInfo.FirebaseUerInfo>()
                            ?.let { userInfo ->
                                _myProfile.value = DetailedUserInfo.fromFirebase(userInfo)
                                Log.d(NAME_TAG, "Profile fetched: $userInfo")
                            }
                    },
                    { exception ->
                        Log.w(NAME_TAG, "Profile fetch failed", exception)
                    })
            }
        }
    }

}