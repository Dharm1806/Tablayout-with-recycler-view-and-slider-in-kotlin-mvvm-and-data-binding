package com.tekmindz.covidhealthcare.constants

import java.text.SimpleDateFormat


object Constants {
    //base url for apis
    // const val LOGIN_BASE_URL = "http://54.218.218.85:8080/"
    // var BASE_URL = "http://34.210.115.120:8081/"

    val OBSERVATION: String? = "observation"

    //dev base url
    const val LOGIN_BASE_URL = "http://54.218.218.85:8080/"
    var BASE_URL = "http://54.188.160.119:8081/"

    //endpoints for apis
    const val LOGIN_END_POINTS = "auth/realms/test/protocol/openid-connect/token"
    //const val LOGIN_END_POINTS = "auth/realms/ctms-qa/protocol/openid-connect/token"

    const val UPDATE_CONTACT_NUMBER = "api/patient/addEmegerncyContact"
    const val GET_EMERGENCY_CONTACT_NUMBER = "api/patient/getEmergencyContact/{patientId}"

    const val GET_USER_INFO = "api/dashboard/user/getUserInfo"
    const val REFRESH_TOKEN_END_POINTS = "auth/realms/test/protocol/openid-connect/token"
    const val GET_DASHBOARD_OBSERVATIONS = "api/dashboard/observations/"
    const val GET_DASHBOARD_COUNTS = "api/dashboard/ragstatus/count"
    const val GET_PATIENT_DETAILS = "api/dashboard/patientDetails/{patientId}"
    const val GET_PATIENT_OBSERVATIONS = "api/dashboard/mobile/patientDetails/{patientId}"
    const val GET_PATIENT_ANALYTICS = "api/dashboard/patient/observations"
    const val UPDATE_MANUAL_OBSERVSTION = "api/manual/observation/create"
    const val SET_PASSWORD_API_END_POINTS = "repos/firebase/firebase-ios-sdk/issues"
    const val GET_NOTIFICATIONS = "api/dashboard/mobile/patientDetails/{patientId}"

    const val SERVER_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    const val SERVER_DOB_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.ms"
    const val SERVER_GRAPH_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"
    const val DELAY_IN_API_CALL = 10000L
    const val APP_DATE_FORMAT = "dd/MM/yyyy"
    const val BASIC = "Basic "
    val ARG_TIME = "time"
    val ARG_PATIENT_NAME = "patientName"

    val ARG_FROM_TIME = "from_time"
    val ARG_TO_TIME = "to_time"
    val PATIENT_ID = "patientId"
    const val TIME_DIFFERENCE = 1800

    const val SOS_NUMBER = 9717514944

    var isLoginRequired: Boolean = true
    const val HEADER_CONTENT_TYPE = "Content-Type: application/json"
    const val HEADER_AUTHRIZATION = "Authorization"
    const val CLIENT_ID: String = "test-app"
    // const val CLIENT_ID: String = "ctms-qa-app"
    //const val CLIENT_SECRET = "cc4c9228-0278-4d65-ada4-42808a3aa20b"
    const val CLIENT_SECRET = "2d78ccf8-0781-4ad9-a63b-d66ca37db970"
    const val FILTER_DATE_FORMAT = "yyyy-MM-dd hh-mm-ss"
    const val PASSWROD = "password"
    const val EXTRA_GRANT_TYPE = "grantType"
    const val SET_PASSWROD = "Set Password"
    const val REFRESH_GRANT_TYPE = "refresh_token"


    const val PREF_IS_LOGIN = "is_login"
    const val PREF_BASE_URL = "base_url"
    const val PREF_ACCESS_TOKEN = "access_token"
    const val PREF_USER_INFO = "user_info"

    const val PREF_EXPIRES_IN = "expires_in"
    const val PREF_REFRESH_EXPIRES_IN = "refresh_expires_in"
    const val PREF_REFRESH_TOKEN = "refresh_token"
    const val PREF_TOKEN_TYPE = "token_type"
    const val PREF_NOT_BEFORE_POLICY = "not_before_policy"
    const val PREF_SESSION_STATE = "session_state"
    const val PREF_SCOPE = "scope"
    const val PREF_USER_TYPE = "user_type"
    const val PREF_USER_ID = "user_id"


    const val STATE_CRITICAL = "critical"
    const val STATE_UNDER_CONTROL = "underControl"
    const val STATE_RECOVERED = "recovered"
    const val UNIT_HEART_RATE = "BPM"
    const val UNIT_RESPIRATION = "BrPM"
    const val UNIT_TEMPERATURE = "°C"

    const val OBSERVATION_TYPE_SPO2 = "spo2"
    const val OBSERVATION_TYPE_BP_HIGH = "bpHigh"
    const val OBSERVATION_TYPE_BP_LOW = "bpLow"

    const val OBSERVATION_TYPE_PAIN_LEVEL = "painlevel"


    const val DATE_RANGE = "Date Range"

    const val BROADCAST_RECEIVER_NAME = "NotificationReceiver"


    const val HEALTH_CARE_WORKER_ROLE = "healthcare-worker"
    const val PATIENT_ROLE = "patient"
    const val SUPER_ADMIN_ROLE = "super-admin"
    const val OFFLINE_ACCESS_ROLE = "offline_access"
    const val UMA_AUTHORIZATION_ROLE = "uma_authorization"
    const val SUPERVISOR_ROLE = "supervisor"
    //parse server date to app format

    fun parseDate(date: String): String {
        val parser = SimpleDateFormat(SERVER_DATE_FORMAT)
        val formatter = SimpleDateFormat(APP_DATE_FORMAT)
        return formatter.format(parser.parse(date))
    }



}