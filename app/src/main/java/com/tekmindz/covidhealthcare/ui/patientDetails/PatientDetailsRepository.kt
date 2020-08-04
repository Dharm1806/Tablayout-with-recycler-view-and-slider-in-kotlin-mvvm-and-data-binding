package com.tekmindz.covidhealthcare.ui.patientDetails

import android.app.Application
import com.tekmindz.covidhealthcare.application.App
import com.tekmindz.covidhealthcare.application.App.Companion.mSharedPrefrenceManager
import com.tekmindz.covidhealthcare.constants.Constants
import com.tekmindz.covidhealthcare.repository.requestModels.UpdateManualObservations
import com.tekmindz.covidhealthcare.repository.responseModel.EditProfileResponse
import com.tekmindz.covidhealthcare.repository.responseModel.PatientDetails
import com.tekmindz.covidhealthcare.repository.responseModel.PatientObservations
import com.tekmindz.covidhealthcare.repository.responseModel.UserInfoBody
import com.tekmindz.covidhealthcare.utills.Presenter
import com.tekmindz.covidhealthcare.utills.Utills
import io.reactivex.Observable
import retrofit2.Response


class PatientDetailsRepository {

    /*request to get get patient detail  from patient details api*/
    fun getPatientDetails(patientId: String): Observable<Response<PatientDetails>> =
        App.healthCareApi.getPatientDetails(
            patientId,
            "bearer " + mSharedPrefrenceManager.getValueString(Constants.PREF_ACCESS_TOKEN)!!
        )

    /*request to get get patient detail  from patient details api*/
    fun getPatientObservations(patientId: String): Observable<Response<PatientObservations>> =
        App.healthCareApi.getPatientObservations(
            patientId,
            "bearer " + mSharedPrefrenceManager.getValueString(Constants.PREF_ACCESS_TOKEN)!!
        )

    fun refreshToken() {
        val presenter = Presenter(Application())
        presenter.refreshToken()
    }

    fun updatePainLevel(updateManualObservations: UpdateManualObservations): Observable<Response<EditProfileResponse>> =
        App.healthCareApi.updatePainLevel(
            updateManualObservations,
            "bearer " + mSharedPrefrenceManager.getValueString(Constants.PREF_ACCESS_TOKEN)!!
        )

    fun isPatient(): Boolean =
        Utills.isPatient(App.mSharedPrefrenceManager.get<UserInfoBody>(Constants.PREF_USER_INFO))

    fun getPatient(): UserInfoBody =
        (App.mSharedPrefrenceManager.get<UserInfoBody>(Constants.PREF_USER_INFO))!!

    fun isPatientAndHc(): Boolean =
        Utills.isPatientAndHc(App.mSharedPrefrenceManager.get<UserInfoBody>(Constants.PREF_USER_INFO))


}
