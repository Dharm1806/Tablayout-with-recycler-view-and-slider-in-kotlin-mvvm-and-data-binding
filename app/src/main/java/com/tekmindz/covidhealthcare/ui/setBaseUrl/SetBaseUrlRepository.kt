package com.tekmindz.covidhealthcare.ui.setBaseUrl

import com.tekmindz.covidhealthcare.application.App
import com.tekmindz.covidhealthcare.application.App.Companion.mSharedPrefrenceManager
import com.tekmindz.covidhealthcare.constants.Constants
import com.tekmindz.covidhealthcare.constants.Constants.PREF_IS_LOGIN
import com.tekmindz.covidhealthcare.repository.requestModels.LoginRequest
import com.tekmindz.covidhealthcare.repository.responseModel.UserModel
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SetBaseUrlRepository : Callback<UserModel> {

    /*request to login user from login api*/
    fun login(loginRequestModel: LoginRequest): Observable<Response<UserModel>> =
        App.healthCareApiLogin.login(
            RequestBody.create(MediaType.parse("multipart/form-data"), loginRequestModel.username),
            RequestBody.create(MediaType.parse("multipart/form-data"), loginRequestModel.password),
            RequestBody.create(MediaType.parse("multipart/form-data"), loginRequestModel.grant_type)

        )

    /*request to get counts  from dashboard count api*/

    fun refreshToken(
        clientID: String,
        refreshGrantType: String

    ){
        val valueString =  App.mSharedPrefrenceManager.getValueString(Constants.PREF_REFRESH_TOKEN)
        val mResponse = App.healthCareApiLogin
            .refreshToken(
                RequestBody.create(MediaType.parse("multipart/form-data"),valueString!!),
                RequestBody.create(MediaType.parse("multipart/form-data"), refreshGrantType)
            )

        mResponse.enqueue(this)
    }

    fun saveUserDate(key: String, value: String) = mSharedPrefrenceManager.saveString(key, value)

    fun setISLogin(isLogin: Boolean) = mSharedPrefrenceManager.setIsLogin(PREF_IS_LOGIN, isLogin)
    override fun onFailure(call: Call<UserModel>, t: Throwable) {

    }

    override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
        if (response.isSuccessful && response.code()==200 && response.body()!= null)
        saveUserDate(Constants.PREF_ACCESS_TOKEN, response.body()?.access_token!!)
        saveUserDate(Constants.PREF_EXPIRES_IN, response.body()?.expires_in.toString())
        saveUserDate(
            Constants.PREF_REFRESH_EXPIRES_IN,
            response.body()?.refresh_expires_in.toString()
        )

    }

}
