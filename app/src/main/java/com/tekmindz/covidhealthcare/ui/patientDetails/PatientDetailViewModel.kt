package com.tekmindz.covidhealthcare.ui.patientDetails

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tekmindz.covidhealthcare.application.App
import com.tekmindz.covidhealthcare.constants.Constants
import com.tekmindz.covidhealthcare.constants.UserTypes
import com.tekmindz.covidhealthcare.repository.responseModel.PatientDetails
import com.tekmindz.covidhealthcare.repository.responseModel.PatientObservations
import com.tekmindz.covidhealthcare.utills.Resource
import com.tekmindz.covidhealthcare.utills.Utills
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class PatientDetailViewModel : ViewModel() {
    private val subscriptions = CompositeDisposable()


    private var patientDetails: MutableLiveData<Resource<PatientDetails>> =
        MutableLiveData<Resource<PatientDetails>>()

    private var patientObserations: MutableLiveData<Resource<PatientObservations>> =
        MutableLiveData<Resource<PatientObservations>>()

    val patientDetail: LiveData<Resource<PatientDetails>> = patientDetails

    var mPatientDetailsRepository: PatientDetailsRepository = PatientDetailsRepository()


    override fun onCleared() {
        subscriptions.clear()
    }


    fun getPatientDetails(): MutableLiveData<Resource<PatientDetails>> = patientDetails


    fun getPatientObservations(): MutableLiveData<Resource<PatientObservations>> =
        patientObserations

    private fun subscribe(disposable: Disposable): Disposable {
        subscriptions.add(disposable)
        return disposable
    }


    fun getPatientDetails(patientId: String) {
        subscribe(mPatientDetailsRepository.getPatientDetails(patientId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                patientDetails.value = Resource.loading()
            }
            .subscribe({
                if (it.code() == 200 && it.isSuccessful) patientDetails.value =
                    (Resource.success(it.body()))
                else patientDetails.value = Resource.error(it.message())
            }, {
                patientDetails.value = Resource.error(it.localizedMessage)
            })
        )
    }

    fun getPatientObservations(patientId: String) {
        subscribe(mPatientDetailsRepository.getPatientObservations(patientId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                patientDetails.value = Resource.loading()
            }
            .subscribe({

                if (it.code() == 200 && it.isSuccessful) {
                    patientObserations.value = (Resource.success(it.body()))
                } else {
                    patientObserations.value = Resource.error(it.message())
                }
            }, {
                patientObserations.value = Resource.error(it.localizedMessage)
            })
        )
    }


    fun parseDate(dob: String): String? = Utills.parseDate(dob)

    fun refreshToken() {
        mPatientDetailsRepository.refreshToken()
    }

    fun getUserType(): String {
        return App.mSharedPrefrenceManager.getValueString(Constants.PREF_USER_TYPE)?: UserTypes.HEALTH_WORKER.toString()
    }
}
