package com.tekmindz.covidhealthcare.ui.UpdatePainLevel

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.tekmindz.covidhealthcare.application.App
import com.tekmindz.covidhealthcare.constants.Constants
import com.tekmindz.covidhealthcare.constants.UserTypes
import com.tekmindz.covidhealthcare.repository.requestModels.UpdatePainLevel
import com.tekmindz.covidhealthcare.repository.responseModel.PatientDetails
import com.tekmindz.covidhealthcare.utills.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class UpdatePainLevelViewModel(application: Application) : AndroidViewModel(Application()) {

    private val subscriptions = CompositeDisposable()

    private var response: MutableLiveData<Resource<PatientDetails>> =
        MutableLiveData<Resource<PatientDetails>>()

    private var mUpdatePatientDetailsRepository: UpdatePainLevelRepository =
        UpdatePainLevelRepository()
    var bedNumber = MutableLiveData<String>()
    var wardNumber = MutableLiveData<String>()
    var sys = MutableLiveData<String>()
    var dia = MutableLiveData<String>()
    var patientId = MutableLiveData<String>()
    private var updatePDetailsMutableLiveData: MutableLiveData<UpdatePainLevel>? = null

    fun getUpdatedPatientDetails(): MutableLiveData<UpdatePainLevel>? {
        if (updatePDetailsMutableLiveData == null) {
            updatePDetailsMutableLiveData = MutableLiveData<UpdatePainLevel>()
        }
        return updatePDetailsMutableLiveData
    }

    fun onClick(view: View?) {
        val loginUser = UpdatePainLevel(
            patientId = patientId.value.toString(),
            bedNumber = bedNumber.value.toString(),
            wardNumber = wardNumber.value.toString(),
            sys = sys.value.toString(),
            dia = dia.value.toString()
        )
        updatePDetailsMutableLiveData!!.value = loginUser
    }

    fun updatePainLevel(updatePainLevel: UpdatePainLevel) {
        subscribe(mUpdatePatientDetailsRepository.updatePainLevel(updatePainLevel)
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

    fun isValidSys(sys: String): Boolean = sys.trim().isNotEmpty()

    fun isValidDia(dia: String): Boolean = dia.trim().isNotEmpty()

    fun getUserType(): String {
        return App.mSharedPrefrenceManager.getValueString(Constants.PREF_USER_TYPE)?: UserTypes.HEALTH_WORKER.toString()
    }


}
