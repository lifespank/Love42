package com.mylittleproject.love42.data

data class DetailedUserInfo(
    var name: String,
    val intraID: String,
    val intraURL: String,
    var imageURI: String,
    var email: String,
    val campus: String,
    var gitHubURL: String = "",
    var slackMemberID: String = "",
    var bio: String = "",
    var isMale: Boolean = true,
    val languages: HashSet<String> = hashSetOf(),
    val likes: HashSet<String> = hashSetOf(),
    val dislikes: HashSet<String> = hashSetOf(),
    val matches: HashSet<String> = hashSetOf()
) {

    data class FirebaseUserInfo(
        var name: String = "",
        val intraID: String = "",
        val intraURL: String = "",
        var imageURI: String = "",
        var email: String = "",
        val campus: String = "",
        var gitHubURL: String = "",
        var slackMemberID: String = "",
        var bio: String = "",
        @field:JvmField
        var isMale: Boolean = true,
        val languages: List<String> = emptyList(),
        val likes: List<String> = emptyList(),
        val dislikes: List<String> = emptyList(),
        val matches: List<String> = emptyList()
    )

    fun toHashMap() =
        hashMapOf(
            "name" to name,
            "intraID" to intraID,
            "intraURL" to intraURL,
            "imageURI" to imageURI,
            "email" to email,
            "campus" to campus,
            "gitHubURL" to gitHubURL,
            "slackMemberID" to slackMemberID,
            "bio" to bio,
            "isMale" to isMale,
            "languages" to languages.toList()
        )

    companion object {

        fun fromFirebase(firebaseUserInfo: FirebaseUserInfo): DetailedUserInfo = DetailedUserInfo(
            firebaseUserInfo.name,
            firebaseUserInfo.intraID,
            firebaseUserInfo.intraURL,
            firebaseUserInfo.imageURI,
            firebaseUserInfo.email,
            firebaseUserInfo.campus,
            firebaseUserInfo.gitHubURL,
            firebaseUserInfo.slackMemberID,
            firebaseUserInfo.bio,
            firebaseUserInfo.isMale,
            HashSet(firebaseUserInfo.languages),
            HashSet(firebaseUserInfo.likes),
            HashSet(firebaseUserInfo.dislikes),
            HashSet(firebaseUserInfo.matches)
        )
    }
}