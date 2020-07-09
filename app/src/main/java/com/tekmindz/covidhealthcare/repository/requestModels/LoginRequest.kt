package com.tekmindz.covidhealthcare.repository.requestModels

data class LoginRequest(
    val username: String,
    val password: String,
    val client_id: String,
    val grant_type: String,
    val client_secret: String
)


