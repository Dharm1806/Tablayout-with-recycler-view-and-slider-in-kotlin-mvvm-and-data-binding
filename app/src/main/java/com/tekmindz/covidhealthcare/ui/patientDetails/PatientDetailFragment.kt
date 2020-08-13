package com.tekmindz.covidhealthcare.ui.patientDetails


import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
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
import com.google.gson.Gson
import com.tekmindz.covidhealthcare.R
import com.tekmindz.covidhealthcare.constants.Constants
import com.tekmindz.covidhealthcare.databinding.PatientDetailFragmentBinding
import com.tekmindz.covidhealthcare.repository.requestModels.UpdateManualObservations
import com.tekmindz.covidhealthcare.repository.requestModels.UpdatePainLevel
import com.tekmindz.covidhealthcare.repository.responseModel.*
import com.tekmindz.covidhealthcare.utills.Resource
import com.tekmindz.covidhealthcare.utills.Utills
import kotlinx.android.synthetic.main.activity_home.*


class PatientDetailFragment : Fragment() {
    private var painLevel: String = "0"
    private lateinit var tvNotificationItemCount: TextView
    private lateinit var binding: PatientDetailFragmentBinding
    private var mProgressDialog: ProgressDialog? = null
    lateinit var patientId: String
    lateinit var patientName: String

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
        setHasOptionsMenu(true)

