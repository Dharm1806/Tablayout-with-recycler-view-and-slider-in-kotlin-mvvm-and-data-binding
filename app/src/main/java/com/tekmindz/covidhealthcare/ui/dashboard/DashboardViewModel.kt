package com.tekmindz.covidhealthcare.ui.dashboard

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.tekmindz.covidhealthcare.repository.requestModels.DashBoardObservations
import com.tekmindz.covidhealthcare.repository.responseModel.DashboardCounts
import com.tekmindz.covidhealthcare.repository.responseModel.DashboardObservationsResponse
import com.tekmindz.covidhealthcare.utills.Resource
import com.tekmindz.covidhealthcare.utills.ResponseList
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class DashboardViewModel(application: Application) : AndroidViewModel(Application()) {

    private val subscriptions = CompositeDisposable()

    private var response: MutableLiveData<ResponseList<DashboardObservationsResponse>> =
        MutableLiveData<ResponseList<DashboardObservationsResponse>>()

    private var responseCounts: MutableLiveData<Resource<DashboardCounts>> =
        MutableLiveData<Resource<DashboardCounts>>()

    var mDashboardRepository: DashboardRepository = DashboardRepository()

    override fun onCleared() {
        subscriptions.clear()
    }

    fun response(): MutableLiveData<ResponseList<DashboardObservationsResponse>> {
        return response
    }


    fun dashBoardCounts():MutableLiveData<Resource<DashboardCounts>>{
        return responseCounts
    }

    private fun subscribe(disposable: Disposable): Disposable {
        subscriptions.add(disposable)
        return disposable
    }

    fun getDashboardObservations(dashBoardObservations: DashBoardObservations) {
        subscribe(mDashboardRepository.getDashboardObservations(dashBoardObservations)
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



    fun getDashBoardCounts(dashBoardObservations: DashBoardObservations) {
        subscribe(mDashboardRepository.getDashBoardCount(dashBoardObservations)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                responseCounts.value = Resource.loading()
            }
            .subscribe({
                Log.e("requestREsponse", "${it.raw().request()}")
                if (it.code() == 200 && it.isSuccessful) {
                    responseCounts.value = (Resource.success(it.body()))
                } else {

                    responseCounts.value = Resource.error(it.message())
                }
            }, {
                Log.e("error Counts", "${it.message} , ${it.localizedMessage} , ${it.stackTrace} ")
                responseCounts.value = Resource.error(it.localizedMessage)
            })
        )
    }

    fun getIsLogin() :Boolean= mDashboardRepository.getIsLogin()
}
