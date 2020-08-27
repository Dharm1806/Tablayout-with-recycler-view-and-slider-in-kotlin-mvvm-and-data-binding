package com.tekmindz.covidhealthcare.repository.requestModels

data class LoginRequest(
    val username: String,
    val password: String,
    val grant_type: String
)


