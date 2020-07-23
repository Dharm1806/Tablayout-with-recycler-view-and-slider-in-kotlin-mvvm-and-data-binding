package com.tekmindz.covidhealthcare.ui.UpdatePainLevel

import com.tekmindz.covidhealthcare.application.App
import com.tekmindz.covidhealthcare.application.App.Companion.mSharedPrefrenceManager
import com.tekmindz.covidhealthcare.constants.Constants
import com.tekmindz.covidhealthcare.repository.requestModels.UpdatePainLevel
import com.tekmindz.covidhealthcare.repository.responseModel.PatientDetails
import io.reactivex.Observable
import retrofit2.Response


class UpdatePainLevelRepository {

    /*request to update the patient details from  api*/
    fun updatePainLevel(updatePainLevel:UpdatePainLevel): Observable<Response<PatientDetails>> =
        App.healthCareApi.updatePainLevel(
            updatePainLevel,
            "bearer " + mSharedPrefrenceManager.getValueString(Constants.PREF_ACCESS_TOKEN)!!
        )


}
