package com.tekmindz.covidhealthcare.ui.dashboard

import android.util.Log
import com.google.gson.Gson
import com.tekmindz.covidhealthcare.application.App
import com.tekmindz.covidhealthcare.application.App.Companion.mSharedPrefrenceManager
import com.tekmindz.covidhealthcare.constants.Constants
import com.tekmindz.covidhealthcare.constants.Constants.PREF_IS_LOGIN
import com.tekmindz.covidhealthcare.repository.requestModels.DashBoardObservations
import com.tekmindz.covidhealthcare.repository.requestModels.LoginRequest
import com.tekmindz.covidhealthcare.repository.responseModel.DashboardCounts
import com.tekmindz.covidhealthcare.repository.responseModel.DashboardObservationsResponse
import com.tekmindz.covidhealthcare.repository.responseModel.UserModel
import com.tekmindz.covidhealthcare.utills.ResponseList
import io.reactivex.Observable
import retrofit2.Response


class DashboardRepository {


    fun getIsLogin():Boolean = mSharedPrefrenceManager.getIsLogin(PREF_IS_LOGIN)

    /*request to get the observation data from api */
    fun getDashboardObservations(dashBoardObservations: DashBoardObservations): Observable< List<DashboardObservationsResponse>> {

        Log.e("time ", "${Gson().toJson(dashBoardObservations)}")
        Log.e("access_token", "${ mSharedPrefrenceManager.getValueString(Constants.PREF_ACCESS_TOKEN)!!}")

     return  App.healthCareApi.getDashboardPatientList(
            dashBoardObservations,
            "bearer "+mSharedPrefrenceManager.getValueString(Constants.PREF_ACCESS_TOKEN)!!
        )
    }

    /*request to get counts  from dashboard count api*/
    fun getDashBoardCount(dashBoardObservations: DashBoardObservations): Observable<Response<DashboardCounts>> =
        App.healthCareApi.getDashboardsCounts(
            dashBoardObservations,
            "bearer "+mSharedPrefrenceManager.getValueString(Constants.PREF_ACCESS_TOKEN)!!
        )
}
