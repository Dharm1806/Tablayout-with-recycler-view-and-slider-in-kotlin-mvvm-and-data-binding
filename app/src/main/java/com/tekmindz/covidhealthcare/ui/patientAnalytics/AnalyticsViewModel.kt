package com.tekmindz.covidhealthcare.ui.patientAnalytics

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.tekmindz.covidhealthcare.R
import com.tekmindz.covidhealthcare.constants.Constants
import com.tekmindz.covidhealthcare.repository.requestModels.PatientAnalyticsRequest
import com.tekmindz.covidhealthcare.repository.responseModel.AnalyticsResponse
import com.tekmindz.covidhealthcare.utills.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat


class AnalyticsViewModel(application: Application) : AndroidViewModel(Application()) {

    private val subscriptions = CompositeDisposable()

    private var response: MutableLiveData<Resource<AnalyticsResponse>> =
        MutableLiveData<Resource<AnalyticsResponse>>()


    var mAnalyticsRepository: AnalyticsRepository = AnalyticsRepository()

    override fun onCleared() {
        subscriptions.clear()
    }

    fun response(): MutableLiveData<Resource<AnalyticsResponse>> {
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
                response.value = Resource.loading()
            }
            .subscribe({
                response.value = (Resource.success(it.body()))

            }, {
                Log.e("error", "${it.message} , ${it.localizedMessage} , ${it.stackTrace}  ")
                response.value = Resource.error(it.localizedMessage)
            })
        )
    }

    fun getTimeFloat(observationDateTime: String, context: Context): Float {
        val parser = SimpleDateFormat(Constants.SERVER_GRAPH_DATE_FORMAT)
        val formatter = SimpleDateFormat(context.getString(R.string.graph_time))
        val output = formatter.format(parser.parse(observationDateTime))
        return output.replace(":", ".").toFloat()
    }

    fun getIntPosture(posture: String, context: Context): Float {
        var value = 0f
        when (posture) {
            context.getString(R.string.posture_sleeping) -> value = 1f
            context.getString(R.string.posture_sitting) -> value = 2f
            context.getString(R.string.posture_standing) -> value = 3f
            context.getString(R.string.posture_lying) -> value = 4f
        }
        return value
    }


}

