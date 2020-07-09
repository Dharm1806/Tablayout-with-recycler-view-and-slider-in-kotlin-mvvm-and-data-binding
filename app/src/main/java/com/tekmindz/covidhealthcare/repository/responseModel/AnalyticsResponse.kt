package com.tekmindz.covidhealthcare.repository.responseModel

data class AnalyticsResponse(val body: List<Analytics>):BaseResponse()
abstract class BaseResponse {
    var statusCode: Int = 0
    var message: String = ""
}
data class Analytics(
    val heartRate: Float, val respirationRate: Float,
    val bodyTemprature: Float, val observationDateTime: String, val posture: String
)
