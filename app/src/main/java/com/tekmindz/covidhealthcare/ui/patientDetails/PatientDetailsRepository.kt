package com.tekmindz.covidhealthcare.ui.patientDetails

import com.tekmindz.covidhealthcare.application.App
import com.tekmindz.covidhealthcare.application.App.Companion.mSharedPrefrenceManager
import com.tekmindz.covidhealthcare.constants.Constants
import com.tekmindz.covidhealthcare.constants.Constants.PREF_IS_LOGIN
import com.tekmindz.covidhealthcare.repository.responseModel.DashboardCounts
import com.tekmindz.covidhealthcare.repository.responseModel.PatientDetails
import io.reactivex.Observable
import retrofit2.Response


class PatientDetailsRepository {

    /*request to get get patient detail  from patient details api*/
    fun getPatientDetails(patientId: String): Observable<Response<PatientDetails>> =
        App.healthCareApi.getPatientDetails(
            patientId,
            "bearer "+mSharedPrefrenceManager.getValueString(Constants.PREF_ACCESS_TOKEN)!!
        )
}
