package com.dharam.offers.ui.dashboard.details

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.dharam.offers.repository.responseModel.OffersResponse
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


class OfferDetailsViewModel(application: Application) : AndroidViewModel(Application()) {

    private val subscriptions = CompositeDisposable()
    var mDetailsRepository: DetailsRepository = DetailsRepository()

    private var detailResponse: MutableLiveData<OffersResponse> =
        MutableLiveData<OffersResponse>()


    override fun onCleared() {
        subscriptions.clear()
    }


    fun getDetailsResponse(): MutableLiveData<OffersResponse> {
        return detailResponse
    }


    private fun subscribe(disposable: Disposable): Disposable {
        subscriptions.add(disposable)
        return disposable
    }


    fun getDetails() {
        detailResponse.postValue(mDetailsRepository.getDetailsResponse())


    }

}
