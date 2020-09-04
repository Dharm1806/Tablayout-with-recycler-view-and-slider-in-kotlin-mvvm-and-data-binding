package com.tekmindz.covidhealthcare.ui.search


import android.app.Activity
import android.app.Application
import com.tekmindz.covidhealthcare.application.App
import com.tekmindz.covidhealthcare.application.App.Companion.mSharedPrefrenceManager
import com.tekmindz.covidhealthcare.constants.Constants
import com.tekmindz.covidhealthcare.repository.requestModels.SearchRequestModel
import com.tekmindz.covidhealthcare.repository.responseModel.DashboardObservationsResponse
import com.tekmindz.covidhealthcare.utills.Presenter
import io.reactivex.Observable
import retrofit2.Response


class SearchRepository {


    /*request to search patient results from api */
    fun getSearchResults(searchRequestModel: SearchRequestModel): Observable<Response<DashboardObservationsResponse>> {
        return App.healthCareApi.getSearchResults(
            searchRequestModel,
            "bearer " + mSharedPrefrenceManager.getValueString(Constants.PREF_ACCESS_TOKEN)!!
        )
    }

    fun refreshToken(context: Activity) {
        val presenter = Presenter(Application())
        presenter.refreshToken(context)
    }

}
