package com.tekmindz.covidhealthcare.ui.patientDetails


import android.Manifest
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
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
import com.google.android.material.textfield.TextInputLayout
import com.tekmindz.covidhealthcare.HomeActivity
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
import kotlinx.android.synthetic.main.add_blood_pressure.*
import kotlinx.android.synthetic.main.patient_detail_fragment.*


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
        //setHasOptionsMenu(true);
        Utills.hideKeyboard(requireActivity())
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
          Utills.callPhoneNumber(requireActivity())
           /*val bundle = bundleOf("patientId" to patientId.toString())
            findNavController().navigate(R.id.pDetailsToUpdateReadings, bundle)
*/
        }
        binding.painLevel.setOnClickListener {
            val bundle = bundleOf("patientId" to patientId.toString())
            findNavController().navigate(R.id.pDetailsToUpdateReadings, bundle)
        }

        binding.addBloodPressure.setOnClickListener {
            addBP()
        }

        binding.addSpo2.setOnClickListener {
            addSpO2Al()
        }


    }

    private fun addSpO2Al() {

        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.add_oxygen)
        val etSpO2 = dialog.findViewById(R.id.et_spo2) as TextInputLayout
        //body.text = title
        val saveSpO2 = dialog.findViewById(R.id.saveSpO2) as TextView
        val cancelSpO2 = dialog.findViewById(R.id.cancelSpO2) as TextView

        saveSpO2.setOnClickListener {
            val spO2 = etSpO2.editText?.text
            if (spO2?.trim()?.length==0){
                etSpO2.isErrorEnabled= true
                etSpO2.error = getString(R.string.err_spo2)
            }else{
                etSpO2.isErrorEnabled =false
                dialog.dismiss()
                Log.e("spo2", "$spO2")
            }
        }
        cancelSpO2.setOnClickListener { dialog.dismiss() }
        dialog.show()
        val cancelAction = dialog.findViewById(R.id.cancel_action) as ImageView

        cancelAction.setOnClickListener { dialog.dismiss() }

    }

    private fun addBP() {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.add_blood_pressure)
        val etSys = dialog.findViewById(R.id.tv_Sys) as TextInputLayout
        val etDia = dialog.findViewById(R.id.tv_dia) as TextInputLayout

        //body.text = title
        val saveBloodPressure = dialog.findViewById(R.id.saveBloodPressure) as TextView
        val cancelBloodPressure = dialog.findViewById(R.id.cancelBloodPressure) as TextView
        val cancelAction = dialog.findViewById(R.id.cancel_action) as ImageView
        saveBloodPressure.setOnClickListener {
            val sys = etSys.editText?.text
            if (sys?.trim()?.length==0){
                etSys.isErrorEnabled= true
                etSys.error = getString(R.string.err_sys)
            }else{
                etSys.isErrorEnabled =false
                Log.e("SYS", "$sys")
            }

            val dia = etDia.editText?.text
            if (dia?.trim()?.length==0){
                etDia.isErrorEnabled= true
                etDia.error = getString(R.string.error_valid_dia)
            }else{
                etDia.isErrorEnabled =false
                Log.e("dia", "$dia")
            }

            if (sys?.trim()?.length!=0 && dia?.trim()?.length!=0){

            dialog.dismiss()}
        }

        cancelBloodPressure.setOnClickListener { dialog.dismiss() }
        cancelAction.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }




    /*override fun onPrepareOptionsMenu(menu: Menu) {
        val item: MenuItem = menu.findItem(R.id.sos)
        item.isVisible = mPatientDetailViewModel.getUserType() == UserTypes.PATIENT.toString()
    }
*/
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
    /*fun callPhoneNumber() {
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
    }*/


}