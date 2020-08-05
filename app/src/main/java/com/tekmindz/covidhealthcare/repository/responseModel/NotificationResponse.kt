package com.tekmindz.covidhealthcare.repository.responseModel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class NotificationResponse(val body: List<Body>) : BaseResponse()

data class Notification(val id: Int, val body: List<Body>, val title: String, val time: String) :
    BaseResponse()

@Parcelize
data class Body(
    val notificationId: Int,
    val obsType: String,
    val obsValue: String,
    val patientName: String,
    val status: String,
    val wardNumber: String,
    val bedNumber: String,
    val patientId: Long,
    val message: String,
    val notificationTime: String
) : Parcelable

