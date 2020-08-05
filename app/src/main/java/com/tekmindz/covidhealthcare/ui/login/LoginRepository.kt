package com.tekmindz.covidhealthcare.ui.login

import com.tekmindz.covidhealthcare.application.App
import com.tekmindz.covidhealthcare.application.App.Companion.mSharedPrefrenceManager
import com.tekmindz.covidhealthcare.constants.Constants
import com.tekmindz.covidhealthcare.constants.Constants.PREF_ACCESS_TOKEN
import com.tekmindz.covidhealthcare.constants.Constants.PREF_IS_LOGIN
import com.tekmindz.covidhealthcare.repository.requestModels.LoginRequest
import com.tekmindz.covidhealthcare.repository.requestModels.UserInfoRequest
import com.tekmindz.covidhealthcare.repository.responseModel.UserInfo
import com.tekmindz.covidhealthcare.repository.responseModel.UserInfoBody
import com.tekmindz.covidhealthcare.repository.responseModel.UserModel
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginRepository : Callback<UserModel> {

    /*request to login user from login api*/
    fun login(loginRequestModel: LoginRequest): Observable<Response<UserModel>> =
        App.healthCareApiLogin.login(
            loginRequestModel.client_id,
            loginRequestModel.username,
            loginRequestModel.password,
            loginRequestModel.grant_type,
            loginRequestModel.client_secret

        )

    /*request to login user from login api*/
    fun getUserInfo(): Observable<Response<UserInfo>> =
        App.healthCareApi.getUserInfo(
            "bearer " + App.mSharedPrefrenceManager.getValueString(PREF_ACCESS_TOKEN)!!,
            UserInfoRequest(Constants.device_token)
        )
    /*request to get counts  from dashboard count api*/

    fun refreshToken(
        clientID: String,
        refreshGrantType: String

    ) {
        val valueString = App.mSharedPrefrenceManager.getValueString(Constants.PREF_REFRESH_TOKEN)
        val mResponse = App.healthCareApiLogin
            .refreshToken(
               clientID, valueString!!, refreshGrantType
            )

        mResponse.enqueue(this)
    }

    fun saveUserDate(key: String, value: String) = mSharedPrefrenceManager.saveString(key, value)

    fun setISLogin(isLogin: Boolean) = mSharedPrefrenceManager.setIsLogin(PREF_IS_LOGIN, isLogin)
    override fun onFailure(call: Call<UserModel>, t: Throwable) {

    }

    override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
        if (response.isSuccessful && response.code() == 200 && response.body() != null)
            saveUserDate(Constants.PREF_ACCESS_TOKEN, response.body()?.access_token!!)
        saveUserDate(Constants.PREF_EXPIRES_IN, response.body()?.expires_in.toString())
        saveUserDate(
            Constants.PREF_REFRESH_EXPIRES_IN,
            response.body()?.refresh_expires_in.toString()
        )

    }

    fun saveUserInfo(key: String, value: UserInfoBody) {
        App.mSharedPrefrenceManager.put(value, key)
    }

}
