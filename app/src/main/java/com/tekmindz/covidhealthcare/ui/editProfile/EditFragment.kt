package com.tekmindz.covidhealthcare.ui.editProfile

import android.app.ProgressDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.util.Log
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
import androidx.navigation.Navigation
import com.tekmindz.covidhealthcare.R
import com.tekmindz.covidhealthcare.databinding.FragmentEditProfileBinding
import com.tekmindz.covidhealthcare.repository.requestModels.EditProfileRequest
import com.tekmindz.covidhealthcare.repository.responseModel.EditProfileResponse
import com.tekmindz.covidhealthcare.utills.Resource
import com.tekmindz.covidhealthcare.utills.Utills


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private lateinit var mEditProfileViewModel: EditProfileViewModel
private var mProgressDialog: ProgressDialog? = null

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditProfileFragment : Fragment() {
    private lateinit var binding: FragmentEditProfileBinding

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
            inflater, R.layout.fragment_edit_profile, container, false
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

        mEditProfileViewModel = ViewModelProviders.of(this).get(EditProfileViewModel::class.java)
        binding.editProfileViewModel = mEditProfileViewModel

        /* button_login.setOnClickListener {
             validateFields()
         }*/

        mEditProfileViewModel.response().observe(requireActivity(), Observer {
            when (it) {
                is Resource<EditProfileResponse> -> {
                    handleIssuesResponse(it)
                }
            }
        })

        mEditProfileViewModel.getProfileData()?.observe(requireActivity(), Observer<EditProfileRequest> { loginUser ->
            validateFields(loginUser)

        })
        binding.etContactNumber.editText?.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (validateEditText((v as EditText).text)) {
                    binding.etContactNumber.isErrorEnabled = false
                } else {
                    binding.etContactNumber.isErrorEnabled = true
                    binding.etContactNumber.error = getString(R.string.error_valid_mobile)
                }
            }
        }

        binding.etEmergencyContactNumber.editText?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (mEditProfileViewModel.isValidEmergencyContactNumber(binding.etEmergencyContactNumber.editText?.text.toString())) {
                    binding.etEmergencyContactNumber.error = getString(R.string.error_valid_emergency_contact_number)
                    binding.etEmergencyContactNumber.isErrorEnabled = true
                } else {
                    binding.etEmergencyContactNumber.isErrorEnabled = false
                }
                true
            }
            false
        }

        binding.etEmergencyContactNumber.editText?.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (validateEditText((v as EditText).text)) {
                    binding.etEmergencyContactNumber.isErrorEnabled = false
                } else {
                    binding.etEmergencyContactNumber.isErrorEnabled = true
                    binding.etEmergencyContactNumber.error = getString(R.string.error_valid_emergency_contact_number)
                }
            }
        }

    }

    private fun validateEditText(s: Editable): Boolean {
        return !TextUtils.isEmpty(s)
    }

    private fun handleIssuesResponse(it: Resource<EditProfileResponse>) {

        when (it.status) {
            Resource.Status.LOADING -> showProgressBar()
            Resource.Status.SUCCESS -> updateSuccess(it.data!!)
            Resource.Status.ERROR -> showError(it.exception!!)

        }
    }

    private fun showProgressBar() {
        mProgressDialog?.show()
    }

    private fun updateSuccess(mEditProfileResponse:EditProfileResponse) {
Log.e("mEditProfileResponse", "$mEditProfileResponse")
        hideProgressbar()

        if (this.activity != null) {

            val navController =
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
            val id = navController.currentDestination!!.id
            //Log.e("id", "$id")
            //mLoginViewModel.saveUserData(PREF_ACCESS_TOKEN, userData.access_token)


          /*  if (id == R.id.login) {
                hideKeyboard(requireActivity())
                findNavController().navigate(
                    R.id.loginToHome, null, NavOptions.Builder()
                        .setPopUpTo(
                            R.id.login,
                            true
                        ).build()
                )
            }*/
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

    private fun validateFields(mEditProfileRequest: EditProfileRequest) {


        if (mEditProfileViewModel.isValidMobileNumber(binding.etContactNumber.editText?.text.toString()) ||  binding.etContactNumber.editText?.text.toString().trim().length>20) {

            binding.etContactNumber.error = getString(R.string.error_valid_mobile)
            binding.etContactNumber.isErrorEnabled = true
        } else {

            binding.etContactNumber.isErrorEnabled = false
        }

        if (mEditProfileViewModel.isValidEmergencyContactNumber(binding.etEmergencyContactNumber.editText?.text.toString())) {
            binding.etEmergencyContactNumber.error = getString(R.string.error_valid_emergency_contact_number)
            binding.etEmergencyContactNumber.isErrorEnabled = true
        } else {

            binding.etEmergencyContactNumber.isErrorEnabled = false
        }

        if (!mEditProfileViewModel.isValidMobileNumber(binding.etContactNumber.editText?.text.toString())
            && !mEditProfileViewModel.isValidEmergencyContactNumber(binding.etEmergencyContactNumber.editText?.text.toString())
            && binding.etContactNumber.editText?.text.toString().trim().length<=20
        ) {

            if (Utills.verifyAvailableNetwork(activity = requireActivity())) {

                mProgressDialog?.show()
                mEditProfileViewModel.updateContactInfo(mEditProfileRequest)

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
    }
}