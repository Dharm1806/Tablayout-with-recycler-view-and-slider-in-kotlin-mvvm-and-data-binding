package com.tekmindz.covidhealthcare.ui.patientAnalytics

import android.app.Application
import android.util.Log
import com.google.gson.Gson
import com.tekmindz.covidhealthcare.application.App
import com.tekmindz.covidhealthcare.application.App.Companion.mSharedPrefrenceManager
import com.tekmindz.covidhealthcare.constants.Constants
import com.tekmindz.covidhealthcare.repository.requestModels.PatientAnalyticsRequest
import com.tekmindz.covidhealthcare.repository.responseModel.AnalyticsResponse
import com.tekmindz.covidhealthcare.utills.Presenter
import io.reactivex.Observable
import retrofit2.Response


class AnalyticsRepository {


    /*request to get the analytics data from api of patient */
    fun getPatientAnalytics(patientAnalyticsRequest: PatientAnalyticsRequest): Observable<Response<AnalyticsResponse>> {



        return App.healthCareApi.getPatientAnalytics(
            patientAnalyticsRequest,
            "bearer " + mSharedPrefrenceManager.getValueString(Constants.PREF_ACCESS_TOKEN)!!
        )
    }

    fun refreshToken() {
        val presenter = Presenter(Application())
        presenter.refreshToken()
    }

}
