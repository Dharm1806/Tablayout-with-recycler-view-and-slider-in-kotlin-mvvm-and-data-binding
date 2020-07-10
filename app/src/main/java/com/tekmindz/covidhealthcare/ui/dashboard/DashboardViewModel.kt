package com.tekmindz.covidhealthcare.ui.dashboard

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.tekmindz.covidhealthcare.constants.Constants
import com.tekmindz.covidhealthcare.repository.requestModels.DashBoardObservations
import com.tekmindz.covidhealthcare.repository.requestModels.DateFilter
import com.tekmindz.covidhealthcare.repository.responseModel.DashboardCounts
import com.tekmindz.covidhealthcare.repository.responseModel.DashboardObservationsResponse
import com.tekmindz.covidhealthcare.repository.responseModel.UserModel
import com.tekmindz.covidhealthcare.utills.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class DashboardViewModel(application: Application) : AndroidViewModel(Application()) {

    private val subscriptions = CompositeDisposable()

    private var response: MutableLiveData<Resource<DashboardObservationsResponse>> =
        MutableLiveData<Resource<DashboardObservationsResponse>>()
    private var refreshTokenResponse: MutableLiveData<Resource<UserModel>> =
        MutableLiveData<Resource<UserModel>>()

    private var responseCounts: MutableLiveData<Resource<DashboardCounts>> =
        MutableLiveData<Resource<DashboardCounts>>()


    var mDashboardRepository: DashboardRepository = DashboardRepository()

    override fun onCleared() {
        subscriptions.clear()
    }

    fun response(): MutableLiveData<Resource<DashboardObservationsResponse>> {
        return response
    }


    fun dashBoardCounts(): MutableLiveData<Resource<DashboardCounts>> {
        return responseCounts
    }

    fun getRefreshToken(): MutableLiveData<Resource<UserModel>> {
        return refreshTokenResponse
    }

    fun refreshToken() {
        mDashboardRepository.refreshToken()
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
                response.value = Resource.loading()
            }
            .subscribe({
               // Log.e("requestREsponse", "${it.raw().request()}")
                response.value = (Resource.success(it.body()))

            }, {
               // Log.e("error", "${it.message} , ${it.localizedMessage} , ${it.stackTrace}  ")
                response.value = Resource.error(it.localizedMessage)
            })
        )
    }


    fun getDashBoardCounts(dateFilter: DateFilter) {
        subscribe(mDashboardRepository.getDashBoardCount(dateFilter)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                responseCounts.value = Resource.loading()
            }
            .subscribe({
                /*Log.e("requestREsponse", "${it.raw().request()}")*/
                Log.e("response", "${it.code()}")
                responseCounts.value = (Resource.success(it.body()))


                //  responseCounts.value = Resource.error(it.message())

            }, {
               /* Log.e(
                    "error Counts",
                    "${it.message} , ${it.localizedMessage} , ${it.stackTrace} "
                )*/
                responseCounts.value = Resource.error(it.localizedMessage)
            })
        )
    }


    fun getIsLogin(): Boolean = mDashboardRepository.getIsLogin()
    fun saveAccessToke(data: UserModel) {
        mDashboardRepository.saveRefreshToken(data)
    }
}
