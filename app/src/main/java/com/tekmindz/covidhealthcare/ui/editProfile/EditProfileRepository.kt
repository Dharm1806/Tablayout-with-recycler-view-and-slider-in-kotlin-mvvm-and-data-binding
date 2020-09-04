package com.tekmindz.covidhealthcare.ui.editProfile

import android.app.Activity
import com.tekmindz.covidhealthcare.HomeActivity
import com.tekmindz.covidhealthcare.application.App
import com.tekmindz.covidhealthcare.application.App.Companion.mSharedPrefrenceManager
import com.tekmindz.covidhealthcare.constants.Constants
import com.tekmindz.covidhealthcare.constants.Constants.PREF_ACCESS_TOKEN
import com.tekmindz.covidhealthcare.constants.Constants.PREF_IS_LOGIN
import com.tekmindz.covidhealthcare.repository.requestModels.EditProfileRequest
import com.tekmindz.covidhealthcare.repository.responseModel.EditProfileResponse
import com.tekmindz.covidhealthcare.repository.responseModel.EmergencyContact
import com.tekmindz.covidhealthcare.repository.responseModel.UserInfoBody
import com.tekmindz.covidhealthcare.repository.responseModel.UserModel
import com.tekmindz.covidhealthcare.utills.Utills
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EditProfileRepository : Callback<UserModel> {
    lateinit var context: Activity
    /*request to update mobile number and emergency contact number using api */
    fun updateContactInfo(editProfileRequest: EditProfileRequest): Observable<Response<EditProfileResponse>> =
        App.healthCareApi.updateProfile(
            editProfileRequest, "bearer "+App.mSharedPrefrenceManager.getValueString(
                PREF_ACCESS_TOKEN)
        )

    /*request to get counts  from dashboard count api*/

    fun refreshToken(
        clientID: String,
        refreshGrantType: String,
        mContext: Activity

    ){
        if (Constants.refreshTokenCall) {
            this.context = mContext

            val valueString =
                App.mSharedPrefrenceManager.getValueString(Constants.PREF_REFRESH_TOKEN)

            val mResponse = App.healthCareApiLogin
                .refreshToken(
                    RequestBody.create(MediaType.parse("multipart/form-data"), valueString!!),
                    RequestBody.create(MediaType.parse("multipart/form-data"), refreshGrantType)
                )
            mResponse.enqueue(this)
        }
    }

    fun saveUserDate(key: String, value: String) = mSharedPrefrenceManager.saveString(key, value)

    fun setISLogin(isLogin: Boolean) = mSharedPrefrenceManager.setIsLogin(PREF_IS_LOGIN, isLogin)
    override fun onFailure(call: Call<UserModel>, t: Throwable) {

    }

    override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
        if (response.isSuccessful && response.code() == 200 && response.body() != null) {
            saveUserDate(Constants.PREF_ACCESS_TOKEN, response.body()?.access_token!!)
            saveUserDate(Constants.PREF_EXPIRES_IN, response.body()?.expires_in.toString())
            saveUserDate(
                Constants.PREF_REFRESH_EXPIRES_IN,
                response.body()?.refresh_expires_in.toString()
            )
        } else if (response.code() == 401) {
            Constants.refreshTokenCall = false

            val homeActivity: HomeActivity = context as HomeActivity
            homeActivity.refreshTokenExpire(context)
        }


    }

    fun isPatient() =
        Utills.isPatient(App.mSharedPrefrenceManager.get<UserInfoBody>(Constants.PREF_USER_INFO))

    fun getEmeregncyContact(patientId: String): Observable<Response<EmergencyContact>> =
        App.healthCareApi.getEmergencyContact(
            patientId, "bearer " + App.mSharedPrefrenceManager.getValueString(PREF_ACCESS_TOKEN)
        )

}
