package com.tekmindz.covidhealthcare.ui.patientDetails


import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.tekmindz.covidhealthcare.R
import com.tekmindz.covidhealthcare.application.App
import com.tekmindz.covidhealthcare.constants.Constants
import com.tekmindz.covidhealthcare.constants.UserTypes
import com.tekmindz.covidhealthcare.databinding.PatientDetailFragmentBinding
import com.tekmindz.covidhealthcare.repository.responseModel.Details
import com.tekmindz.covidhealthcare.repository.responseModel.PatientDetails
import com.tekmindz.covidhealthcare.repository.responseModel.PatientObservation
import com.tekmindz.covidhealthcare.repository.responseModel.PatientObservations
import com.tekmindz.covidhealthcare.utills.Resource
import com.tekmindz.covidhealthcare.utills.Utills
import kotlinx.android.synthetic.main.activity_home.*


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

        if (mPatientDetailViewModel.getUserType() == UserTypes.PATIENT.toString()){
            binding.btSos.visibility = View.VISIBLE
         val appBarConfiguration = AppBarConfiguration(
                setOf(R.id.home, R.id.patient_details), requireActivity().findViewById(R.id.drawer_layout))
            requireActivity().toolbar.setupWithNavController(findNavController(), appBarConfiguration)

        }else{
            val appBarConfiguration = AppBarConfiguration(
                setOf(R.id.home), requireActivity().findViewById(R.id.drawer_layout))
            requireActivity().toolbar.setupWithNavController(findNavController(), appBarConfiguration)

            binding.btSos.visibility = View.GONE
        }

        binding.btSos.setOnClickListener {
           callPhoneNumber()
        /*    val bundle = bundleOf("patientId" to patientId.toString())
            findNavController().navigate(R.id.pDetailsToUpdateReadings, bundle)*/

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
       // formatString("103.012955")
        binding.tvHeartRateValue.text =Utills.round(data.heartRate)
        binding.tvRespirationRateValue.text =Utills.round(data.respirationRate)
        binding.tvBodyTempratureValue.text = Utills.round(data.bodyTemprature)

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
        Glide.with(requireActivity()).load(data.imageUrl).placeholder(R.drawable.ic_placeholder).into(binding.imgPatientProfile)

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
    fun callPhoneNumber() {
        try {
            if (Build.VERSION.SDK_INT > 22) {
                if (ActivityCompat.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.CALL_PHONE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.CALL_PHONE),
                        101
                    )
                    return
                }
                val callIntent = Intent(Intent.ACTION_CALL)
                callIntent.data = Uri.parse("tel:" +  Constants.SOS_NUMBER)
                startActivity(callIntent)
            } else {
                val callIntent = Intent(Intent.ACTION_CALL)
                callIntent.data = Uri.parse("tel:" + Constants.SOS_NUMBER)
                startActivity(callIntent)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override  fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 101) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callPhoneNumber()
            }
        }
    }
}