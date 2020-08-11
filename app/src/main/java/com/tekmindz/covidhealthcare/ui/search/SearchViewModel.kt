package com.tekmindz.covidhealthcare.ui.search

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tekmindz.covidhealthcare.repository.requestModels.SearchRequestModel
import com.tekmindz.covidhealthcare.repository.responseModel.DashboardObservationsResponse
import com.tekmindz.covidhealthcare.utills.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class SearchViewModel : ViewModel() {
    private val subscriptions = CompositeDisposable()

    private var response: MutableLiveData<Resource<DashboardObservationsResponse>> =
        MutableLiveData<Resource<DashboardObservationsResponse>>()


    var mSearchRepository: SearchRepository = SearchRepository()

    override fun onCleared() {
        subscriptions.clear()
    }

    fun response(): MutableLiveData<Resource<DashboardObservationsResponse>> {
        return response
    }


    private fun subscribe(disposable: Disposable): Disposable {
        subscriptions.add(disposable)
        return disposable
    }

    fun getSearchPatientResults(searchRequestModel: SearchRequestModel) {
        subscribe(mSearchRepository.getSearchResults(searchRequestModel)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {

                response.value = Resource.loading()
            }
            .subscribe({
                Log.e("request", "${it.code()} , ${it.raw().request()}")
                Log.e("response", "${it.body()}")
                response.value = (Resource.success(it.body()))

            }, {
                response.value = Resource.error(it.localizedMessage)
            })
        )
    }

    fun refreshToken() {
        mSearchRepository.refreshToken()
    }


}
