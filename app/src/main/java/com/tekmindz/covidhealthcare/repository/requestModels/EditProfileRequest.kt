package com.tekmindz.covidhealthcare.repository.requestModels

data class EditProfileRequest(val mobileNumber:String, val emergencyContactNumber:String, val patientId:String)