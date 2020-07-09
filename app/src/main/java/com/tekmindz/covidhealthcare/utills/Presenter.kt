package com.tekmindz.covidhealthcare.utills

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.tekmindz.covidhealthcare.application.App
import com.tekmindz.covidhealthcare.constants.Constants.CLIENT_ID
import com.tekmindz.covidhealthcare.constants.Constants.PREF_REFRESH_TOKEN
import com.tekmindz.covidhealthcare.constants.Constants.REFRESH_GRANT_TYPE
import com.tekmindz.covidhealthcare.constants.Constants.TIME_DIFFERENCE
import com.tekmindz.covidhealthcare.repository.responseModel.UserModel
import com.tekmindz.covidhealthcare.ui.login.LoginRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


class Presenter(mApplication: Application) :

    CoroutineScope { // implement CoroutineScope to create local scope
    private val subscriptions = CompositeDisposable()

    private var response: MutableLiveData<Resource<UserModel>> =
        MutableLiveData<Resource<UserModel>>()

    var mApplication: Application? = null

    private var job: Job = Job()

    init {

        this.mApplication = mApplication

    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job

    // this method will help to stop execution of a coroutine. 
    // Call it to cancel coroutine and to break the while loop defined in the coroutine below    
    fun cancel() {

        job.cancel()
    }

    fun schedule(miliseconds: Long, mContext: Activity) = launch {

        // launching the coroutine
        while (true) {

            delay((miliseconds - TIME_DIFFERENCE) * 1000)

            refreshToken()
        }
    }

    /**
     * call refresh token api
     */

    fun refreshToken() {
        val loginRepository = LoginRepository()

        val refreshtoken = loginRepository.refreshToken(
            CLIENT_ID,
            REFRESH_GRANT_TYPE,
            App.mSharedPrefrenceManager.getValueString(PREF_REFRESH_TOKEN)
        )

        subscribe(refreshtoken
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                response.value = Resource.loading()
            }
            .subscribe({
                if (it.code() == 200 && it.isSuccessful) {
                    response.value = (Resource.success(it.body()))
                } else {
                    response.value = Resource.error(it.message())
                }
            }, {

                Log.e("error", "${it.message} , ${it.localizedMessage} , ${it.stackTrace} ")
                response.value = Resource.error(it.localizedMessage)

            })
        )


    }

    private fun subscribe(disposable: Disposable): Disposable {
        subscriptions.add(disposable)
        return disposable
    }

}