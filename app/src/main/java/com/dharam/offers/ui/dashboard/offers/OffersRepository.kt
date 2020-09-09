package com.dharam.offers.ui.dashboard.offers

import com.dharam.offers.application.App
import com.dharam.offers.repository.responseModel.OffersResponse

import io.reactivex.Observable
import retrofit2.Response


class OffersRepository {

    /*request to get offers details form api*/

    fun getOffersApiResponse(): Observable<Response<OffersResponse>> =
        App.offersApi.getOffers()


}
