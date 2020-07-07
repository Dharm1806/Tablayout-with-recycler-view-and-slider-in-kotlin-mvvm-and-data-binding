package com.tekmindz.covidhealthcare.repository.api


import com.tekmindz.covidhealthcare.constants.Constants.GET_DASHBOARD_COUNTS
import com.tekmindz.covidhealthcare.constants.Constants.GET_DASHBOARD_OBSERVATIONS
import com.tekmindz.covidhealthcare.constants.Constants.GET_PATIENT_ANALYTICS
import com.tekmindz.covidhealthcare.constants.Constants.GET_PATIENT_DETAILS
import com.tekmindz.covidhealthcare.constants.Constants.GET_PATIENT_OBSERVATIONS
import com.tekmindz.covidhealthcare.constants.Constants.LOGIN_END_POINTS
import com.tekmindz.covidhealthcare.constants.Constants.UPDATE_PATIENT_DETAILS
import com.tekmindz.covidhealthcare.repository.requestModels.*
import com.tekmindz.covidhealthcare.repository.responseModel.*
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
        @Body dateFilter: DateFilter,
        @Header("Authorization") access_token: String
    ): Observable<Response<DashboardCounts>>

    /*  call the patient details from  patient observations api api*/


    @GET(GET_PATIENT_DETAILS)
    fun getPatientDetails(
        @Path("patientId")  patientId:String,
        @Header("Authorization") access_token: String
    ): Observable<Response<PatientDetails>>


    @GET(GET_PATIENT_OBSERVATIONS)
    fun getPatientObservations(
        @Path("patientId")  patientId:String,
        @Header("Authorization") access_token: String
    ): Observable<Response<PatientObservations>>

    @POST(GET_DASHBOARD_OBSERVATIONS)
    fun getSearchResults(
        @Body searchRequestModel: SearchRequestModel,
        @Header("Authorization") access_token: String
    ): Observable<List<DashboardObservationsResponse>>

    @POST(GET_PATIENT_ANALYTICS)
    fun getPatientAnalytics(
        @Body patientAnalyticsRequest: PatientAnalyticsRequest,
        @Header("Authorization") access_token: String
    ): Observable<List<AnalyticsResponse>>

    /* update the patient details from update api*/

    @POST(UPDATE_PATIENT_DETAILS)
    fun updatePatientDetails(
        @Body updatePatientReadings: UpdatePatientReadings,
        @Header("Authorization") access_token: String
    ): Observable<Response<PatientDetails>>

}

