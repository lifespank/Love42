package com.mylittleproject.love42.data

data class DetailedUserInfo(
    var name: String,
    val intraID: String,
    val intraURL: String,
    var imageURI: String,
    var email: String,
    var gitHubURL: String = "",
    var slackMemberID: String = "",
    var bio: String = "",
    var isMale: Boolean = true,
    val languages: HashSet<String> = hashSetOf()
)