package com.tekmindz.covidhealthcare.repository.requestModels

data class PatientAnalyticsRequest(
    val patientId: String,
    val fromDateTime: String,
    val toDateTime: String
)