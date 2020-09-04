package com.tekmindz.covidhealthcare.utills

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.tekmindz.covidhealthcare.constants.Constants.CLIENT_ID
import com.tekmindz.covidhealthcare.constants.Constants.REFRESH_GRANT_TYPE
import com.tekmindz.covidhealthcare.constants.Constants.TIME_DIFFERENCE
import com.tekmindz.covidhealthcare.repository.responseModel.UserModel
import com.tekmindz.covidhealthcare.ui.login.LoginRepository
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
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

    fun schedule(miliseconds: Long) = launch {

        // launching the coroutine
        while (true) {

            delay((miliseconds - TIME_DIFFERENCE) * 1000)

            //refreshToken()
        }
    }

    /**
     * call refresh token api
     */

    fun refreshToken(context: Activity) {
        Log.e("presenter", "refresh")
        LoginRepository().refreshToken(
            CLIENT_ID,
            REFRESH_GRANT_TYPE,
            context

        )

    }

    private fun subscribe(disposable: Disposable): Disposable {
        subscriptions.add(disposable)
        return disposable
    }

}