package com.tekmindz.covidhealthcare.ui.notifications


import android.app.Application
import com.tekmindz.covidhealthcare.application.App
import com.tekmindz.covidhealthcare.application.App.Companion.mSharedPrefrenceManager
import com.tekmindz.covidhealthcare.constants.Constants
import com.tekmindz.covidhealthcare.repository.responseModel.NotificationResponse
import com.tekmindz.covidhealthcare.repository.responseModel.UserInfoBody
import com.tekmindz.covidhealthcare.utills.Presenter
import com.tekmindz.covidhealthcare.utills.Utills
import io.reactivex.Observable
import retrofit2.Response


class NotificationRepository {


    /*request to notification list from api */

    fun getNotifications(): Observable<Response<NotificationResponse>> {
        return App.healthCareApi.getNotifications(
            "bearer " + mSharedPrefrenceManager.getValueString(Constants.PREF_ACCESS_TOKEN)!!
        )
    }

    fun refreshToken() {
        val presenter = Presenter(Application())
        presenter.refreshToken()
    }

    fun isPatient(): Boolean =
        Utills.isPatient(App.mSharedPrefrenceManager.get<UserInfoBody>(Constants.PREF_USER_INFO))
}
