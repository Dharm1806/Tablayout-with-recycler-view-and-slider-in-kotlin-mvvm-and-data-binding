package com.tekmindz.covidhealthcare.ui.notifications

import android.app.Activity
import android.os.Handler
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tekmindz.covidhealthcare.application.App
import com.tekmindz.covidhealthcare.constants.Constants
import com.tekmindz.covidhealthcare.constants.UserTypes
import com.tekmindz.covidhealthcare.repository.responseModel.NotificationResponse
import com.tekmindz.covidhealthcare.utills.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class NotificationViewModel : ViewModel() {
    private val subscriptions = CompositeDisposable()

    private var response: MutableLiveData<Resource<NotificationResponse>> =
        MutableLiveData<Resource<NotificationResponse>>()


    var mNotificationRepository: NotificationRepository = NotificationRepository()

    override fun onCleared() {
        subscriptions.clear()
    }

    fun response(): MutableLiveData<Resource<NotificationResponse>> {
        return response
    }


    private fun subscribe(disposable: Disposable): Disposable {
        subscriptions.add(disposable)
        return disposable
    }

    fun getNotifications(context: Activity) {
        subscribe(mNotificationRepository.getNotifications()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                response.value = Resource.loading()
            }
            .subscribe({
                if (it.code() == 401) {
                    refreshToken(context)
                    Handler().postDelayed({
                        getNotifications(context)
                    }, Constants.DELAY_IN_API_CALL)

                } else {
                    response.value = (Resource.success(it.body()))
                }
            }, {
                response.value = Resource.error(it.localizedMessage)
            })
        )
    }

    fun refreshToken(context: Activity) {
        mNotificationRepository.refreshToken(context = context)
    }


    fun getUserType(): String {
        return App.mSharedPrefrenceManager.getValueString(Constants.PREF_USER_TYPE)
            ?: UserTypes.HEALTH_WORKER.toString()
    }


    fun isPatient(): Boolean = mNotificationRepository.isPatient()

}
