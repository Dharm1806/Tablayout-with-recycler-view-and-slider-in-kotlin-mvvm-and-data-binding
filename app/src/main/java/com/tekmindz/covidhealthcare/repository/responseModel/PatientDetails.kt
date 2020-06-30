package com.tekmindz.covidhealthcare.repository.responseModel

data class PatientDetails(
    val firstName: String,
    val lastName: String,
    val gender: String,
    val dob: String,
    val bedNumber: String,
    val admittedDate: String,
    val wardNo: String,
    val wearableId: String,
    val relayId: String,
    val wearableIdentifier: String,
    val wearableName: String,
    val imageUrl: String
)
