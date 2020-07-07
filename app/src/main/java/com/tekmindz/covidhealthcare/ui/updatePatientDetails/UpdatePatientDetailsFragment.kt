package com.tekmindz.covidhealthcare.ui.updatePatientDetails

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.tekmindz.covidhealthcare.R
import com.tekmindz.covidhealthcare.constants.Constants
import com.tekmindz.covidhealthcare.constants.Constants.PATIENT_ID
import com.tekmindz.covidhealthcare.constants.Constants.PREF_ACCESS_TOKEN
import com.tekmindz.covidhealthcare.constants.Constants.PREF_EXPIRES_IN
import com.tekmindz.covidhealthcare.constants.Constants.PREF_REFRESH_EXPIRES_IN
import com.tekmindz.covidhealthcare.constants.Constants.PREF_REFRESH_TOKEN
import com.tekmindz.covidhealthcare.constants.Constants.PREF_SCOPE
import com.tekmindz.covidhealthcare.constants.Constants.PREF_SESSION_STATE
import com.tekmindz.covidhealthcare.constants.Constants.PREF_TOKEN_TYPE
import com.tekmindz.covidhealthcare.databinding.FragmentLoginBinding
import com.tekmindz.covidhealthcare.databinding.FragmentUpdatePatientBinding
import com.tekmindz.covidhealthcare.repository.requestModels.LoginRequest
import com.tekmindz.covidhealthcare.repository.requestModels.UpdatePatientReadings
import com.tekmindz.covidhealthcare.repository.responseModel.PatientDetails
import com.tekmindz.covidhealthcare.repository.responseModel.UserModel
import com.tekmindz.covidhealthcare.utills.Resource
import com.tekmindz.covidhealthcare.utills.Utills
import kotlinx.android.synthetic.main.fragment_login.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private lateinit var mUpdatePatientViewModel: UpdatePatientViewModel
private var mProgressDialog: ProgressDialog? = null
private lateinit var patientId:String
/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UpdatePatientDetailsFragment : Fragment() {
    private lateinit var binding: FragmentUpdatePatientBinding

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            patientId = it.getString(PATIENT_ID)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_update_patient, container, false
        )
        val view: View = binding.getRoot()
        binding.setLifecycleOwner(this);

        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // initializing progress dailog
        mProgressDialog = activity?.let { Utills.initializeProgressBar(it,R.style.AppTheme_WhiteAccent) }

        mUpdatePatientViewModel = ViewModelProviders.of(this).get(UpdatePatientViewModel::class.java)
        binding.updatePatientReadings = (mUpdatePatientViewModel);

       /* button_login.setOnClickListener {
            validateFields()
        }*/

        mUpdatePatientViewModel.response().observe(requireActivity(), Observer {
            when (it) {
                is Resource<PatientDetails> -> {
                    handlePatientDetails(it)
                }
            }
        })
        mUpdatePatientViewModel.getUpdatedPatientDetails()?.observe(requireActivity(), Observer<UpdatePatientReadings> { updateDetails ->

            validateFields(updateDetails)
        })
    }

    private fun handlePatientDetails(it: Resource<PatientDetails>) {

        when (it.status) {
            Resource.Status.LOADING -> showProgressBar()
            Resource.Status.SUCCESS -> loginSuccess(it.data!!)
            Resource.Status.ERROR -> showError(it.exception!!)

        }
    }

    private fun showProgressBar() {
        mProgressDialog?.show()
    }

    private fun loginSuccess(userData: PatientDetails) {

        hideProgressbar()

        if (this.activity != null) {
            val navController =
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
            val id = navController.currentDestination!!.id
            Log.e("id", "$id")

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

    private fun validateFields(updatePatientReadings: UpdatePatientReadings) {


        if (!mUpdatePatientViewModel.isValidBedNumber(updatePatientReadings.bedNumber)) {

            binding.textBedNo.error = getString(R.string.error_valid_bed_number)
            binding.textBedNo.isErrorEnabled = true
        } else {

            binding.textBedNo.isErrorEnabled = false
        }

        if (!mUpdatePatientViewModel.isValidWardNumber(updatePatientReadings.wardNumber)) {
            binding.textWardNo.error  = getString(R.string.error_valid_ward_no)
            binding.textWardNo.isErrorEnabled = true
        } else {

            binding.textWardNo.isErrorEnabled = false
        }

        if (!mUpdatePatientViewModel.isValidSys(updatePatientReadings.sys)) {
            binding.textSys.error  = getString(R.string.error_valid_sys)
            binding.textSys.isErrorEnabled = true
        } else {

            binding.textSys.isErrorEnabled = false
        }

        if (!mUpdatePatientViewModel.isValidDia(updatePatientReadings.dia)) {
            binding.textDia.error  = getString(R.string.error_valid_dia)
            binding.textDia.isErrorEnabled = true
        } else {

            binding.textDia.isErrorEnabled = false
        }

        if (mUpdatePatientViewModel.isValidBedNumber(updatePatientReadings.bedNumber)
            && mUpdatePatientViewModel.isValidWardNumber(updatePatientReadings.wardNumber)
            &&  mUpdatePatientViewModel.isValidSys(updatePatientReadings.sys)
            && mUpdatePatientViewModel.isValidDia(updatePatientReadings.dia)) {
            mProgressDialog?.show()

            mUpdatePatientViewModel.updatePatientDetails(updatePatientReadings)
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