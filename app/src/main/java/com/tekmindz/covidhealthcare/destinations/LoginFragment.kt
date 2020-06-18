package com.tekmindz.covidhealthcare.destinations

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.tekmindz.covidhealthcare.R
import com.tekmindz.covidhealthcare.repository.requestModels.LoginRequest
import com.tekmindz.covidhealthcare.repository.responseModel.UserModel
import com.tekmindz.covidhealthcare.utills.Resource
import com.tekmindz.covidhealthcare.utills.Utills
import com.tekmindz.covidhealthcare.viewModel.LoginViewModel
import kotlinx.android.synthetic.main.fragment_login.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private lateinit var mLoginViewModel: LoginViewModel
private var mProgressDialog: ProgressDialog? = null

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // initializing progress dailog
        mProgressDialog = activity?.let { Utills.initializeProgressBar(it) }

        mLoginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)


        button_login.setOnClickListener {
            validateFields()
        }
        mLoginViewModel.response().observe(requireActivity(), Observer {
            when(it){
                is Resource<UserModel> ->{
                    handleIssuesResponse(it)
                }
            }
        } )

        image_cancel.setOnClickListener {  findNavController().navigate(
            R.id.loginToHome, null) }
        /* Handler().postDelayed({

         }, 3000)*/


    }

    private fun handleIssuesResponse(it: Resource<UserModel>) {

        when(it.status){
            Resource.Status.LOADING -> showProgressBar()
            Resource.Status.SUCCESS -> loginSuccess(it.data!!)
            Resource.Status.ERROR -> showError(it.exception!!)

        }
    }
    private fun showProgressBar() {
        mProgressDialog?.show()
    }

    private fun loginSuccess(userData: List<UserModel>) {

        hideProgressbar()
        if (this.activity!=null) {
            val navController =
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
            val id = navController.currentDestination!!.id
            Log.e("id", "$id")
            if (id == R.id.login) {
                findNavController().navigate(
                    R.id.loginToHome, null, NavOptions.Builder()
                        .setPopUpTo(
                            R.id.splash,
                            true
                        ).build()
                )
            }
        }
    }

    private fun showError(error: String) {
       showMessage(error)
        hideProgressbar()
    }



    //hide the progressbar
    private fun hideProgressbar() {
        mProgressDialog?.hide()
    }
    private fun validateFields() {
        val email = text_emailLogin.editText?.text.toString()
        val passsword = text_passwordLogin.editText?.text.toString()
        if (!mLoginViewModel.isValidEmail(email)) {
            showMessage(getString(R.string.error_valid_email))
            text_emailLogin.isErrorEnabled = true
        } else {
            text_emailLogin.isErrorEnabled = false
        }
        if (!mLoginViewModel.validPassword(passsword)) {
            showMessage(getString(R.string.error_valid_password))
            text_passwordLogin.isErrorEnabled = true
        } else {
            text_passwordLogin.isErrorEnabled = false
        }
        if (mLoginViewModel.isValidEmail(email) && mLoginViewModel.validPassword(passsword)) {
            mProgressDialog?.show()
            mLoginViewModel.login(LoginRequest(email, passsword))
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SplashFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}