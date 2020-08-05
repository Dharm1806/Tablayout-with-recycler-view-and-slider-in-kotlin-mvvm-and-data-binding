package com.tekmindz.covidhealthcare.repository.api


import com.tekmindz.covidhealthcare.constants.Constants.GET_DASHBOARD_COUNTS
import com.tekmindz.covidhealthcare.constants.Constants.GET_DASHBOARD_OBSERVATIONS
import com.tekmindz.covidhealthcare.constants.Constants.GET_EMERGENCY_CONTACT_NUMBER
import com.tekmindz.covidhealthcare.constants.Constants.GET_NOTIFICATIONS
import com.tekmindz.covidhealthcare.constants.Constants.GET_PATIENT_ANALYTICS
import com.tekmindz.covidhealthcare.constants.Constants.GET_PATIENT_DETAILS
import com.tekmindz.covidhealthcare.constants.Constants.GET_PATIENT_OBSERVATIONS
import com.tekmindz.covidhealthcare.constants.Constants.GET_USER_INFO
import com.tekmindz.covidhealthcare.constants.Constants.LOGIN_END_POINTS
import com.tekmindz.covidhealthcare.constants.Constants.REFRESH_TOKEN_END_POINTS
import com.tekmindz.covidhealthcare.constants.Constants.UPDATE_CONTACT_NUMBER
import com.tekmindz.covidhealthcare.constants.Constants.UPDATE_MANUAL_OBSERVSTION
import com.tekmindz.covidhealthcare.repository.requestModels.*
import com.tekmindz.covidhealthcare.repository.responseModel.*
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import retrofit2.http.Body

interface HealthCareApis {

    /*  call the login api*/
    @FormUrlEncoded
    @POST(LOGIN_END_POINTS)
    fun login(
        @Field("client_id") client_id: String,
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("grant_type") grant_type: String,
        @Field("client_secret") client_secret: String

    ): Observable<Response<UserModel>>

    /*  call the login api*/
    @FormUrlEncoded
    @POST(REFRESH_TOKEN_END_POINTS)
    fun refreshToken(
        @Field("client_id") client_id: String,
        @Field("refresh_token") username: String,
        @Field("grant_type") grant_type: String

    ): Call<UserModel>

    @POST(GET_USER_INFO)
    fun getUserInfo(
        @Header("Authorization") access_token: String,
        @Body userInfoRequest: UserInfoRequest
    ): Observable<Response<UserInfo>>

    /*  call the patient contact numbers and emergency contact number */

    @POST(UPDATE_CONTACT_NUMBER)
    fun updateProfile(
        @Body editProfileRequest: EditProfileRequest,
        @Header("Authorization") access_token: String
    ): Observable<Response<EditProfileResponse>>

    /*  call the patient list dashboard observations api*/

    @POST(GET_DASHBOARD_OBSERVATIONS)
    fun getDashboardPatientList(
        @Body dashBoardObservations: DashBoardObservations,
        @Header("Authorization") access_token: String
    ): Observable<Response<DashboardObservationsResponse>>


    /*  call the patient count dashboard observations api*/

    @POST(GET_DASHBOARD_COUNTS)
    fun getDashboardsCounts(
        @Body dateFilter: DateFilter,
        @Header("Authorization") access_token: String
    ): Observable<Response<DashboardCounts>>

    /*  call the patient details from  patient observations api api*/


    @GET(GET_PATIENT_DETAILS)
    fun getPatientDetails(
        @Path("patientId") patientId: String,
        @Header("Authorization") access_token: String
    ): Observable<Response<PatientDetails>>


    @GET(GET_PATIENT_OBSERVATIONS)
    fun getPatientObservations(
        @Path("patientId") patientId: String,
        @Header("Authorization") access_token: String
    ): Observable<Response<PatientObservations>>

    @POST(GET_DASHBOARD_OBSERVATIONS)
    fun getSearchResults(
        @Body searchRequestModel: SearchRequestModel,
        @Header("Authorization") access_token: String
    ): Observable<Response<DashboardObservationsResponse>>

    @POST(GET_PATIENT_ANALYTICS)
    fun getPatientAnalytics(
        @Body patientAnalyticsRequest: PatientAnalyticsRequest,
        @Header("Authorization") access_token: String
    ): Observable<Response<AnalyticsResponse>>

    /* update the patient details from update api*/

    @POST(UPDATE_MANUAL_OBSERVSTION)
    fun updatePainLevel(
        @Body updateManualObservations: UpdateManualObservations,
        @Header("Authorization") access_token: String
    ): Observable<Response<EditProfileResponse>>

    /* get the notifications from getNotifications api*/

    @GET(GET_NOTIFICATIONS)
    fun getNotifications(
        @Header("Authorization") access_token: String
    ): Observable<Response<NotificationResponse>>

    @GET(GET_EMERGENCY_CONTACT_NUMBER)
    fun getEmergencyContact(
        @Path("patientId") patientId: String,
        @Header("Authorization") access_token: String
    ): Observable<Response<EmergencyContact>>


}

