package com.tekmindz.covidhealthcare.constants

import java.text.SimpleDateFormat


object Constants {
    //base url for apis
    const val BASE_URL = "https://api.github.com/"

    //endpoints for apis
    const val LOGIN_END_POINTS = "repos/firebase/firebase-ios-sdk/issues"
    const val CHECK_ITS_PASSWORD_END_POINTS = "repos/firebase/firebase-ios-sdk/issues"
    const val SET_PASSWORD_API_END_POINTS = "repos/firebase/firebase-ios-sdk/issues"

    private const val SERVER_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    private const val APP_DATE_FORMAT = "dd-MM-yyyy HH:mm"
    const val BASIC = "Basic "
    val ARG_OBJECT = "object"

    var isLoginRequired: Boolean = true
    const val HEADER_CONTENT_TYPE = "Content-Type: application/json"
    const val HEADER_AUTHRIZATION = "Authorization"
    const val CLIENT_ID: String = "covid"
    const val CLIENT_SECRET = "covid"
    const val FILTER_DATE_FORMAT = "yyyy-MM-dd"
    const val PASSWROD = "PASSWORD"
    const val EXTRA_GRANT_TYPE = "grantType"
    const val SET_PASSWROD = "Set Password"

    //parse server date to app format

    fun parseDate(date: String): String {
        val parser = SimpleDateFormat(SERVER_DATE_FORMAT)
        val formatter = SimpleDateFormat(APP_DATE_FORMAT)
        return formatter.format(parser.parse(date))
    }


}