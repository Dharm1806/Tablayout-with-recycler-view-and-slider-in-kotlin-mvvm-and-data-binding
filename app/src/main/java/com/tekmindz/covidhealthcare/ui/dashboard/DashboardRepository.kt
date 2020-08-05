package com.tekmindz.covidhealthcare.ui.dashboard

import android.app.Application
import com.tekmindz.covidhealthcare.application.App
import com.tekmindz.covidhealthcare.application.App.Companion.mSharedPrefrenceManager
import com.tekmindz.covidhealthcare.constants.Constants
import com.tekmindz.covidhealthcare.constants.Constants.PREF_IS_LOGIN
import com.tekmindz.covidhealthcare.constants.Constants.PREF_REFRESH_TOKEN
import com.tekmindz.covidhealthcare.repository.requestModels.DashBoardObservations
import com.tekmindz.covidhealthcare.repository.requestModels.DateFilter
import com.tekmindz.covidhealthcare.repository.responseModel.DashboardCounts
import com.tekmindz.covidhealthcare.repository.responseModel.DashboardObservationsResponse
import com.tekmindz.covidhealthcare.repository.responseModel.UserInfoBody
import com.tekmindz.covidhealthcare.repository.responseModel.UserModel
import com.tekmindz.covidhealthcare.utills.Presenter
import com.tekmindz.covidhealthcare.utills.Utills
import io.reactivex.Observable
import retrofit2.Response


class DashboardRepository {


    fun getIsLogin(): Boolean = mSharedPrefrenceManager.getIsLogin(PREF_IS_LOGIN)

    /*request to get the observation data from api */
    fun getDashboardObservations(dashBoardObservations: DashBoardObservations): Observable<Response<DashboardObservationsResponse>> {

       /* Log.e("time ", "${Gson().toJson(dashBoardObservations)}")
        Log.e(
            "access_token",
            "${mSharedPrefrenceManager.getValueString(Constants.PREF_ACCESS_TOKEN)!!}"
        )*/

        return App.healthCareApi.getDashboardPatientList(
            dashBoardObservations,
            "bearer " + mSharedPrefrenceManager.getValueString(Constants.PREF_ACCESS_TOKEN)!!
        )
    }

    /*request to get counts  from dashboard count api*/
    fun getDashBoardCount(dateFilter: DateFilter): Observable<Response<DashboardCounts>> =
        App.healthCareApi.getDashboardsCounts(
            dateFilter,
            "Bearer " + mSharedPrefrenceManager.getValueString(Constants.PREF_ACCESS_TOKEN)!!
        )



    fun refreshToken(){
        val presenter = Presenter(Application())
        presenter.refreshToken()
    }

    fun saveRefreshToken(data: UserModel) {
        mSharedPrefrenceManager.saveString(Constants.PREF_ACCESS_TOKEN, data.access_token)
        mSharedPrefrenceManager.saveString(Constants.PREF_EXPIRES_IN, data.expires_in.toString())
        mSharedPrefrenceManager.saveString(
            Constants.PREF_REFRESH_EXPIRES_IN,
            data.refresh_expires_in.toString()
        )
        mSharedPrefrenceManager.saveString(PREF_REFRESH_TOKEN, data.refresh_token)
        mSharedPrefrenceManager.saveString(Constants.PREF_TOKEN_TYPE, data.token_type)
        //mLoginViewModel.saveUserData(PREF_NOT_BEFORE_POLICY, userData.not_before_policy)
        mSharedPrefrenceManager.saveString(Constants.PREF_SESSION_STATE, data.session_state)
        mSharedPrefrenceManager.saveString(Constants.PREF_SCOPE, data.scope)
    }

    fun isPatient() =
        Utills.isPatient(App.mSharedPrefrenceManager.get<UserInfoBody>(Constants.PREF_USER_INFO))

}
