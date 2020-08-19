package com.tekmindz.covidhealthcare.ui.UpdatePainLevel

import android.util.Log
import com.google.gson.Gson
import com.tekmindz.covidhealthcare.application.App
import com.tekmindz.covidhealthcare.application.App.Companion.mSharedPrefrenceManager
import com.tekmindz.covidhealthcare.constants.Constants
import com.tekmindz.covidhealthcare.repository.requestModels.UpdateManualObservations
import com.tekmindz.covidhealthcare.repository.responseModel.EditProfileResponse
import io.reactivex.Observable
import retrofit2.Response


class UpdatePainLevelRepository {

    /*request to update the patient pain level from  api*/
    fun updatePainLevel(updatePainLevel: UpdateManualObservations): Observable<Response<EditProfileResponse>> {
        Log.e("resure", "${Gson().toJson(updatePainLevel)}")
        return App.healthCareApi.updatePainLevel(
            updatePainLevel,
            "bearer " + mSharedPrefrenceManager.getValueString(Constants.PREF_ACCESS_TOKEN)!!
        )
    }

}
