package com.tekmindz.covidhealthcare.repository.responseModel

data class UserModel(
    val access_token: String,
    val expires_in: Long,
    val refresh_expires_in: Long,
    val refresh_token: String,
    val token_type: String,

    val session_state: String,
    val scope: String,
    val email: String,
    val jti: String
)

data class Logout(var isLOgout: Boolean)
