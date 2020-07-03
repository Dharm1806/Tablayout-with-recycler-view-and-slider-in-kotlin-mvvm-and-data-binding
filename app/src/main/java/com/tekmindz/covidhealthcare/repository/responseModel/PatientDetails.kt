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
    val heartRate:String,
    val respirationRate :String,
    val bodyTemprature:String,
    val status:String,
    val imageUrl: String
)

data class PatientObservations(
    val heartRate:String,
    val respirationRate :String,
    val bodyTemprature:String,
    val patientStatus:String
)