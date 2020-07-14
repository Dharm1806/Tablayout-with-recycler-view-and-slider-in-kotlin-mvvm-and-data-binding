package com.tekmindz.covidhealthcare.ui.setBaseUrl

import android.app.ProgressDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.tekmindz.covidhealthcare.R
import com.tekmindz.covidhealthcare.application.App
import com.tekmindz.covidhealthcare.constants.Constants
import com.tekmindz.covidhealthcare.constants.Constants.PREF_BASE_URL
import com.tekmindz.covidhealthcare.databinding.FragmentSetBaseUrlLoginBinding
import com.tekmindz.covidhealthcare.repository.requestModels.LoginRequest
import com.tekmindz.covidhealthcare.utills.Utills
import com.tekmindz.covidhealthcare.utills.Utills.hideKeyboard


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private lateinit var mLoginViewModel: SetBaseUrlViewModel
private var mProgressDialog: ProgressDialog? = null

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SetBaseUrlFragment : Fragment() {
    private lateinit var binding: FragmentSetBaseUrlLoginBinding

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
            inflater, R.layout.fragment_set_base_url_login, container, false
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

        mLoginViewModel = ViewModelProviders.of(this).get(SetBaseUrlViewModel::class.java)
        binding.loginViewModel = mLoginViewModel

        val mBaseUrl =
            App.mSharedPrefrenceManager.getValueString("base_url") ?: "http://34.216.159.69:8081/"
    mLoginViewModel.Password.postValue(mBaseUrl)
        mLoginViewModel.getUser()?.observe(requireActivity(), Observer<LoginRequest> { loginUser ->
            validateFields(loginUser)

        })
        binding.textEmailLogin.editText?.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (validateEditText((v as EditText).text)) {
                    binding.textEmailLogin.isErrorEnabled = false
                } else {
                    binding.textEmailLogin.isErrorEnabled = true
                    binding.textEmailLogin.error = getString(R.string.error_valid_email)
                }
            }
        }

        binding.textPasswordLogin.editText?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (mLoginViewModel.validPassword(binding.textPasswordLogin.editText?.text.toString())) {
                    binding.textPasswordLogin.error = getString(R.string.error_valid_password)
                    binding.textPasswordLogin.isErrorEnabled = true
                } else {
                    binding.textPasswordLogin.isErrorEnabled = false
                }
                true
            }
            false
        }
        binding.textPasswordLogin.editText?.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (validateEditText((v as EditText).text)) {
                    binding.textPasswordLogin.isErrorEnabled = false
                } else {
                    binding.textPasswordLogin.isErrorEnabled = true
                    binding.textPasswordLogin.error = getString(R.string.error_valid_password)
                }
            }
        }

    }

    private fun validateEditText(s: Editable): Boolean {
        return !TextUtils.isEmpty(s)
    }


    private fun showProgressBar() {
        mProgressDialog?.show()
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
                //  mProgressDialog?.show()
                if (binding.textEmailLogin.editText?.text.toString() == "supervisor") {
                    App.mSharedPrefrenceManager.saveString(
                        PREF_BASE_URL,
                        binding.textPasswordLogin.editText?.text.toString()
                    )
                    App.mSharedPrefrenceManager.setIsLogin(Constants.PREF_IS_LOGIN, false)
                    App().initalize()
                    hideKeyboard(requireActivity())
                    findNavController().navigate(
                        R.id.setBaseUrlToHome, null, NavOptions.Builder()
                            .setPopUpTo(
                                R.id.setBaseUrl,
                                true
                            ).build()
                    )
                } else {
                    Toast.makeText(
                        requireActivity(),
                        "Please enter valid username",
                        Toast.LENGTH_LONG
                    )
                }
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

    }
}