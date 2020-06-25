package com.tekmindz.covidhealthcare.ui.login

import com.tekmindz.covidhealthcare.application.App
import com.tekmindz.covidhealthcare.application.App.Companion.mSharedPrefrenceManager
import com.tekmindz.covidhealthcare.constants.Constants.PREF_IS_LOGIN
import com.tekmindz.covidhealthcare.repository.requestModels.LoginRequest
import com.tekmindz.covidhealthcare.repository.responseModel.UserModel
import io.reactivex.Observable
import retrofit2.Response


class LoginRepository {

    /*request to login user from login api*/
    fun login(loginRequestModel: LoginRequest): Observable<Response<UserModel>> =
        App.healthCareApiLogin.login(
            loginRequestModel.client_id,
            loginRequestModel.username,
            loginRequestModel.password,
            loginRequestModel.grant_type

        )

    fun saveUserDate(key: String, value: String) = mSharedPrefrenceManager.saveString(key, value)

    fun setISLogin(isLogin: Boolean) = mSharedPrefrenceManager.setIsLogin(PREF_IS_LOGIN, isLogin)

}
