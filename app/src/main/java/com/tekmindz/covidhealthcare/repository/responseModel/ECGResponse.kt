package com.tekmindz.covidhealthcare.repository.responseModel

data class ECGResponse(
    val ECG_DATA:List<EcgData>
)
data class EcgData(
    val TsECG:String,
    val ECG0:List<String>,
    val ECG1:List<String>,
    val HR:String,
    val Respiration:List<String>,
    val Temperature:String,
    val PatchId:String,
    val POSTURE:String
)