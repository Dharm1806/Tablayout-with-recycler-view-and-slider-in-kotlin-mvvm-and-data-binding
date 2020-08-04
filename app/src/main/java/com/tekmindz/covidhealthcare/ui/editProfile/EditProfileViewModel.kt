package com.tekmindz.covidhealthcare.ui.editProfile

import android.app.Application
import android.util.Log
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.tekmindz.covidhealthcare.constants.Constants
import com.tekmindz.covidhealthcare.repository.requestModels.EditProfileRequest
import com.tekmindz.covidhealthcare.repository.responseModel.EditProfileResponse
import com.tekmindz.covidhealthcare.repository.responseModel.EmergencyContact
import com.tekmindz.covidhealthcare.utills.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class EditProfileViewModel(application: Application) : AndroidViewModel(Application()) {

    private val subscriptions = CompositeDisposable()

    private var response: MutableLiveData<Resource<EditProfileResponse>> =
        MutableLiveData<Resource<EditProfileResponse>>()

    private var emergencyContactResponse: MutableLiveData<Resource<EmergencyContact>> =
        MutableLiveData<Resource<EmergencyContact>>()

    private var mEditProfileRepository: EditProfileRepository =
        EditProfileRepository()
    var mobileNumber = MutableLiveData<String>()
    var emergencyContactNumber = MutableLiveData<String>()

    private var ePMutableLiveData: MutableLiveData<EditProfileRequest>? = null

    fun getProfileData(): MutableLiveData<EditProfileRequest>? {
        if (ePMutableLiveData == null) {
            ePMutableLiveData = MutableLiveData<EditProfileRequest>()
        }
        return ePMutableLiveData
    }

    fun onClick(view: View?) {
        val editProfileRequest = EditProfileRequest(
            1,
            emergencyContactNumber.value.toString()
        )
        ePMutableLiveData!!.value = editProfileRequest
    }

    fun updateContactInfo(editProfileRequest: EditProfileRequest) {

        subscribe(mEditProfileRepository.updateContactInfo(editProfileRequest)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                response.value = Resource.loading()
            }
            .subscribe({
                Log.e("request", "${it.code()} , ${it.raw().request()}")
                Log.e("response", "${it.body()}")
                if (it.code() == 200 && it.isSuccessful) {
                    response.value = (Resource.success(it.body()))

                } else {
                    response.value = Resource.error(it.message())
                }
            }, {
             //   Log.e("error", "${it.message} , ${it.localizedMessage} , ${it.stackTrace} ")
                response.value = Resource.error(it.localizedMessage)
            })
        )
    }


    override fun onCleared() {
        subscriptions.clear()
    }

    fun response(): MutableLiveData<Resource<EditProfileResponse>> = response

    fun emergencyContact(): MutableLiveData<Resource<EmergencyContact>> = emergencyContactResponse

    private fun subscribe(disposable: Disposable): Disposable {
        subscriptions.add(disposable)
        return disposable
    }

    fun isValidMobileNumber(mobileNumber: String): Boolean =
        mobileNumber.trim().isNullOrBlank()


    fun isValidEmergencyContactNumber(emergencyContactNumber: String): Boolean =
        emergencyContactNumber.trim().isNullOrBlank()

    fun saveUserData(key: String, value: String) = mEditProfileRepository.saveUserDate(key, value)

    fun setIsLogin(isLogin: Boolean) = mEditProfileRepository.setISLogin(isLogin)
    fun refreshToken() {
        mEditProfileRepository.refreshToken(Constants.CLIENT_ID, Constants.REFRESH_GRANT_TYPE)
        /*  val presenter = Presenter(Application())
          presenter.schedule(userData.expires_in)*/
    }


    fun isPatient(): Boolean = mEditProfileRepository.isPatient()

    fun getEmerncyContact(patientId: String) {

        subscribe(mEditProfileRepository.getEmeregncyContact(patientId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                emergencyContactResponse.value = Resource.loading()
            }
            .subscribe({
                Log.e("request", "${it.code()} , ${it.raw().request()}")
                Log.e("response", "${it.body()}")
                if (it.code() == 200 && it.isSuccessful) {
                    emergencyContactResponse.value = (Resource.success(it.body()))

                } else {
                    emergencyContactResponse.value = Resource.error(it.message())
                }
            }, {
                //   Log.e("error", "${it.message} , ${it.localizedMessage} , ${it.stackTrace} ")
                emergencyContactResponse.value = Resource.error(it.localizedMessage)
            })
        )
    }

}
