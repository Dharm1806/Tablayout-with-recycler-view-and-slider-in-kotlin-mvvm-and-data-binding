package com.tekmindz.covidhealthcare.ui.patientAnalytics

import android.app.Activity
import android.app.Application
import com.tekmindz.covidhealthcare.application.App
import com.tekmindz.covidhealthcare.application.App.Companion.mSharedPrefrenceManager
import com.tekmindz.covidhealthcare.constants.Constants
import com.tekmindz.covidhealthcare.repository.requestModels.PatientAnalyticsRequest
import com.tekmindz.covidhealthcare.repository.responseModel.AnalyticsResponse
import com.tekmindz.covidhealthcare.repository.responseModel.UserInfoBody
import com.tekmindz.covidhealthcare.utills.Presenter
import com.tekmindz.covidhealthcare.utills.Utills
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

    fun refreshToken(context: Activity) {
        val presenter = Presenter(Application())
        presenter.refreshToken(context = context)
    }

    fun isPatient(): Boolean =
        Utills.isPatient(App.mSharedPrefrenceManager.get<UserInfoBody>(Constants.PREF_USER_INFO))


}
