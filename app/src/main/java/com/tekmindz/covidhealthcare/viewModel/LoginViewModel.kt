package com.tekmindz.covidhealthcare.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.tekmindz.covidhealthcare.repository.LoginRepository
import com.tekmindz.covidhealthcare.repository.requestModels.LoginRequest
import com.tekmindz.covidhealthcare.repository.responseModel.UserModel
import com.tekmindz.covidhealthcare.utills.Resource
import com.tekmindz.covidhealthcare.utills.Utills
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class LoginViewModel(application: Application) : AndroidViewModel(Application()) {

    private val subscriptions = CompositeDisposable()

    private var response: MutableLiveData<Resource<UserModel>> = MutableLiveData<Resource<UserModel>>()
    var mLoginRepository: LoginRepository = LoginRepository()




    fun  login(loginRequest: LoginRequest){
        subscribe(mLoginRepository.login(loginRequest)
           .subscribeOn(Schedulers.io())
           .observeOn(AndroidSchedulers.mainThread())
           .doOnSubscribe {
               response.value = Resource.loading() }
           .subscribe({
               response.value  = (Resource.success(it))
           }, {
              response.value = Resource.error(it.localizedMessage!!)
           }))
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
    fun matchPassword(password: String, confrimPassword: String): Boolean =
        Utills.matchPassword(password, confrimPassword)


    fun isValidEmail(email: String): Boolean =
       Utills.isValidEmail(email)


    fun validPassword(password: String): Boolean =
        password.trim().isNotEmpty()
}
