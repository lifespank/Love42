package com.mylittleproject.love42.data

data class DetailedUserInfo(
    var name: String,
    val intraID: String,
    val intraURL: String,
    var imageURI: String,
    var email: String,
    val campus: String?,
    var gitHubURL: String = "",
    var slackMemberID: String = "",
    var bio: String = "",
    var isMale: Boolean = true,
    val languages: HashSet<String> = hashSetOf()
) {
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
}