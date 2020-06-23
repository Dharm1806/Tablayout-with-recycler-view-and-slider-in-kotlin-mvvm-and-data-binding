package com.tekmindz.covidhealthcare.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.tekmindz.covidhealthcare.ui.dashboard.DashboardRepository
import com.tekmindz.covidhealthcare.repository.responseModel.UserModel
import com.tekmindz.covidhealthcare.utills.Resource
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


class DashboardViewModel(application: Application) : AndroidViewModel(Application()) {

    private val subscriptions = CompositeDisposable()

    private var response: MutableLiveData<Resource<UserModel>> =
        MutableLiveData<Resource<UserModel>>()
    var mDashboardRepository: DashboardRepository =
        DashboardRepository()




    override fun onCleared() {
        subscriptions.clear()
    }

    fun response(): MutableLiveData<Resource<UserModel>> {
        return response
    }


    private fun subscribe(disposable: Disposable): Disposable {
        subscriptions.add(disposable)
        return disposable
    }


    fun getIsLogin() :Boolean= mDashboardRepository.getIsLogin()
}
