package com.tekmindz.covidhealthcare.ui.patientDetails

import android.app.ProgressDialog
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.tekmindz.covidhealthcare.R
import com.tekmindz.covidhealthcare.constants.Constants
import com.tekmindz.covidhealthcare.databinding.PatientDetailFragmentBinding
import com.tekmindz.covidhealthcare.repository.responseModel.PatientDetails
import com.tekmindz.covidhealthcare.repository.responseModel.PatientObservations
import com.tekmindz.covidhealthcare.utills.Resource


import com.tekmindz.covidhealthcare.utills.Utills

class PatientDetailFragment : Fragment() {
    private lateinit var binding: PatientDetailFragmentBinding
    private var mProgressDialog: ProgressDialog? = null

    companion object {
        fun newInstance() = PatientDetailFragment()
    }

    private lateinit var mPatientDetailViewModel: PatientDetailViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.patient_detail_fragment, container, false
        )
        val view: View = binding.getRoot()
        binding.setLifecycleOwner(this);

        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mProgressDialog = activity?.let { Utills.initializeProgressBar(it) }

        mPatientDetailViewModel = ViewModelProviders.of(this).get(PatientDetailViewModel::class.java)

        binding.patientDetailsBind = (mPatientDetailViewModel);

        arguments?.takeIf { it.containsKey(Constants.PATIENT_ID) }?.apply {
            val patientId = getInt(Constants.PATIENT_ID)
            mPatientDetailViewModel.getPatientDetails(patientId = patientId.toString())
           // mPatientDetailViewModel.getPatientObservations(patientId.toString())
        }

        mPatientDetailViewModel.getPatientDetails().observe(requireActivity()!!, Observer {
            when (it) {

                is Resource<PatientDetails> -> handlePatientDetails(it)
            }
        })

        mPatientDetailViewModel.getPatientObservations().observe(requireActivity()!!, Observer {
            when (it) {

                is Resource<PatientObservations> -> handlePatientObservations(it)
            }
        })


    }


    private fun handlePatientDetails(it: Resource<PatientDetails>) {

        when (it.status) {
            Resource.Status.LOADING -> showProgressBar()
            Resource.Status.SUCCESS -> showPatientDeatils(it.data!!)
            Resource.Status.ERROR -> showError(it.exception!!)
        }
    }

    private fun handlePatientObservations(it: Resource<PatientObservations>) {

        when (it.status) {
            //Resource.Status.LOADING -> showProgressBar()
            Resource.Status.SUCCESS -> showPatientObserVations(it.data!!)
            Resource.Status.ERROR -> showError(it.exception!!)
        }
    }

    private fun showPatientObserVations(data: PatientObservations) {
      /*  binding.tvHeartRateValue.text = data.heartRate
        binding.tvRespirationRateValue.text = data.respirationRate
        binding.tvBodyTempratureValue.text = data.bodyTemprature
        binding.tvPatientStatus.text = data.patientStatus
*/
    }

    private fun showPatientDeatils(data: PatientDetails) {
        Glide.with(requireActivity()).load(data.imageUrl).into(binding.imgPatientProfile)
        binding.tvPatientName.text = data.firstName+" "+data.lastName
        binding.tvPatientDob.text = mPatientDetailViewModel.parseDate(data.dob)
        binding.tvGenderId.text = data.gender.toUpperCase()
        binding.tvBedNo.text = data.bedNumber
        binding.tvWardNo.text = data.wardNo
        binding.tvRelayId.text = data.relayId
        binding.tvAddmittedSince.text = mPatientDetailViewModel.parseDate(data.admittedDate)
        binding.tvBioSensorId.text  = data.wearableIdentifier
        binding.tvHeartRateValue.text = data.heartRate
        binding.tvRespirationRateValue.text = data.respirationRate
        binding.tvBodyTempratureValue.text = data.bodyTemprature
        binding.tvPatientStatus.text = data.status
        hideProgressbar()
    }

    private fun showProgressBar() {
        mProgressDialog?.show()
    }

    private fun showError(error: String) {
        showMessage(error)
        hideProgressbar()
    }

    private fun hideProgressbar() {
         mProgressDialog?.hide()
    }

    private fun showMessage(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }
}