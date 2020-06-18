package com.tekmindz.covidhealthcare.repository

import com.tekmindz.covidhealthcare.application.App
import com.tekmindz.covidhealthcare.repository.requestModels.LoginRequest
import com.tekmindz.covidhealthcare.repository.responseModel.UserModel
import io.reactivex.Observable


class LoginRepository {

   /*request to login user from login api*/
    fun login(loginRequestModel: LoginRequest): Observable<List<UserModel>> = App.healthCareApi.login(loginRequestModel)

}
