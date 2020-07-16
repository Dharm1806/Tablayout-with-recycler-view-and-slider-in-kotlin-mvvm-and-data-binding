package com.tekmindz.covidhealthcare.ui.notifications

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

    fun getNotifications() {
        subscribe(mNotificationRepository.getNotifications()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                response.value = Resource.loading()
            }
            .subscribe({
                response.value = (Resource.success(it.body()))

            }, {
                response.value = Resource.error(it.localizedMessage)
            })
        )
    }

    fun refreshToken() {
        mNotificationRepository.refreshToken()
    }


}
