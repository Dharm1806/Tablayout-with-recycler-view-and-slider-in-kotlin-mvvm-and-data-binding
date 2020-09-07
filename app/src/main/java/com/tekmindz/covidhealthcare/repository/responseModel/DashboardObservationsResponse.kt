package com.tekmindz.covidhealthcare.repository.responseModel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class DashboardObservationsResponse(
    val body: List<observations>
) : BaseResponse()

@Parcelize
data class observations(
    var firstName: String,
    var lastName: String,
    var bedNumber: String,
    var wardNo: String,
    var heartRate: Double,
    var respirationRate: Double,
    var bodyTemperature: Double,
    var status: String,
    var observationDateTime: String,
    var imageUrl: String,
    var patientId: Long,
    var gender: String,
    var dob: String,
    var admittedDate: String,
    var wearableId: String,
    var relayId: String,
    var wearableIdentifier: String,
    var wearableName: String
) : Parcelable

data class DashboardCounts(
    val body: Counts
) : BaseResponse()

data class Counts(
    val recovered: String,
    val total: String,
    val critical: String,
    val underControl: String
)

data class UserInfo(val body: UserInfoBody) : BaseResponse()

data class UserInfoBody(
    val userId: Long,
    val firstName: String,
    val lastName: String,
    val gender: String,
    val dob: String="",
    val bedNumber: String,
    val wardNo: String,
    val admittedDate: String,
    val wearableId: String,
    val relayId: String,
    val wearableIdentifier: String,
    val wearableName: String,
    val imageUrl: String,
    val patientId: Long,
    val emergencyContact: String,
    val roles: List<String>
)