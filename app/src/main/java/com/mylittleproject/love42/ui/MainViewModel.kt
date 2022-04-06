package com.mylittleproject.love42.ui

import android.util.Log
import androidx.lifecycle.*
import com.mylittleproject.love42.R
import com.mylittleproject.love42.data.DetailedUserInfo
import com.mylittleproject.love42.keys.TEAM_ID
import com.mylittleproject.love42.repository.FirebaseRepository
import com.mylittleproject.love42.repository.PrivateInfoRepository
import com.mylittleproject.love42.tools.Event
import com.mylittleproject.love42.tools.NAME_TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
    private val myProfile: LiveData<DetailedUserInfo> get() = _myProfile
    private val _localProfile: MutableLiveData<DetailedUserInfo> by lazy { MutableLiveData() }
    val localProfile: LiveData<DetailedUserInfo> get() = _localProfile
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

    val preferredLanguages = localProfile.switchMap {
        liveData {
            emit(it.languages.toList())
        }
    }

    private val candidates = myProfile.switchMap {
        liveData {
            firebaseRepository.candidatesInFlow(
                it.isMale,
                it.campus,
                it.likes,
                it.dislikes,
                it.matches
            ).catch { throwable ->
                Log.w(NAME_TAG, "Candidate fetch failed", throwable)
            }.collect {
                Log.d(NAME_TAG, "ViewModel candidates: $it")
                emit(it.sortedBy { cards -> cards.timeStamp })
            }
        }
    }

    val mutableCandidates: MutableLiveData<List<DetailedUserInfo>> =
        candidates as MutableLiveData<List<DetailedUserInfo>>

    val matches = myProfile.switchMap { me ->
        liveData {
            firebaseRepository.matchesInFlow(me.matches)
                .catch { throwable ->
                    Log.w(NAME_TAG, "Matches fetch failed", throwable)
                }.collect {
                    Log.d(NAME_TAG, "Matches fetched: $it")
                    emit(it)
                }
        }
    }

    val selectedPreferredLanguages = selectedProfile.switchMap {
        liveData {
            emit(it.languages.toList())
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
            val me = myProfile.value
            mutableCandidates.value?.toMutableList()?.let { candidateList ->
                if (candidateList.isNotEmpty() && me != null) {
                    val like = candidateList.removeFirst()
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
                    mutableCandidates.value = candidateList
                }
            }
        }
    }

    fun popDislike() {
        viewModelScope.launch {
            val me = myProfile.value
            mutableCandidates.value?.toMutableList()?.let { candidateList ->
                if (candidateList.isNotEmpty() && me != null) {
                    val dislike = candidateList.removeFirst()
                    me.dislikes.add(dislike.intraID)
                    if (firebaseRepository.uploadProfile(dislike)) {
                        Log.d(NAME_TAG, "dislike uploaded: $dislike")
                    }
                    if (firebaseRepository.uploadProfile(me)) {
                        Log.d(NAME_TAG, "me uploaded: $me")
                    }
                    mutableCandidates.value = candidateList
                }
            }
        }
    }

    fun addLanguage(language: String) {
        val languages = localProfile.value?.languages
        languages?.let {
            it.add(language)
            _localProfile.value = _localProfile.value?.copy(languages = it)
        }
    }

    fun onWhatIsSlackIDClick() {
        _popUpSlackIDDescriptionEvent.value = Event(Unit)
    }

    fun onProfileImageEditClick() {
        _loadProfileImageEvent.value = Event(Unit)
    }

    fun setImageURI(imageURI: String) {
        _localProfile.value = _localProfile.value?.copy(imageURI = imageURI)
        Log.d(NAME_TAG, "User image changed: ${localProfile.value}")
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
        val languages = localProfile.value?.languages
        languages?.let {
            it.remove(language)
            _localProfile.value = _localProfile.value?.copy(languages = it)
        }
    }

    fun uploadProfileImage() {
        viewModelScope.launch {
            val user = localProfile.value
            user?.let {
                if (it.slackMemberID.isNotBlank()) {
                    _showLoading.value = true
                    val downloadURL =
                        firebaseRepository.uploadProfileImage(it.intraID, it.imageURI)
                    if (downloadURL.isNotBlank()) {
                        _localProfile.value = it.copy(imageURI = downloadURL)
                        Log.d(NAME_TAG, "Image uploaded: ${localProfile.value}")
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
            localProfile.value?.let {
                if (firebaseRepository.uploadProfile(it)) {
                    Log.d(NAME_TAG, "Profile upload success")
                    _snackBarEvent.value = Event(R.string.profile_uploaded)
                }
            }
            _showLoading.value = false
        }
    }

    private suspend fun collectMyProfile(intraID: String) {
        firebaseRepository.myProfileInFlow(intraID)
            .catch { throwable ->
                Log.w(NAME_TAG, "My profile fetch failed", throwable)
            }.collect { me ->
                _myProfile.value = me
                if (localProfile.value == null) {
                    _localProfile.value = me
                }
                Log.d(NAME_TAG, "My profile: ${myProfile.value}")
            }
    }

    fun downloadProfile() {
        viewModelScope.launch {
            val intraID = privateInfoRepository.fetchIntraID()
            intraID.getOrNull()?.let {
                collectMyProfile(it)
            }
        }
    }
}