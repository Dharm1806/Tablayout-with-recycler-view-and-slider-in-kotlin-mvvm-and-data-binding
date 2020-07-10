package com.tekmindz.covidhealthcare.ui.patientDetails


import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.tekmindz.covidhealthcare.R
import com.tekmindz.covidhealthcare.constants.Constants
import com.tekmindz.covidhealthcare.constants.Constants.ARG_PATIENT_NAME
import com.tekmindz.covidhealthcare.databinding.PatientDetailFragmentBinding
import com.tekmindz.covidhealthcare.repository.responseModel.Details
import com.tekmindz.covidhealthcare.repository.responseModel.PatientDetails
import com.tekmindz.covidhealthcare.repository.responseModel.PatientObservation
import com.tekmindz.covidhealthcare.repository.responseModel.PatientObservations
import com.tekmindz.covidhealthcare.utills.Resource
import com.tekmindz.covidhealthcare.utills.Utills

class PatientDetailFragment : Fragment() {
    private lateinit var binding: PatientDetailFragmentBinding
    private var mProgressDialog: ProgressDialog? = null
    lateinit var patientId: String
    lateinit var patientName :String

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
        val view: View = binding.root
        binding.lifecycleOwner = this

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mProgressDialog =
            activity?.let { Utills.initializeProgressBar(it, R.style.AppTheme_WhiteAccent) }

        mPatientDetailViewModel =
            ViewModelProviders.of(this).get(PatientDetailViewModel::class.java)

        binding.patientDetailsBind = (mPatientDetailViewModel)

        arguments?.takeIf { it.containsKey(Constants.PATIENT_ID) }?.apply {
            patientId = getInt(Constants.PATIENT_ID).toString()
            if (Utills.verifyAvailableNetwork(requireActivity())) {
                showProgressBar()

                mPatientDetailViewModel.getPatientDetails(patientId = patientId.toString())
                mPatientDetailViewModel.getPatientObservations(patientId.toString())
            }
        }

        mPatientDetailViewModel.getPatientDetails().observe(requireActivity(), Observer {
            when (it) {

                is Resource<PatientDetails> -> handlePatientDetails(it)
            }
        })

        mPatientDetailViewModel.getPatientObservations().observe(requireActivity(), Observer {
            when (it) {

                is Resource<PatientObservations> -> handlePatientObservations(it)
            }
        })

        binding.viewAnalytics.setOnClickListener {
            val bundle = bundleOf(
                "patientId" to patientId.toInt(),
                "patientName" to patientName
            )

            findNavController().navigate(R.id.pDetailsToAnalytics, bundle)
        }

        binding.updateBp.setOnClickListener {
            val bundle = bundleOf("patientId" to patientId.toString())
            findNavController().navigate(R.id.pDetailsToUpdateReadings, bundle)
        }

    }


    private fun handlePatientDetails(it: Resource<PatientDetails>) {

        when (it.status) {
            //Resource.Status.LOADING -> showProgressBar()
            Resource.Status.SUCCESS -> {
                if (it.data?.statusCode == 200 && it.data.body != null) showPatientDeatils(it.data.body)
                else if (it.data?.statusCode == 401 ){
                    mPatientDetailViewModel.refreshToken()

                    Handler().postDelayed({
                        mPatientDetailViewModel.getPatientDetails(patientId = patientId.toString())
                    }, Constants.DELAY_IN_API_CALL)

                }
                else showError(it.data?.message!!)

            }
            Resource.Status.ERROR -> showError(it.exception!!)
        }
    }

    private fun handlePatientObservations(it: Resource<PatientObservations>) {

        when (it.status) {
            //Resource.Status.LOADING -> showProgressBar()
            Resource.Status.SUCCESS -> {
                if (it.data?.statusCode == 200 && it.data.body != null) showPatientObserVations(it.data.body)
                else if (it.data?.statusCode == 401 ){
                    mPatientDetailViewModel.refreshToken()

                    Handler().postDelayed({
                        mPatientDetailViewModel.getPatientObservations(patientId = patientId.toString())

                    }, Constants.DELAY_IN_API_CALL)
                }
                else showError(it.data?.message!!)

            }
            Resource.Status.ERROR -> showError(it.exception!!)
        }
    }

    private fun showPatientObserVations(data: PatientObservation) {
        binding.tvHeartRateValue.text = Utills.formatString(data.heartRate)
        binding.tvRespirationRateValue.text = Utills.formatString(data.respirationRate)
        binding.tvBodyTempratureValue.text = Utills.formatString(data.bodyTemprature)

        if (data.status.equals(Constants.STATE_RECOVERED)) {
            binding.tvPatientStatus.background = activity?.getDrawable(R.drawable.recovered_bg)
        }

        if (data.status.equals(Constants.STATE_UNDER_CONTROL)) {
            binding.tvPatientStatus.background = activity?.getDrawable(R.drawable.under_control_bg)
        }

        if (data.status.equals(Constants.STATE_CRITICAL)) {
            binding.tvPatientStatus.background = activity?.getDrawable(R.drawable.critical_bg)
        }

        binding.tvPatientStatus.text = data.status

    }

    private fun showPatientDeatils(data: Details) {
        patientName = data.firstName+" "+data.lastName
        Glide.with(requireActivity()).load(data.imageUrl).into(binding.imgPatientProfile)

        binding.tvPatientName.text = patientName
        binding.tvPatientDob.text = mPatientDetailViewModel.parseDate(data.dob)
        binding.tvGenderId.text = data.gender.toUpperCase()
        binding.tvBedNo.text = data.bedNumber
        binding.tvWardNo.text = data.wardNo
        binding.tvRelayId.text = data.relayId
        binding.tvAddmittedSince.text = mPatientDetailViewModel.parseDate(data.admittedDate)
        binding.tvBioSensorId.text = data.wearableIdentifier
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