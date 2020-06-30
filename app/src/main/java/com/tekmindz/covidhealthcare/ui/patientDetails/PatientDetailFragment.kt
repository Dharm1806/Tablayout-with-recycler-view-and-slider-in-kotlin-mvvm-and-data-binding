package com.tekmindz.covidhealthcare.ui.patientDetails

import android.app.ProgressDialog
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.tekmindz.covidhealthcare.R
import com.tekmindz.covidhealthcare.constants.Constants
import com.tekmindz.covidhealthcare.databinding.PatientDetailFragmentBinding


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
            Log.e("patientID", "$patientId")
            mPatientDetailViewModel.getPatientDetails(patientId = patientId.toString())
        }
    }
}