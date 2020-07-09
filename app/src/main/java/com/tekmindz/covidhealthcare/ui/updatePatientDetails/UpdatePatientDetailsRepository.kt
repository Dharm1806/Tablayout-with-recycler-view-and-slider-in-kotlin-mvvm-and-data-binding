package com.tekmindz.covidhealthcare.ui.updatePatientDetails

import com.tekmindz.covidhealthcare.application.App
import com.tekmindz.covidhealthcare.application.App.Companion.mSharedPrefrenceManager
import com.tekmindz.covidhealthcare.constants.Constants
import com.tekmindz.covidhealthcare.repository.requestModels.UpdatePatientReadings
import com.tekmindz.covidhealthcare.repository.responseModel.PatientDetails
import io.reactivex.Observable
import retrofit2.Response


class UpdatePatientDetailsRepository {

    /*request to update the patient details from  api*/
    fun updatePatientDetails(updatePatientReadings: UpdatePatientReadings): Observable<Response<PatientDetails>> =
        App.healthCareApi.updatePatientDetails(
            updatePatientReadings,
            "bearer " + mSharedPrefrenceManager.getValueString(Constants.PREF_ACCESS_TOKEN)!!
        )


}
