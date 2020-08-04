package com.tekmindz.covidhealthcare.repository.responseModel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class DashboardObservationsResponse(
    val body: List<observations>
) : BaseResponse()

@Parcelize
data class observations(
    var firstName: String,
    val lastName: String,
    val bedNumber: String,
    val wardNo: String,
    val heartRate: Double,
    val respirationRate: Double,
    val bodyTemprature: Double,
    val status: String,
    val observationDateTime: String,
    val imageUrl: String,
    val patientId: Long,
    val gender: String,
    val dob: String,
    val admittedDate: String,
    val wearableId: String,
    val relayId: String,
    val wearableIdentifier: String,
    val wearableName: String
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
    val dob: String,
    val bedNumber: String,
    val wardNo: String,
    val admittedDate: String,
    val wearableId: String,
    val relayId: String,
    val wearableIdentifier: Double,
    val wearableName: String,
    val imageUrl: String,
    val patientId: Long,
    val emergencyContact: String,
    val roles: List<String>
)