package com.tekmindz.covidhealthcare.repository.responseModel

data class UserModel(
    val access_token: String,
    val expires_in: Long,
    val refresh_expires_in: Long,
    val refresh_token: String,
    val token_type: String,
    val not_before_policy: String,
    val session_state: String,
    val scope: String
)
