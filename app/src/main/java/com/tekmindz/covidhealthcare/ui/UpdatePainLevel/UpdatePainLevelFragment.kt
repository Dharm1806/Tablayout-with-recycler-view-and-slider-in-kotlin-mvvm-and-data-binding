package com.tekmindz.covidhealthcare.ui.UpdatePainLevel

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
import com.tekmindz.covidhealthcare.R
import com.tekmindz.covidhealthcare.constants.Constants.PATIENT_ID
import com.tekmindz.covidhealthcare.constants.UserTypes
import com.tekmindz.covidhealthcare.databinding.FragmentUpdatePainLevelBinding
import com.tekmindz.covidhealthcare.repository.requestModels.UpdatePainLevel
import com.tekmindz.covidhealthcare.repository.responseModel.PatientDetails
import com.tekmindz.covidhealthcare.utills.Resource
import com.tekmindz.covidhealthcare.utills.Utills


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private lateinit var mUpdatePainLevelViewModel: UpdatePainLevelViewModel
private var mProgressDialog: ProgressDialog? = null
private lateinit var patientId: String

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UpdatePainLevelFragment : Fragment() {
    private lateinit var binding: FragmentUpdatePainLevelBinding

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
            inflater, R.layout.fragment_update_pain_level, container, false
        )
        val view: View = binding.root
        binding.lifecycleOwner = this
        // setHasOptionsMenu(true);
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // initializing progress dailog
        mProgressDialog =
            activity?.let { Utills.initializeProgressBar(it, R.style.AppTheme_WhiteAccent) }

        mUpdatePainLevelViewModel =
            ViewModelProviders.of(this).get(UpdatePainLevelViewModel::class.java)
      // binding.updatePainLevel = (mUpdatePainLevelViewModel)?:null

        /* button_login.setOnClickListener {
             validateFields()
         }*/

        mUpdatePainLevelViewModel.response().observe(requireActivity(), Observer {
            when (it) {
                is Resource<PatientDetails> -> {
                    handlePatientDetails(it)
                }
            }
        })
        mUpdatePainLevelViewModel.getUpdatedPatientDetails()
            ?.observe(requireActivity(), Observer<UpdatePainLevel> { updateDetails ->

                validateFields(updateDetails)
            })


    }
    /* override fun onPrepareOptionsMenu(menu: Menu) {
         val item: MenuItem = menu.findItem(R.id.sos)
         item.isVisible = mUpdatePatientViewModel.getUserType() == UserTypes.PATIENT.toString()
     }*/

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

    private fun validateFields(updatePatientReadings: UpdatePainLevel) {




        if (!mUpdatePainLevelViewModel.isValidBedNumber(updatePatientReadings.bedNumber)
            && !mUpdatePainLevelViewModel.isValidWardNumber(updatePatientReadings.wardNumber)
            && !mUpdatePainLevelViewModel.isValidSys(updatePatientReadings.sys)
            && !mUpdatePainLevelViewModel.isValidDia(updatePatientReadings.dia)
        ) {
            if (Utills.verifyAvailableNetwork(requireActivity())) {
                mProgressDialog?.show()
                mUpdatePainLevelViewModel.updatePainLevel(updatePatientReadings)
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