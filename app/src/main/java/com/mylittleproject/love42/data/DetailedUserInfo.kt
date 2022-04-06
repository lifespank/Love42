package com.mylittleproject.love42.data

data class DetailedUserInfo(
    var name: String,
    val intraID: String,
    var imageURI: String,
    var email: String,
    val campus: String,
    var gitHubID: String = "",
    var slackMemberID: String = "",
    var bio: String = "",
    var isMale: Boolean = true,
    val languages: HashSet<String> = hashSetOf(),
    val likes: HashSet<String> = hashSetOf(),
    val dislikes: HashSet<String> = hashSetOf(),
    val matches: HashSet<String> = hashSetOf(),
    var timeStamp: Long = System.currentTimeMillis()
) {

    data class FirebaseUserInfo(
        var name: String = "",
        val intraID: String = "",
        var imageURI: String = "",
        var email: String = "",
        val campus: String = "",
        var gitHubID: String = "",
        var slackMemberID: String = "",
        var bio: String = "",
        @field:JvmField
        var isMale: Boolean = true,
        val languages: List<String> = emptyList(),
        val likes: List<String> = emptyList(),
        val dislikes: List<String> = emptyList(),
        val matches: List<String> = emptyList(),
        var timeStamp: Long = 0L
    )

    fun toHashMap() =
        hashMapOf(
            "name" to name,
            "intraID" to intraID,
            "imageURI" to imageURI,
            "email" to email,
            "campus" to campus,
            "gitHubID" to gitHubID,
            "slackMemberID" to slackMemberID,
            "bio" to bio,
            "isMale" to isMale,
            "languages" to languages.toList(),
            "likes" to likes.toList(),
            "dislikes" to dislikes.toList(),
            "matches" to matches.toList(),
            "timeStamp" to timeStamp
        )

    companion object {

        fun fromFirebase(firebaseUserInfo: FirebaseUserInfo): DetailedUserInfo = DetailedUserInfo(
            firebaseUserInfo.name,
            firebaseUserInfo.intraID,
            firebaseUserInfo.imageURI,
            firebaseUserInfo.email,
            firebaseUserInfo.campus,
            firebaseUserInfo.gitHubID,
            firebaseUserInfo.slackMemberID,
            firebaseUserInfo.bio,
            firebaseUserInfo.isMale,
            HashSet(firebaseUserInfo.languages),
            HashSet(firebaseUserInfo.likes),
            HashSet(firebaseUserInfo.dislikes),
            HashSet(firebaseUserInfo.matches),
            firebaseUserInfo.timeStamp
        )
    }
}