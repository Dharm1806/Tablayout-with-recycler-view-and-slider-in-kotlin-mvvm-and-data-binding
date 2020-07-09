package com.tekmindz.covidhealthcare.ui.login

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.tekmindz.covidhealthcare.R
import com.tekmindz.covidhealthcare.constants.Constants.PREF_ACCESS_TOKEN
import com.tekmindz.covidhealthcare.constants.Constants.PREF_EXPIRES_IN
import com.tekmindz.covidhealthcare.constants.Constants.PREF_REFRESH_EXPIRES_IN
import com.tekmindz.covidhealthcare.constants.Constants.PREF_REFRESH_TOKEN
import com.tekmindz.covidhealthcare.constants.Constants.PREF_SCOPE
import com.tekmindz.covidhealthcare.constants.Constants.PREF_SESSION_STATE
import com.tekmindz.covidhealthcare.constants.Constants.PREF_TOKEN_TYPE
import com.tekmindz.covidhealthcare.databinding.FragmentLoginBinding
import com.tekmindz.covidhealthcare.repository.requestModels.LoginRequest
import com.tekmindz.covidhealthcare.repository.responseModel.UserModel
import com.tekmindz.covidhealthcare.utills.Resource
import com.tekmindz.covidhealthcare.utills.Utills


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
    private lateinit var binding: FragmentLoginBinding

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
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_login, container, false
        )
        val view: View = binding.root
        binding.lifecycleOwner = this

        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // initializing progress dailog
        mProgressDialog =
            activity?.let { Utills.initializeProgressBar(it, R.style.AppTheme_WhiteAccent) }

        mLoginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        binding.loginViewModel = mLoginViewModel

        /* button_login.setOnClickListener {
             validateFields()
         }*/

        mLoginViewModel.response().observe(requireActivity(), Observer {
            when (it) {
                is Resource<UserModel> -> {
                    handleIssuesResponse(it)
                }
            }
        })
        mLoginViewModel.getUser()?.observe(requireActivity(), Observer<LoginRequest> { loginUser ->

            validateFields(loginUser)
        })
    }

    private fun handleIssuesResponse(it: Resource<UserModel>) {

        when (it.status) {
            Resource.Status.LOADING -> showProgressBar()
            Resource.Status.SUCCESS -> loginSuccess(it.data!!)
            Resource.Status.ERROR -> showError(it.exception!!)

        }
    }

    private fun showProgressBar() {
        mProgressDialog?.show()
    }

    private fun loginSuccess(userData: UserModel) {

        hideProgressbar()

        if (this.activity != null) {
            val navController =
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
            val id = navController.currentDestination!!.id
            Log.e("id", "$id")
            mLoginViewModel.saveUserData(PREF_ACCESS_TOKEN, userData.access_token)
            mLoginViewModel.saveUserData(PREF_EXPIRES_IN, userData.expires_in.toString())
            mLoginViewModel.saveUserData(
                PREF_REFRESH_EXPIRES_IN,
                userData.refresh_expires_in.toString()
            )
            mLoginViewModel.saveUserData(PREF_REFRESH_TOKEN, userData.refresh_token)
            mLoginViewModel.saveUserData(PREF_TOKEN_TYPE, userData.token_type)
            //mLoginViewModel.saveUserData(PREF_NOT_BEFORE_POLICY, userData.not_before_policy)
            mLoginViewModel.saveUserData(PREF_SESSION_STATE, userData.session_state)
            mLoginViewModel.saveUserData(PREF_SCOPE, userData.scope)
            mLoginViewModel.setIsLogin(true)

            if (id == R.id.login) {
                findNavController().navigate(
                    R.id.loginToHome, null, NavOptions.Builder()
                        .setPopUpTo(
                            R.id.login,
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

    private fun validateFields(loginUser: LoginRequest) {

        Log.e(
            "validations",
            "" + mLoginViewModel.isValidEmail(binding.textEmailLogin.editText?.text.toString())
                    + " " + mLoginViewModel.validPassword(binding.textPasswordLogin.editText?.text.toString())
        )
        if (mLoginViewModel.isValidEmail(binding.textEmailLogin.editText?.text.toString())) {

            binding.textEmailLogin.error = getString(R.string.error_valid_email)
            binding.textEmailLogin.isErrorEnabled = true
        } else {

            binding.textEmailLogin.isErrorEnabled = false
        }

        if (mLoginViewModel.validPassword(binding.textPasswordLogin.editText?.text.toString())) {
            binding.textPasswordLogin.error = getString(R.string.error_valid_password)
            binding.textPasswordLogin.isErrorEnabled = true
        } else {

            binding.textPasswordLogin.isErrorEnabled = false
        }

        if (!mLoginViewModel.isValidEmail(binding.textEmailLogin.editText?.text.toString())
            && !mLoginViewModel.validPassword(binding.textPasswordLogin.editText?.text.toString())
        ) {

            if (Utills.verifyAvailableNetwork(activity = requireActivity())) {

                mProgressDialog?.show()
                mLoginViewModel.login(loginUser)
                Log.e("loginuser", Gson().toJson(loginUser))

            }
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