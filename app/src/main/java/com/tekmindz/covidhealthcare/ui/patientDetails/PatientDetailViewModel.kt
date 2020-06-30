package com.tekmindz.covidhealthcare.ui.patientDetails

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tekmindz.covidhealthcare.repository.responseModel.PatientDetails
import com.tekmindz.covidhealthcare.utills.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class PatientDetailViewModel : ViewModel() {
    private val subscriptions = CompositeDisposable()


    private var patientDetails: MutableLiveData<Resource<PatientDetails>> =
        MutableLiveData<Resource<PatientDetails>>()

    var mPatientDetailsRepository: PatientDetailsRepository = PatientDetailsRepository()

    override fun onCleared() {
        subscriptions.clear()
    }




    fun getPatientDetails(): MutableLiveData<Resource<PatientDetails>> {
        return patientDetails
    }

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
                Log.e("requestREsponse", "${it.raw().request()}")
                if (it.code() == 200 && it.isSuccessful) {
                    patientDetails.value = (Resource.success(it.body()))
                } else {

                    patientDetails.value = Resource.error(it.message())
                }
            }, {
                Log.e("error Counts", "${it.message} , ${it.localizedMessage} , ${it.stackTrace} ")
                patientDetails.value = Resource.error(it.localizedMessage)
            })
        )
    }

}
