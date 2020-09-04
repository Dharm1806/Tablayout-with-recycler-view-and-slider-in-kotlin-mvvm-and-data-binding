package com.tekmindz.covidhealthcare.ui.login

import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.tekmindz.covidhealthcare.HomeActivity
import com.tekmindz.covidhealthcare.application.App
import com.tekmindz.covidhealthcare.application.App.Companion.mSharedPrefrenceManager
import com.tekmindz.covidhealthcare.constants.Constants
import com.tekmindz.covidhealthcare.constants.Constants.PREF_ACCESS_TOKEN
import com.tekmindz.covidhealthcare.constants.Constants.PREF_IS_LOGIN
import com.tekmindz.covidhealthcare.repository.requestModels.LoginRequest
import com.tekmindz.covidhealthcare.repository.requestModels.UserInfoRequest
import com.tekmindz.covidhealthcare.repository.responseModel.Logout
import com.tekmindz.covidhealthcare.repository.responseModel.UserInfo
import com.tekmindz.covidhealthcare.repository.responseModel.UserInfoBody
import com.tekmindz.covidhealthcare.repository.responseModel.UserModel
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginRepository : Callback<UserModel> {
    lateinit var context: Activity
    var responseLiveData: MutableLiveData<Logout> =
        MutableLiveData<Logout>()

    /*request to login user from login api*/
    fun login(loginRequestModel: LoginRequest): Observable<Response<UserModel>> =
        App.healthCareApiLogin.login(
            RequestBody.create(MediaType.parse("multipart/form-data"), loginRequestModel.username),
            RequestBody.create(MediaType.parse("multipart/form-data"), loginRequestModel.password),
            RequestBody.create(MediaType.parse("multipart/form-data"), loginRequestModel.grant_type)
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
        refreshGrantType: String,
        mContext: Activity

    ) {
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
        Log.e("request", "${response.raw().request()}")
        Log.e(
            "response login ",
            "${response.isSuccessful} , ${response.code()}, ${response.errorBody()} , ${Gson().toJson(
                response.body()
            )}"
        )
        if (response.isSuccessful && response.code() == 200 && response.body() != null) {
            saveUserDate(Constants.PREF_ACCESS_TOKEN, response.body()?.access_token!!)
            saveUserDate(Constants.PREF_EXPIRES_IN, response.body()?.expires_in.toString())
            saveUserDate(
                Constants.PREF_REFRESH_EXPIRES_IN,
                response.body()?.refresh_expires_in.toString()
            )
        } else if (response.code() == 401) {
            val homeActivity: HomeActivity = context as HomeActivity
            Constants.refreshTokenCall = false
            homeActivity.refreshTokenExpire(context)
            responseLiveData.postValue(Logout(true))
        }

    }

    fun saveUserInfo(key: String, value: UserInfoBody) {
        App.mSharedPrefrenceManager.put(value, key)
    }

}
