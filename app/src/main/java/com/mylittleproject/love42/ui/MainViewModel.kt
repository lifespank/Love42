package com.mylittleproject.love42.ui

import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.firestore.ktx.toObject
import com.mylittleproject.love42.R
import com.mylittleproject.love42.data.DetailedUserInfo
import com.mylittleproject.love42.repository.FirebaseRepository
import com.mylittleproject.love42.repository.PrivateInfoRepository
import com.mylittleproject.love42.tools.Event
import com.mylittleproject.love42.tools.NAME_TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val privateInfoRepository: PrivateInfoRepository
) :
    ViewModel() {

    private val _myProfile: MutableLiveData<DetailedUserInfo> by lazy { MutableLiveData() }
    val myProfile: LiveData<DetailedUserInfo> get() = _myProfile
    private val _loadProfileImageEvent: MutableLiveData<Event<Unit>> by lazy { MutableLiveData() }
    val loadProfileImageEvent: LiveData<Event<Unit>> get() = _loadProfileImageEvent
    private val _manualLanguagePopUpEvent: MutableLiveData<Event<Unit>> by lazy { MutableLiveData() }
    val manualLanguagePopUpEvent: LiveData<Event<Unit>> get() = _manualLanguagePopUpEvent
    private val _popUpSlackIDDescriptionEvent: MutableLiveData<Event<Unit>> by lazy { MutableLiveData() }
    val popUpSlackIDDescriptionEvent: LiveData<Event<Unit>> get() = _popUpSlackIDDescriptionEvent
    private val _snackBarEvent: MutableLiveData<Event<Int>> by lazy { MutableLiveData() }
    val snackBarEvent: LiveData<Event<Int>> get() = _snackBarEvent
    private val _showLoading: MutableLiveData<Boolean> by lazy { MutableLiveData() }
    val showLoading: LiveData<Boolean> get() = _showLoading
    private val _candidateProfiles: MutableLiveData<List<DetailedUserInfo>> by lazy { MutableLiveData() }
    val candidateProfiles: LiveData<List<DetailedUserInfo>> get() = _candidateProfiles
    private val _matchProfiles: MutableLiveData<List<DetailedUserInfo>> by lazy { MutableLiveData() }
    val matchProfiles: LiveData<List<DetailedUserInfo>> get() = _matchProfiles
    val preferredLanguages = myProfile.switchMap {
        liveData {
            if (it != null) {
                emit(it.languages.toList())
            }
        }
    }

    fun popLike() {
        viewModelScope.launch {
            val candidates = candidateProfiles.value?.toMutableList()
            val me = myProfile.value
            if (!candidates.isNullOrEmpty() && me != null) {
                val like = candidates.removeFirst()
                if (like.likes.contains(me.intraID)) {
                    me.matches.add(like.intraID)
                    like.likes.remove(me.intraID)
                    like.matches.add(me.intraID)
                } else {
                    me.likes.add(like.intraID)
                }
                if (firebaseRepository.uploadProfile(like)) {
                    Log.d(NAME_TAG, "like uploaded: $like")
                }
                if (firebaseRepository.uploadProfile(me)) {
                    Log.d(NAME_TAG, "me uploaded: $me")
                }
                _candidateProfiles.value = candidates
            }
        }
    }

    fun popDislike() {
        viewModelScope.launch {
            val candidates = candidateProfiles.value?.toMutableList()
            val me = myProfile.value
            if (!candidates.isNullOrEmpty() && me != null) {
                val dislike = candidates.removeFirst()
                me.dislikes.add(dislike.intraID)
                if (firebaseRepository.uploadProfile(dislike)) {
                    Log.d(NAME_TAG, "like uploaded: $dislike")
                }
                if (firebaseRepository.uploadProfile(me)) {
                    Log.d(NAME_TAG, "me uploaded: $me")
                }
                _candidateProfiles.value = candidates
            }
        }
    }

    fun addLanguage(language: String) {
        val languages = myProfile.value?.languages
        languages?.let {
            it.add(language)
            _myProfile.value = myProfile.value?.copy(languages = it)
        }
    }

    fun onWhatIsSlackIDClick() {
        _popUpSlackIDDescriptionEvent.value = Event(Unit)
    }

    fun onProfileImageEditClick() {
        _loadProfileImageEvent.value = Event(Unit)
    }

    fun setImageURI(imageURI: String) {
        _myProfile.value = myProfile.value?.copy(imageURI = imageURI)
        Log.d(NAME_TAG, "User image changed: ${myProfile.value}")
    }

    fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        Log.d(NAME_TAG, "onTextChange: $s")
        if (s.toString() != "Something else") {
            addLanguage(s.toString())
        } else {
            _manualLanguagePopUpEvent.value = Event(Unit)
        }
    }

    fun removeLanguage(language: String) {
        val languages = myProfile.value?.languages
        languages?.let {
            it.remove(language)
            _myProfile.value = myProfile.value?.copy(languages = it)
        }
    }

    fun uploadProfileImage() {
        viewModelScope.launch {
            val user = myProfile.value
            user?.let {
                if (it.slackMemberID.isNotBlank()) {
                    _showLoading.value = true
                    val downloadURL =
                        firebaseRepository.uploadProfileImage(it.intraID, it.imageURI)
                    if (downloadURL.isNotBlank()) {
                        _myProfile.value = it.copy(imageURI = downloadURL)
                        Log.d(NAME_TAG, "Image uploaded: ${myProfile.value}")
                        uploadProfile()
                    } else if (it.imageURI.startsWith("https://firebasestorage")
                        || it.imageURI.contains("intra.42")
                    ) {
                        uploadProfile()
                    }
                } else {
                    _snackBarEvent.value = Event(R.string.fill_out_slack)
                }
            }
        }
    }

    private fun uploadProfile() {
        viewModelScope.launch {
            myProfile.value?.let {
                if (firebaseRepository.uploadProfile(it)) {
                    Log.d(NAME_TAG, "Profile upload success")
                    _snackBarEvent.value = Event(R.string.profile_uploaded)
                }
            }
            _showLoading.value = false
        }
    }

    fun downloadProfile() {
        if (myProfile.value == null) {
            viewModelScope.launch {
                _showLoading.value = true
                val intraID = privateInfoRepository.fetchIntraID()
                intraID.getOrNull()?.let {
                    val documentSnapshot = firebaseRepository.downloadProfile(it)
                    val profile = DetailedUserInfo.fromFirebase(documentSnapshot?.toObject()!!)
                    _myProfile.value = profile
                    Log.d(NAME_TAG, "Profile fetched: $profile")
                    _showLoading.value = false
                }
            }
        }
    }

    fun downloadCandidates() {
        if (candidateProfiles.value == null) {
            viewModelScope.launch {
                val profile = myProfile.value
                profile?.let { myProf ->
                    val querySnapshot =
                        firebaseRepository.downloadCandidates(myProf.isMale, myProf.campus)
                    val candidates = querySnapshot?.documents?.map { documentSnapshot ->
                        DetailedUserInfo.fromFirebase(documentSnapshot.toObject()!!)
                    }?.filter {
                        !profile.likes.contains(it.intraID)
                                && !profile.dislikes.contains(it.intraID)
                                && !profile.matches.contains(it.intraID)
                    }?.shuffled()
                    Log.d(NAME_TAG, "Candidates: $candidates")
                    _candidateProfiles.value = candidates
                }
            }
        }
    }

    fun downloadMatches() {
        viewModelScope.launch {
            val matches = myProfile.value?.matches

        }
    }
}