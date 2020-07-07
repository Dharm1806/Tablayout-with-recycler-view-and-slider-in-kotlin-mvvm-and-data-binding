package com.tekmindz.covidhealthcare.ui.updatePatientDetails

import android.app.Application
import android.util.Log
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.tekmindz.covidhealthcare.repository.requestModels.UpdatePatientReadings
import com.tekmindz.covidhealthcare.repository.responseModel.PatientDetails
import com.tekmindz.covidhealthcare.utills.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class UpdatePatientViewModel(application: Application) : AndroidViewModel(Application()) {

    private val subscriptions = CompositeDisposable()

    private var response: MutableLiveData<Resource<PatientDetails>> =
        MutableLiveData<Resource<PatientDetails>>()

    private var mUpdatePatientDetailsRepository: UpdatePatientDetailsRepository =
        UpdatePatientDetailsRepository()
    var bedNumber = MutableLiveData<String>()
    var wardNumber = MutableLiveData<String>()
    var sys = MutableLiveData<String>()
    var dia = MutableLiveData<String>()
    var patientId = MutableLiveData<String>()
    private var updatePDetailsMutableLiveData: MutableLiveData<UpdatePatientReadings>? = null

    fun getUpdatedPatientDetails(): MutableLiveData<UpdatePatientReadings>? {
        if (updatePDetailsMutableLiveData == null) {
            updatePDetailsMutableLiveData = MutableLiveData<UpdatePatientReadings>()
        }
        return updatePDetailsMutableLiveData
    }

    fun onClick(view: View?) {
        val loginUser = UpdatePatientReadings(patientId = patientId.value.toString(), bedNumber =  bedNumber.value.toString(), wardNumber = wardNumber.value.toString(),
            sys = sys.value.toString(), dia = dia.value.toString())
        updatePDetailsMutableLiveData!!.setValue(loginUser)
    }

    fun updatePatientDetails(updatePatientReadings: UpdatePatientReadings) {
        subscribe(mUpdatePatientDetailsRepository.updatePatientDetails(updatePatientReadings)
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


    override fun onCleared() {
        subscriptions.clear()
    }

    fun response(): MutableLiveData<Resource<PatientDetails>> {
        return response
    }


    private fun subscribe(disposable: Disposable): Disposable {
        subscriptions.add(disposable)
        return disposable
    }

    fun isValidWardNumber(wardNumber: String): Boolean =
        wardNumber.trim().isNotEmpty()


    fun isValidBedNumber(bedNumber: String): Boolean =
        bedNumber.trim().isNotEmpty()

    fun  isValidSys(sys:String):Boolean =sys.trim().isNotEmpty()

    fun isValidDia(dia:String):Boolean = dia.trim().isNotEmpty()

}
