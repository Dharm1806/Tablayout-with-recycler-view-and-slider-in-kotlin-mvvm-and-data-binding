package com.dharam.offers.ui.dashboard.details

import com.dharam.offers.constants.Constants
import com.dharam.offers.repository.responseModel.OffersResponse


class DetailsRepository {

    /*request to get the details form api*/

    fun getDetailsResponse(): OffersResponse? =
        Constants.mOffersResponse


}
