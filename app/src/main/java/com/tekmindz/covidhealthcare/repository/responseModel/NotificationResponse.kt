package com.tekmindz.covidhealthcare.repository.responseModel

import java.util.concurrent.TimeUnit

data class NotificationResponse(val body: List<Notification>):BaseResponse()

data class Notification (val id:Int, val body:Body, val title:String, val time:String)
data class Body(val patientId:Long, val message:String, val patientStatus:String, val bedNo:String, val wardNo:String, val notificationType:String, val reading:String, val unit: String)