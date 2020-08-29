package com.tekmindz.covidhealthcare.repository.responseModel

data class ECGResponse(
    val status: String,
    val liveData: List<LiveData>,
    val rqst_name: String
)

data class LiveData(
    val TsECG: Long,
    val ECG0: List<Float>,
    val ECG1: List<Float>,
    val HR: Int,
    val Respiration: List<Float>,
    val Temperature: Int,
    val PatchId: String,
    val RR_OUT: Int,
    val POSTURE: Int
)