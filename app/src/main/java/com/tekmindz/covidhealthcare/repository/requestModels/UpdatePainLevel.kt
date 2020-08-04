package com.tekmindz.covidhealthcare.repository.requestModels


data class UpdatePainLevel(

    val patientId: String,
    val observationType: String,
    val value: String,
    val observationDateTime: String
)

data class UpdateManualObservations
    (val data: List<UpdatePainLevel>)