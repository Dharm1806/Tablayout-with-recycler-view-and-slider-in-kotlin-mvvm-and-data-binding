package com.tekmindz.covidhealthcare.ui.patientAnalytics

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Handler
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.tekmindz.covidhealthcare.R
import com.tekmindz.covidhealthcare.application.App
import com.tekmindz.covidhealthcare.constants.Constants
import com.tekmindz.covidhealthcare.constants.Constants.PREF_USER_TYPE
import com.tekmindz.covidhealthcare.constants.UserTypes
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

    fun getPatientAnalytics(patientAnalyticsRequest: PatientAnalyticsRequest, context: Activity) {
        subscribe(mAnalyticsRepository.getPatientAnalytics(patientAnalyticsRequest)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                response.value = Resource.loading()
            }
            .subscribe({
                Log.e("request", "${it.raw().request()}")

                Log.e(
                    "response",
                    "${it.body()}, ${it.code()}, ${it.message()} , ${it.errorBody()}"
                )

                if (it.code() == 401 && Constants.refreshTokenCall == true) {
                    refreshToken(context)
                    Handler().postDelayed({
                        getPatientAnalytics(patientAnalyticsRequest, context)
                    }, Constants.DELAY_IN_API_CALL)

                } else {
                    Constants.refreshTokenCall = true
                    response.value = (Resource.success(it.body()))
                }
            }, {
                response.value = Resource.error(it.localizedMessage)
            })
        )
    }

    fun getTimeFloat(observationDateTime: String, context: Context): Long {
        //Log.e("observationDAteTime", "$observationDateTime")
        val parser = SimpleDateFormat(Constants.SERVER_GRAPH_DATE_FORMAT)
        val formatter = SimpleDateFormat(context.getString(R.string.graph_time))
        //val output = formatter.format(parser.parse(observationDateTime))
        val formatterrr =
            SimpleDateFormat(Constants.SERVER_GRAPH_DATE_FORMAT).parse(observationDateTime)
//val date = Date(observationDateTime)
        // Log.e("datetimesdsfs", "${formatterrr.time}")
        return formatterrr.time
        // return output.replace(":", ".").toFloat()
    }

    fun formatXAxis(observationDateTime: String): Long {
        val formatterrr =
            SimpleDateFormat(Constants.SERVER_GRAPH_DATE_FORMAT).parse(observationDateTime)
//val date = Date(observationDateTime)
        //  Log.e("datetimesdsfs", "${formatterrr.time}")
        return formatterrr.time
    }

    fun getIntPosture(posture: String, context: Context): Float {
        var value = 0f
        when (posture) {
            context.getString(R.string.posture_sleeping) -> value = 0f
            context.getString(R.string.posture_sitting) -> value = 1f
            context.getString(R.string.posture_standing) -> value = 2f
            context.getString(R.string.posture_lying) -> value = 3f
            context.getString(R.string.posture_motion) -> value = 4f

        }
        return value
    }

    fun getGranuality(hours: Int): Int {
        var granuality = hours * 60 * 60 * 100
        return granuality / 6
    }

    fun refreshToken(context: Activity) {
        Log.e("refreshTOken", "yes")
        mAnalyticsRepository.refreshToken(context = context)
    }

    fun getUserType(): String {
        return App.mSharedPrefrenceManager.getValueString(PREF_USER_TYPE)
            ?: UserTypes.HEALTH_WORKER.toString()
    }

    fun isPatient(): Boolean = mAnalyticsRepository.isPatient()
    fun getXAXIS(toDate: String, fromDate: String, context: Context): ArrayList<String> {
        var min = formatXAxis(fromDate)
        val max = formatXAxis(toDate)
        val labelList: ArrayList<String> = ArrayList()
        var diff = max - min
        val mFormat: SimpleDateFormat =
            SimpleDateFormat(context.getString(R.string.graph_time))


        labelList.add(mFormat.format(min))
        diff = diff / 7
        for (x in 0..5) {
            min = min + diff
            labelList.add(mFormat.format(min + diff))
        }
        return labelList
    }

    fun getFormat(toDate: Long, fromDate: Long): String {
        val days = getDays(toDate, fromDate)
        var mFormat = Constants.DAY_ONE
        Log.e("days", "$days")
        if (days <= 1) {
            mFormat = Constants.DAY_ONE
        } else if (days <= 3) {
            mFormat = Constants.DAY_THREE
        } else if (days > 3) {
            mFormat = Constants.MORE_THAN_THREE_DAY
        }
        return mFormat
    }

    private fun getDays(toDate: Long, fromDate: Long): Int {
        var days = 1
        val diffms = toDate - fromDate
        days = (diffms / (1000 * 60 * 60 * 24)).toInt()
        return days

    }


}