        return view
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.updateProfile -> {
                val bundle = bundleOf(
                    "patientId" to patientId.toInt()

                )

                findNavController().navigate(R.id.navigateToUpdateContactInfo, bundle)/*, NavOptions.Builder()
                    .setPopUpTo(
                        R.id.patient_details,
                        true
                    ).build())  */
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mProgressDialog =
            activity?.let { Utills.initializeProgressBar(it, R.style.AppTheme_WhiteAccent) }

        mPatientDetailViewModel =
            ViewModelProviders.of(this).get(PatientDetailViewModel::class.java)

        binding.patientDetailsBind = (mPatientDetailViewModel)

        arguments?.takeIf {
            it.containsKey(Constants.OBSERVATION)
        }?.apply {
            val observation = getParcelable<observations>(Constants.OBSERVATION) as observations
            patientId = observation.patientId.toString()
            //Log.e("patientId", "$patientId")
            showPatientDeatils(
                Details(
                    observation.firstName,
                    observation.lastName,
                    observation.gender,
                    observation.dob,
                    observation.bedNumber,
                    observation.admittedDate,
                    observation.wardNo,
                    "",
                    observation.relayId,
                    observation.wearableIdentifier.toString(),
                    "",
                    observation.imageUrl
                )
            )
            if (Utills.verifyAvailableNetwork(requireActivity())) {
                showProgressBar()

                //  mPatientDetailViewModel.getPatientDetails(patientId = patientId.toString())
                mPatientDetailViewModel.getPatientObservations(patientId.toString())
            }
        }

        arguments?.takeIf {
            it.containsKey(Constants.PATIENT_ID)
        }?.apply {
            if (getInt(Constants.PATIENT_ID) != 0) {
                patientId = getInt(Constants.PATIENT_ID).toString()
//                Log.e("patienTiD", "$patientId")
                getPatient()
            }
        }


        if (mPatientDetailViewModel.isPatient()) {
            getPatient()
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

        mPatientDetailViewModel.updateObservation().observe(requireActivity(), Observer {
            when (it) {

                is Resource<EditProfileResponse> -> handleUpdatePatientObservations(it)
            }
        })

        binding.viewAnalytics.setOnClickListener {
            val bundle = bundleOf(
                "patientId" to patientId.toInt(),
                "patientName" to patientName
            )

            findNavController().navigate(R.id.pDetailsToAnalytics, bundle)
        }

        if (mPatientDetailViewModel.isPatient()) {
            binding.btSos.visibility = View.VISIBLE
            binding.addBloodPressure.visibility = View.GONE
            binding.addSpo2.visibility = View.GONE
            binding.painLevel.visibility = View.VISIBLE
            val appBarConfiguration = AppBarConfiguration(
                setOf(R.id.home, R.id.patient_details),
                requireActivity().findViewById(R.id.drawer_layout)
            )
            requireActivity().toolbar.setupWithNavController(
                findNavController(),
                appBarConfiguration
            )

        } else {
            val appBarConfiguration = AppBarConfiguration(
                setOf(R.id.home), requireActivity().findViewById(R.id.drawer_layout)
            )
            requireActivity().toolbar.setupWithNavController(
                findNavController(),
                appBarConfiguration
            )
            binding.addBloodPressure.visibility = View.VISIBLE
            binding.addSpo2.visibility = View.VISIBLE
            binding.btSos.visibility = View.GONE
            binding.painLevel.visibility = View.GONE
        }
        if (mPatientDetailViewModel.isPatientAndHC()) {
            binding.painLevel.visibility = View.VISIBLE
        }

        binding.btSos.setOnClickListener {
            Utills.callPhoneNumber(requireActivity())
            /*val bundle = bundleOf("patientId" to patientId.toString())
             findNavController().navigate(R.id.pDetailsToUpdateReadings, bundle)
 */
        }
        binding.painLevel.setOnClickListener {
            val bundle = bundleOf(
                "patientId" to patientId,
                "painLevel" to painLevel
            )
            Log.e("painLevel", "$painLevel")
            findNavController().navigate(R.id.pDetailsToUpdateReadings, bundle)
        }

        binding.addBloodPressure.setOnClickListener {
            addBP()
        }

        binding.addSpo2.setOnClickListener {
            addSpO2Al()
        }


    }

    private fun getPatient() {
        val userInfoBody = mPatientDetailViewModel.getPatientInfo()
        patientId = userInfoBody.patientId.toString()
        if (Utills.verifyAvailableNetwork(requireActivity())) {
            showProgressBar()

            //  mPatientDetailViewModel.getPatientDetails(patientId = patientId.toString())
            mPatientDetailViewModel.getPatientObservations(patientId.toString())
        }

        showPatientDeatils(
            Details(
                userInfoBody.firstName,
                userInfoBody.lastName,
                userInfoBody.gender,
                userInfoBody.dob,
                userInfoBody.bedNumber,
                userInfoBody.admittedDate,
                userInfoBody.wardNo,
                "",
                userInfoBody.relayId,
                userInfoBody.wearableIdentifier.toString(),
                "",
                userInfoBody.imageUrl
            )
        )

    }

    private fun handleUpdatePatientObservations(it: Resource<EditProfileResponse>) {
        when (it.status) {
            //Resource.Status.LOADING -> showProgressBar()
            Resource.Status.SUCCESS -> {
                if ((it.data?.statusCode == 200 || it.data?.statusCode == 201)) hideProgressbar()// showError(it.data.message)
                else showError(it.data?.message!!)

            }
            Resource.Status.ERROR -> showError(it.exception!!)
        }
    }


    private fun addSpO2Al() {

        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.add_oxygen)
        val window: Window = dialog.window!!
        window.setLayout(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        val etSpO2 = dialog.findViewById(R.id.et_spo2) as TextInputLayout
        //body.text = title
        val saveSpO2 = dialog.findViewById(R.id.saveSpO2) as TextView
        val cancelSpO2 = dialog.findViewById(R.id.cancelSpO2) as TextView

        etSpO2.editText?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (etSpO2.editText?.text.toString().trim().length == 0) {
                    etSpO2.error = getString(R.string.err_spo2)
                    etSpO2.isErrorEnabled = true
                } else {
                    etSpO2.isErrorEnabled = false
                }
                true
            }
            false
        }

