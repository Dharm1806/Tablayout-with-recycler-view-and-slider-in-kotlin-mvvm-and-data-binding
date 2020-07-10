package com.tekmindz.covidhealthcare.ui.login

import android.app.Application
import android.util.Log
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.tekmindz.covidhealthcare.constants.Constants
import com.tekmindz.covidhealthcare.repository.requestModels.LoginRequest
import com.tekmindz.covidhealthcare.repository.responseModel.UserModel
import com.tekmindz.covidhealthcare.utills.Presenter
import com.tekmindz.covidhealthcare.utills.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class LoginViewModel(application: Application) : AndroidViewModel(Application()) {

    private val subscriptions = CompositeDisposable()

    private var response: MutableLiveData<Resource<UserModel>> =
        MutableLiveData<Resource<UserModel>>()

    private var mLoginRepository: LoginRepository =
        LoginRepository()
    var UserName = MutableLiveData<String>()
    var Password = MutableLiveData<String>()

    private var userMutableLiveData: MutableLiveData<LoginRequest>? = null

    fun getUser(): MutableLiveData<LoginRequest>? {
        if (userMutableLiveData == null) {
            userMutableLiveData = MutableLiveData<LoginRequest>()
        }
        return userMutableLiveData
    }

    fun onClick(view: View?) {
        val loginUser = LoginRequest(
            UserName.value.toString(),
            Password.value.toString(),
            Constants.CLIENT_ID,
            Constants.PASSWROD,
            Constants.CLIENT_SECRET
        )
        userMutableLiveData!!.value = loginUser
    }

    fun login(loginRequest: LoginRequest) {
        subscribe(mLoginRepository.login(loginRequest)
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
             //   Log.e("error", "${it.message} , ${it.localizedMessage} , ${it.stackTrace} ")
                response.value = Resource.error(it.localizedMessage)
            })
        )
    }


    override fun onCleared() {
        subscriptions.clear()
    }

    fun response(): MutableLiveData<Resource<UserModel>> {
        return response
    }


    private fun subscribe(disposable: Disposable): Disposable {
        subscriptions.add(disposable)
        return disposable
    }

    fun isValidEmail(email: String): Boolean =
        email.trim().isNullOrBlank()


    fun validPassword(password: String): Boolean =
        password.trim().isNullOrBlank()

    fun saveUserData(key: String, value: String) = mLoginRepository.saveUserDate(key, value)

    fun setIsLogin(isLogin: Boolean) = mLoginRepository.setISLogin(isLogin)
    fun refreshToken() {
        mLoginRepository.refreshToken(Constants.CLIENT_ID, Constants.REFRESH_GRANT_TYPE)
      /*  val presenter = Presenter(Application())
        presenter.schedule(userData.expires_in)*/
    }
}
