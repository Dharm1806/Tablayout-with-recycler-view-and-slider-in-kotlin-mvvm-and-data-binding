package com.tekmindz.covidhealthcare.repository.requestModels

data class UpdatePatientReadings (val patientId:String, val bedNumber:String, val wardNumber:String, val sys:String, val dia:String)