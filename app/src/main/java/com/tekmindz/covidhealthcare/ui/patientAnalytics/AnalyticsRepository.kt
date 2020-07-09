package com.tekmindz.covidhealthcare.ui.patientAnalytics

import android.util.Log
import com.google.gson.Gson
import com.tekmindz.covidhealthcare.application.App
import com.tekmindz.covidhealthcare.application.App.Companion.mSharedPrefrenceManager
import com.tekmindz.covidhealthcare.constants.Constants
import com.tekmindz.covidhealthcare.repository.requestModels.PatientAnalyticsRequest
import com.tekmindz.covidhealthcare.repository.responseModel.AnalyticsResponse
import io.reactivex.Observable
import retrofit2.Response


class AnalyticsRepository {


    /*request to get the analytics data from api of patient */
    fun getPatientAnalytics(patientAnalyticsRequest: PatientAnalyticsRequest): Observable<Response<AnalyticsResponse>> {

        Log.e("time ", "${Gson().toJson(patientAnalyticsRequest)}")
        Log.e(
            "access_token",
            "${mSharedPrefrenceManager.getValueString(Constants.PREF_ACCESS_TOKEN)!!}"
        )

        return App.healthCareApi.getPatientAnalytics(
            patientAnalyticsRequest,
            "bearer " + mSharedPrefrenceManager.getValueString(Constants.PREF_ACCESS_TOKEN)!!
        )
    }

}
