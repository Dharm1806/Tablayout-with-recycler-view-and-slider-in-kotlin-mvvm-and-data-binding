package com.dharam.offers.ui.dashboard.offers

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.dharam.offers.repository.responseModel.OffersResponse
import com.dharam.offers.utills.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class OffersViewModel(application: Application) : AndroidViewModel(Application()) {

    private val subscriptions = CompositeDisposable()

    private var offersResponse: MutableLiveData<Resource<OffersResponse>> =
        MutableLiveData<Resource<OffersResponse>>()

    var mOffersRepository: OffersRepository = OffersRepository()

    override fun onCleared() {
        subscriptions.clear()
    }


    fun getOffersResponse(): MutableLiveData<Resource<OffersResponse>> {
        return offersResponse
    }


    private fun subscribe(disposable: Disposable): Disposable {
        subscriptions.add(disposable)
        return disposable
    }


    fun getOffersCount() {
        subscribe(mOffersRepository.getOffersApiResponse()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                offersResponse.value = Resource.loading()
            }
            .subscribe({
                Log.e("requestREsponse", "${it.raw().request()}")
                Log.e("response", "${it.code()}")
                if (it.code() == 200 ){
                    offersResponse.postValue(Resource.success(it.body()))
                }else{
                    offersResponse.postValue(Resource.error(it.message()))
                }

                //  responseCounts.value = Resource.error(it.message())

            }, {

                offersResponse.postValue(Resource.error(it.localizedMessage))
            })
        )
    }

}
