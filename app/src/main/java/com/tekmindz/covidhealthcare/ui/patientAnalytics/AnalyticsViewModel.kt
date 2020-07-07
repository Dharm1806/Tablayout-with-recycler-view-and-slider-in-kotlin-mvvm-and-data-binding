package com.tekmindz.covidhealthcare.ui.patientAnalytics

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.tekmindz.covidhealthcare.repository.requestModels.PatientAnalyticsRequest
import com.tekmindz.covidhealthcare.repository.responseModel.AnalyticsResponse
import com.tekmindz.covidhealthcare.utills.ResponseList
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class AnalyticsViewModel(application: Application) : AndroidViewModel(Application()) {

    private val subscriptions = CompositeDisposable()

    private var response: MutableLiveData<ResponseList<AnalyticsResponse>> =
        MutableLiveData<ResponseList<AnalyticsResponse>>()


    var mAnalyticsRepository: AnalyticsRepository = AnalyticsRepository()

    override fun onCleared() {
        subscriptions.clear()
    }

    fun response(): MutableLiveData<ResponseList<AnalyticsResponse>> {
        return response
    }

    private fun subscribe(disposable: Disposable): Disposable {
        subscriptions.add(disposable)
        return disposable
    }

    fun getPatientAnalytics(patientAnalyticsRequest: PatientAnalyticsRequest) {
        subscribe(mAnalyticsRepository.getPatientAnalytics(patientAnalyticsRequest)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                response.value = ResponseList.loading()
            }
            .subscribe({
                    Log.e("size", "${it.size}")
                    response.value = (ResponseList.success(it))

            }, {
                Log.e("error", "${it.message} , ${it.localizedMessage} , ${it.stackTrace}  ")
                response.value = ResponseList.error(it.localizedMessage)
            })
        )
    }
}