        etSpO2.editText?.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (!mPatientDetailViewModel.isValidSpo2(s)) {
                    etSpO2.isErrorEnabled = true
                    etSpO2.error = getString(R.string.err_spo2_100)
                } else {
                    etSpO2.isErrorEnabled = false
                }

            }
        })
        etSpO2.editText?.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if ((v as EditText).text.toString().trim().length != 0) {
                    etSpO2.isErrorEnabled = false
                } else {
                    etSpO2.isErrorEnabled = true
                    etSpO2.error = getString(R.string.err_spo2)
                }
            }
        }
        saveSpO2.setOnClickListener {
            val spO2 = etSpO2.editText?.text.toString()
            if (spO2.trim().length == 0) {
                etSpO2.isErrorEnabled = true
                etSpO2.error = getString(R.string.err_spo2)
            } else if (mPatientDetailViewModel.isValidSpo2(spO2)) {
                etSpO2.isErrorEnabled = false
                Utills.hide(requireContext())
                dialog.dismiss()
                showProgressBar()
                binding.tvSpO2.text = spO2 + " " + getString(R.string.spo2_unit)

                updateObservationType(
                    UpdateManualObservations(
                        listOf<UpdatePainLevel>(
                            UpdatePainLevel(
                                patientId,
                                Constants.OBSERVATION_TYPE_SPO2,
                                spO2,
                                Utills.getCurrentDate()
                            )
                        )
                    )
                )
            }
        }
        //  cancelSpO2.setOnClickListener { dialog.dismiss() }
        dialog.show()
        val cancelAction = dialog.findViewById(R.id.cancel_action) as ImageView

        cancelAction.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                Utills.hide(requireContext())

                dialog.dismiss()
                return v?.onTouchEvent(event) ?: true
            }
        })
        cancelSpO2.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {

                Utills.hide(requireContext())

                dialog.dismiss()
                return v?.onTouchEvent(event) ?: true
            }
        })

    }

    private fun updateObservationType(updateManualObservations: UpdateManualObservations) {

        mPatientDetailViewModel.updateObservationType(updateManualObservations)
    }

    private fun addBP() {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.add_blood_pressure)
        val window: Window = dialog.window!!
        window.setLayout(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        val etSys = dialog.findViewById(R.id.tv_Sys) as TextInputLayout
        val etDia = dialog.findViewById(R.id.tv_dia) as TextInputLayout

        etSys.editText?.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if ((v as EditText).text.toString().trim().length != 0) {
                    etSys.isErrorEnabled = false
                } else {
                    etSys.isErrorEnabled = true
                    etSys.error = getString(R.string.err_sys)
                }
            }
        }

        etDia.editText?.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if ((v as EditText).text.toString().trim().length != 0) {
                    etDia.isErrorEnabled = false
                } else {
                    etDia.isErrorEnabled = true
                    etDia.error = getString(R.string.error_valid_dia)
                }
            }
        }

        etDia.editText?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (etDia.editText?.text.toString().trim().length == 0) {
                    etDia.error = getString(R.string.error_valid_dia)
                    etDia.isErrorEnabled = true
                } else {
                    etDia.isErrorEnabled = false
                }
                true
            }
            false
        }

        //body.text = title
        val saveBloodPressure = dialog.findViewById(R.id.saveBloodPressure) as TextView
        val cancelBloodPressure = dialog.findViewById(R.id.cancelBloodPressure) as TextView
        val cancelAction = dialog.findViewById(R.id.cancel_action) as ImageView
        saveBloodPressure.setOnClickListener {
            val sys = etSys.editText?.text.toString()
            if (sys.trim().length == 0) {
                etSys.isErrorEnabled = true
                etSys.error = getString(R.string.err_sys)
            } else {
                etSys.isErrorEnabled = false
            }

            val dia = etDia.editText?.text.toString()
            if (dia.trim().length == 0) {
                etDia.isErrorEnabled = true
                etDia.error = getString(R.string.error_valid_dia)
            } else {
                etDia.isErrorEnabled = false
            }

            if (sys.trim().length != 0 && dia.trim().length != 0) {

                Utills.hideKeyboard(requireActivity())
                showProgressBar()

                binding.tvBloodPressure.text =
                    (sys + "/" + dia) + " " + getString(R.string.bp_unit)
                dialog.window
                    ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
                Utills.hide(requireContext())

                updateObservationType(
                    UpdateManualObservations(
                        listOf<UpdatePainLevel>(
                            UpdatePainLevel(
                                patientId,
                                Constants.OBSERVATION_TYPE_BP_HIGH,
                                sys,
                                Utills.getCurrentDate()
                            ),
                            UpdatePainLevel(
                                patientId,
                                Constants.OBSERVATION_TYPE_BP_LOW,
                                dia,
                                Utills.getCurrentDate()
                            )
                        )
                    )
                )
                dialog.dismiss()

            }
        }


        //    cancelAction.setOnClickListener {}
        cancelAction.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                Utills.hide(requireContext())

                dialog.dismiss()

                return v?.onTouchEvent(event) ?: true
            }
        })
        cancelBloodPressure.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                Utills.hide(requireContext())

                dialog.dismiss()
                return v?.onTouchEvent(event) ?: true
            }
        })
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
                else if (it.data?.statusCode == 401) {
                    mPatientDetailViewModel.refreshToken()

                    Handler().postDelayed({
                        mPatientDetailViewModel.getPatientDetails(patientId = patientId.toString())
                    }, Constants.DELAY_IN_API_CALL)

                } else showError(it.data?.message!!)

            }
            Resource.Status.ERROR -> showError(it.exception!!)
        }
    }

    private fun handlePatientObservations(it: Resource<PatientObservations>) {

        when (it.status) {
            //Resource.Status.LOADING -> showProgressBar()
            Resource.Status.SUCCESS -> {
                hideProgressbar()
                if (it.data?.statusCode == 200 && it.data.body != null) showPatientObserVations(it.data.body)
                else if (it.data?.statusCode == 401) {
                    mPatientDetailViewModel.refreshToken()

                    Handler().postDelayed({
                        mPatientDetailViewModel.getPatientObservations(patientId = patientId.toString())

                    }, Constants.DELAY_IN_API_CALL)
                } else showError(it.data?.message!!)

            }
            Resource.Status.ERROR -> showError(it.exception!!)
        }
    }

    private fun showPatientObserVations(data: PatientObservation) {
        // formatString("103.012955")
        Log.e("observations", "${Gson().toJson(data)}")
        binding.tvHeartRateValue.text = Utills.round(data.heartRate)
        binding.tvRespirationRateValue.text = Utills.round(data.respirationRate)
        binding.tvBodyTempratureValue.text = Utills.round(data.bodyTemprature)
        binding.tvSpO2.text = data.spo2 + " " + getString(R.string.spo2_unit)
        binding.tvBloodPressure.text =
            (data.bpHigh + "/" + data.bpLow) + " " + getString(R.string.bp_unit)
        var painLl = data.painLevel
        Log.e("painLevelesdsd", "${data.painLevel}")
        if (painLl != null) {
            Log.e("painLl", "$painLl")
            painLevel = painLl
        } else {
            painLevel = "0"
        }
        if (data.status.toUpperCase().equals(Constants.STATE_RECOVERED)) {
            binding.tvPatientStatus.background = activity?.getDrawable(R.drawable.recovered_bg)
        }

        if (data.status.toUpperCase().equals(Constants.STATE_UNDER_CONTROL)) {
            binding.tvPatientStatus.background = activity?.getDrawable(R.drawable.under_control_bg)
        }

        if (data.status.toUpperCase().equals(Constants.STATE_CRITICAL)) {
            binding.tvPatientStatus.background = activity?.getDrawable(R.drawable.critical_bg)
        }

        binding.tvPatientStatus.text = data.status

    }

    private fun showPatientDeatils(data: Details) {
        patientName = data.firstName + " " + data.lastName
        Glide.with(requireActivity()).load(data.imageUrl).placeholder(R.drawable.ic_placeholder)
            .into(binding.imgPatientProfile)

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