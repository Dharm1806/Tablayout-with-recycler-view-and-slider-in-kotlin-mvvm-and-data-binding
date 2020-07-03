package com.tekmindz.covidhealthcare.repository.responseModel

data class DashboardObservationsResponse(
    val firstName: String,
    val lastName: String,
    val bedNumber: String,
    val wardNo: String,
    val heartRate: Double,
    val respirationRate: Double,
    val bodyTemprature: Double,
    val status: String,
    val observationDateTime: String,
    val imageUrl: String,
    val patientId: Long
)

data class DashboardCounts(
    val recovered: String,
    val total: String,
    val critical: String,
    val underControl: String
)