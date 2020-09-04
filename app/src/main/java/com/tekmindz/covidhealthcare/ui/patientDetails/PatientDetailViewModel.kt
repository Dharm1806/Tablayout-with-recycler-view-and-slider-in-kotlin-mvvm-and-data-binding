package com.tekmindz.covidhealthcare.ui.patientDetails

import android.app.Activity
import android.os.Handler
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.tekmindz.covidhealthcare.constants.Constants
import com.tekmindz.covidhealthcare.repository.requestModels.UpdateManualObservations
import com.tekmindz.covidhealthcare.repository.responseModel.EditProfileResponse
import com.tekmindz.covidhealthcare.repository.responseModel.PatientDetails
import com.tekmindz.covidhealthcare.repository.responseModel.PatientObservations
import com.tekmindz.covidhealthcare.repository.responseModel.UserInfoBody
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


    private var updatePatientObservations: MutableLiveData<Resource<EditProfileResponse>> =
        MutableLiveData<Resource<EditProfileResponse>>()

    val patientDetail: LiveData<Resource<PatientDetails>> = patientDetails

    var mPatientDetailsRepository: PatientDetailsRepository = PatientDetailsRepository()


    override fun onCleared() {
        subscriptions.clear()
    }


    fun getPatientDetails(): MutableLiveData<Resource<PatientDetails>> = patientDetails


    fun getPatientObservations(): MutableLiveData<Resource<PatientObservations>> =
        patientObserations

    fun updateObservation(): MutableLiveData<Resource<EditProfileResponse>> = updatePatientObservations

    private fun subscribe(disposable: Disposable): Disposable {
        subscriptions.add(disposable)
        return disposable
    }


    fun getPatientDetails(patientId: String, context: Activity) {
        subscribe(mPatientDetailsRepository.getPatientDetails(patientId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                patientDetails.value = Resource.loading()
            }
            .subscribe({
                Log.e("requestPatientDEtails", "${it.raw().request()}")
                Log.e(
                    "response",
                    "${it.code()}, ${it.body()?.message}, ${Gson().toJson(it.errorBody())}"
                )
                if (it.code() == 200 && it.isSuccessful) patientDetails.value =
                    (Resource.success(it.body()))
                else if (it.code() == 401 && Constants.refreshTokenCall == true) {
                    refreshToken(context = context)
                    Handler().postDelayed({
                        getPatientDetails(patientId, context)
                    }, Constants.DELAY_IN_API_CALL)

                } else patientDetails.value = Resource.error(it.message())
            }, {
                patientDetails.value = Resource.error(it.localizedMessage)
            })
        )
    }

    fun getPatientObservations(patientId: String, context: Activity) {
        subscribe(mPatientDetailsRepository.getPatientObservations(patientId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                patientObserations.value = Resource.loading()
            }
            .subscribe({
                Log.e("requestPatientObsns", "${it.isSuccessful} , ${it.raw().request()}")
                Log.e(
                    "response",
                    "${it.code()}, ${it.body()?.message}, ${Gson().toJson(it.body())} ,${Gson().toJson(
                        it.errorBody()
                    )}"
                )
                if (it.code() == 200 || it.isSuccessful) {
                    Log.e("200 yes", "yes")
                    Constants.refreshTokenCall = true
                    patientObserations.postValue(Resource.success(it.body()))
                } else if (it.code() == 401 && Constants.refreshTokenCall == true) {
                    refreshToken(context = context)
                    Handler().postDelayed({
                        getPatientObservations(patientId, context)
                    }, Constants.DELAY_IN_API_CALL)

                } else {
                    patientObserations.value = Resource.error(it.message())
                }
            }, {
                patientObserations.value = Resource.error(it.localizedMessage)
            })
        )
    }


    fun parseDate(dob: String): String? = Utills.parseDate(dob)

    fun refreshToken(context: Activity) {
        mPatientDetailsRepository.refreshToken(context)
    }

    fun updateObservationType(
        updateManualObservations: UpdateManualObservations,
        context: Activity
    ) {
        subscribe(mPatientDetailsRepository.updatePainLevel(updateManualObservations)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                updatePatientObservations.value = Resource.loading()
            }
            .subscribe({
                Log.e("requestREsponse", "${it.raw().request()}")
                Log.e(
                    "response",
                    "${it.code()}, ${it.body()?.message}, ${Gson().toJson(it.errorBody())}"
                )
                if ((it.code() == 200 || it.code() == 201)) {
                    Constants.refreshTokenCall = true
                    updatePatientObservations.value = (Resource.success(it.body()))
                } else if (it.code() == 401 && Constants.refreshTokenCall == true) {
                    refreshToken(context)
                    Handler().postDelayed({

                        updateObservationType(updateManualObservations, context)


                    }, Constants.DELAY_IN_API_CALL)

                } else {
                    updatePatientObservations.value = Resource.error(it.message())
                }
            }, {
                updatePatientObservations.value = Resource.error(it.localizedMessage)
            })
        )
    }

    fun getPatientInfo(): UserInfoBody = mPatientDetailsRepository.getPatient()
    fun isPatient(): Boolean = mPatientDetailsRepository.isPatient()
    fun isPatientAndHC(): Boolean = mPatientDetailsRepository.isPatientAndHc()
    fun isValidSpo2(s: CharSequence): Boolean =
        !(s != null && s.toString().trim().length != 0 && s.toString() != "." && s.toString().trim()
            .toFloat() > 100.0f)
}
