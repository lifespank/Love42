package com.mylittleproject.love42.ui

import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.firestore.ktx.toObject
import com.mylittleproject.love42.R
import com.mylittleproject.love42.data.DetailedUserInfo
import com.mylittleproject.love42.keys.TEAM_ID
import com.mylittleproject.love42.repository.FirebaseRepository
import com.mylittleproject.love42.repository.PrivateInfoRepository
import com.mylittleproject.love42.tools.Event
import com.mylittleproject.love42.tools.NAME_TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
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
    private val _selectedProfile: MutableLiveData<DetailedUserInfo> by lazy { MutableLiveData() }
    val selectedProfile: LiveData<DetailedUserInfo> get() = _selectedProfile
    private val _selectProfileEvent: MutableLiveData<Event<Unit>> by lazy { MutableLiveData() }
    val selectProfileEvent: LiveData<Event<Unit>> get() = _selectProfileEvent
    private val _openSchemeEvent: MutableLiveData<Event<String>> by lazy { MutableLiveData() }
    val openSchemeEvent: LiveData<Event<String>> get() = _openSchemeEvent
    private val _openURLEvent: MutableLiveData<Event<String>> by lazy { MutableLiveData() }
    val openURLEvent: LiveData<Event<String>> get() = _openURLEvent
    private val _sendEmailEvent: MutableLiveData<Event<Array<String>>> by lazy { MutableLiveData() }
    val sendEmailEvent: LiveData<Event<Array<String>>> get() = _sendEmailEvent
    private val _matchEvent: MutableLiveData<Event<Unit>> by lazy { MutableLiveData() }
    val matchEvent: LiveData<Event<Unit>> get() = _matchEvent
    val preferredLanguages = myProfile.switchMap {
        liveData {
            if (it != null) {
                emit(it.languages.toList())
            }
        }
    }

    val selectedPreferredLanguages = selectedProfile.switchMap {
        liveData {
            if (it != null) {
                emit(it.languages.toList())
            }
        }
    }

    fun onMatchClick(matchInfo: DetailedUserInfo) {
        Log.d(NAME_TAG, "Selected: $matchInfo")
        _selectedProfile.value = matchInfo
        _selectProfileEvent.value = Event(Unit)
    }

    fun onSlackButtonClick() {
        selectedProfile.value?.let {
            _openSchemeEvent.value = Event("slack://user?team=$TEAM_ID&id=${it.slackMemberID}")
        }
    }

    fun collectCandidates() {
        viewModelScope.launch {
            myProfile.value?.let { me ->
                firebaseRepository.candidatesInFlow(
                    me.isMale,
                    me.campus,
                    me.likes,
                    me.dislikes,
                    me.matches
                ).catch { throwable ->
                    Log.w(NAME_TAG, "Candidate fetch failed", throwable)
                }.collect {
                    Log.d(NAME_TAG, "ViewModel candidates: $it")
                    _candidateProfiles.value = it.sortedBy { candidate -> candidate.timeStamp }
                }
            }
        }
    }

    fun collectMatches() {
        viewModelScope.launch {
            myProfile.value?.let { me ->
                firebaseRepository.matchesInFlow(me.matches)
                    .catch { throwable ->
                        Log.w(NAME_TAG, "Matches fetch failed", throwable)
                    }.collect {
                        Log.d(NAME_TAG, "Matches fetched: $it")
                        _matchProfiles.value = it
                    }
            }
        }
    }

    fun onGitHubButtonClick() {
        selectedProfile.value?.let {
            _openURLEvent.value = Event("https://github.com/${it.gitHubID}")
        }
    }

    fun on42ButtonClick() {
        selectedProfile.value?.let {
            _openURLEvent.value = Event("https://profile.intra.42.fr/users/${it.intraID}")
        }
    }

    fun onEmailButtonClick() {
        selectedProfile.value?.let {
            _sendEmailEvent.value = Event(arrayOf(it.email))
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
                    _matchEvent.value = Event(Unit)
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
}