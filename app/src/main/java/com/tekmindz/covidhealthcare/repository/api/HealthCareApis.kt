package com.tekmindz.covidhealthcare.repository.api


import com.tekmindz.covidhealthcare.constants.Constants.GET_DASHBOARD_COUNTS
import com.tekmindz.covidhealthcare.constants.Constants.GET_DASHBOARD_OBSERVATIONS
import com.tekmindz.covidhealthcare.constants.Constants.LOGIN_END_POINTS
import com.tekmindz.covidhealthcare.repository.requestModels.DashBoardObservations
import com.tekmindz.covidhealthcare.repository.responseModel.DashboardCounts
import com.tekmindz.covidhealthcare.repository.responseModel.DashboardObservationsResponse
import com.tekmindz.covidhealthcare.repository.responseModel.UserModel
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

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

    /*  call the patient list dashboard observations api*/

    @POST(GET_DASHBOARD_OBSERVATIONS)
    fun getDashboardPatientList(
        @Body dashBoardObservations: DashBoardObservations,
        @Header("Authorization") access_token: String
    ): Observable<List<DashboardObservationsResponse>>


    /*  call the patient count dashboard observations api*/

    @POST(GET_DASHBOARD_COUNTS)
    fun getDashboardsCounts(
        @Body dashBoardObservations: DashBoardObservations,
        @Header("Authorization") access_token: String
    ): Observable<Response<DashboardCounts>>


}

