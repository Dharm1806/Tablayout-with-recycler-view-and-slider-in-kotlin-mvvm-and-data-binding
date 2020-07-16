package com.tekmindz.covidhealthcare.repository.responseModel

data class NotificationResponse(val body: List<Notification>):BaseResponse()

data class Notification (val id:Int, val body:Body, val title:String, val time:String)
data class Body(val patientId:Long, val message:String)