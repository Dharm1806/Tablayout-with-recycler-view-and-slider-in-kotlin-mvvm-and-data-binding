package com.tekmindz.covidhealthcare.repository.api


import com.tekmindz.covidhealthcare.constants.Constants.LOGIN_END_POINTS
import com.tekmindz.covidhealthcare.repository.responseModel.UserModel
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface HealthCareApis {

    /*  call the login api*/
    @FormUrlEncoded
    @POST(LOGIN_END_POINTS)
    fun login(
        @Field("client_id") client_id: String,
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("grant_type") grant_type: String

    ): Observable<Response<UserModel>>

}

