package com.tekmindz.covidhealthcare.repository.responseModel

data class PatientDetails(
    val body: Details
):BaseResponse()


data class Details(
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

data class PatientObservations(
    val body: PatientObservation
):BaseResponse()

data class PatientObservation(

    val heartRate: String,
    val respirationRate: String,
    val bodyTemprature: String,
    val status: String,
    val bpLow: String,
    val bpHigh: String,
    val painlevel: String,
    val spo2: String
)