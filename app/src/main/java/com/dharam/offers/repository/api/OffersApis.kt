package com.dharam.offers.repository.api

import com.dharam.offers.constants.Constants.GET_OFFERS_ENDPOINTS
import com.dharam.offers.repository.responseModel.*
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

interface OffersApis {
    @GET(GET_OFFERS_ENDPOINTS)
    fun getOffers(): Observable<Response<OffersResponse>>

}

