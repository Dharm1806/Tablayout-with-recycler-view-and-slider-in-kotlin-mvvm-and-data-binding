package com.tekmindz.covidhealthcare.ui.UpdatePainLevel

import android.app.Activity
import android.app.Application
import android.os.Handler
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.tekmindz.covidhealthcare.application.App
import com.tekmindz.covidhealthcare.constants.Constants
import com.tekmindz.covidhealthcare.constants.UserTypes
import com.tekmindz.covidhealthcare.repository.requestModels.UpdateManualObservations
import com.tekmindz.covidhealthcare.repository.requestModels.UpdatePainLevel
import com.tekmindz.covidhealthcare.repository.responseModel.EditProfileResponse
import com.tekmindz.covidhealthcare.utills.Presenter
import com.tekmindz.covidhealthcare.utills.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class UpdatePainLevelViewModel(application: Application) : AndroidViewModel(Application()) {

    private val subscriptions = CompositeDisposable()

    private var updatePatientObservations: MutableLiveData<Resource<EditProfileResponse>> =
        MutableLiveData<Resource<EditProfileResponse>>()

    private var mUpdatePatientDetailsRepository: UpdatePainLevelRepository =
        UpdatePainLevelRepository()


    fun updateObservationType(mUpdatePainLevel: UpdatePainLevel, context: Activity) {
        Log.e("update", "${Gson().toJson(mUpdatePainLevel)}")
        val updateManualObservationsList = listOf<UpdatePainLevel>(mUpdatePainLevel)

        subscribe(mUpdatePatientDetailsRepository.updatePainLevel(
            UpdateManualObservations(
                updateManualObservationsList
            )
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                updatePatientObservations.value = Resource.loading()
            }
            .subscribe({
                Log.e("request", "${it.code()} , ${it.raw().request()}")
                Log.e("response", "${it.body()}")
                if ((it.code() == 200 || it.code() == 201)) {
                    Constants.refreshTokenCall = true
                    updatePatientObservations.value = (Resource.success(it.body()))
                } else if (it.code() == 401 && Constants.refreshTokenCall == true) {
                    refreshToken(context)
                    Handler().postDelayed({
                        updateObservationType(mUpdatePainLevel, context)
                    }, Constants.DELAY_IN_API_CALL)

                } else {
                    updatePatientObservations.value = Resource.error(it.message())
                }
            }, {
                Log.e("error", "${it.message} , ${it.localizedMessage} , ${it.stackTrace} ")

                updatePatientObservations.value = Resource.error(it.localizedMessage)
            })
        )
    }

    private fun refreshToken(context: Activity) {
        val presenter = Presenter(Application())
        presenter.refreshToken(context = context)
    }


    override fun onCleared() {
        subscriptions.clear()
    }

    fun response(): MutableLiveData<Resource<EditProfileResponse>> {
        return updatePatientObservations
    }


    private fun subscribe(disposable: Disposable): Disposable {
        subscriptions.add(disposable)
        return disposable
    }


    fun getUserType(): String {
        return App.mSharedPrefrenceManager.getValueString(Constants.PREF_USER_TYPE)?: UserTypes.HEALTH_WORKER.toString()
    }


}
