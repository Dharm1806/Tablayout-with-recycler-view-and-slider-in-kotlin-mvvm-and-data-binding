package com.tekmindz.covidhealthcare.repository.api


import com.tekmindz.covidhealthcare.constants.Constants.LOGIN_END_POINTS
import com.tekmindz.covidhealthcare.repository.requestModels.LoginRequest
import com.tekmindz.covidhealthcare.repository.responseModel.UserModel
import io.reactivex.Observable
import retrofit2.http.GET

interface HealthCareApis {

    /*  call the login api*/
    @GET(LOGIN_END_POINTS)
    fun login(loginRequest: LoginRequest): Observable<List<UserModel>>

}
