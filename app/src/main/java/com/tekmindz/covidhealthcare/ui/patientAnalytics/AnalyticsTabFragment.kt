package com.tekmindz.covidhealthcare.ui.patientAnalytics

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
import com.google.android.material.datepicker.MaterialDatePicker
import com.tekmindz.covidhealthcare.R
import com.tekmindz.covidhealthcare.constants.Constants
import com.tekmindz.covidhealthcare.constants.Constants.ARG_TIME
import com.tekmindz.covidhealthcare.constants.Constants.PATIENT_ID
import com.tekmindz.covidhealthcare.databinding.FragmentAnalyticsTabBinding
import com.tekmindz.covidhealthcare.repository.requestModels.DashBoardObservations
import com.tekmindz.covidhealthcare.repository.requestModels.DateFilter
import com.tekmindz.covidhealthcare.repository.requestModels.PatientAnalyticsRequest
import com.tekmindz.covidhealthcare.repository.responseModel.AnalyticsResponse
import com.tekmindz.covidhealthcare.utills.ResponseList
import com.tekmindz.covidhealthcare.utills.Utills
import kotlinx.android.synthetic.main.fragment_tab_item.*
import java.util.*
import kotlin.collections.ArrayList


class AnalyticsTabFragment : Fragment() {
    private var patientId: String = "0"
    private var hours: Int = 3
    private lateinit var binding: FragmentAnalyticsTabBinding

    //  private lateinit var binding: TabItemFragmentBinding
    private lateinit var mAnalyticsViewModel: AnalyticsViewModel
    private var mProgressDialog: ProgressDialog? = null
    private var mAnalyticsList = ArrayList<AnalyticsResponse>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_analytics_tab, container, false
        )
        val view: View = binding.getRoot()
        binding.setLifecycleOwner(this);

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mProgressDialog = activity?.let { Utills.initializeProgressBar(it, R.style.AppTheme_WhiteAccent) }

        mAnalyticsViewModel = ViewModelProviders.of(this).get(AnalyticsViewModel::class.java)

        binding.patientAnalytics = (mAnalyticsViewModel);
        binding.selectDate.setOnClickListener { showDateRangePicker() }

        arguments?.takeIf { it.containsKey(ARG_TIME) }?.apply {
             hours = getInt(ARG_TIME)
            patientId  =getString(PATIENT_ID)!!
            Log.e("hours", "$hours")
            if (hours == 0) {
                binding.selectDate.visibility = View.VISIBLE
                showDateRangePicker()

            } else {
                binding.selectDate.visibility = View.GONE

                getPatientAnalytics(PatientAnalyticsRequest(
                   patientId,
                    Utills.getStartDate(hours),
                    Utills.getCurrentDate()
                ))

            }
        }



        // binding.listPatient.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)

        mAnalyticsViewModel.response().observe(requireActivity()!!, Observer {
            when (it) {

                is ResponseList<AnalyticsResponse> -> {
                    handleObservations(it)
                }
            }
        })



    }

    private fun showDateRangePicker() {
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        //builder.setTheme(R.style.TimePickerTheme)
        val now = Calendar.getInstance()
        builder.setSelection(androidx.core.util.Pair(now.timeInMillis, now.timeInMillis))
        val picker = builder.build()
        picker.show(activity?.supportFragmentManager!!, picker.toString())
        picker.addOnNegativeButtonClickListener {
            picker.dismiss()
        }
        picker.addOnPositiveButtonClickListener {
            Log.e("date", "The selected date range is ${it.first} - ${it.second}")
            val fromDate = Utills.getDate(it.first!!)
            val toDate = Utills.getDate(it.second!!)
            select_date.text = Constants.parseDate(fromDate) +" - "+ Constants.parseDate(toDate)
            getPatientAnalytics(PatientAnalyticsRequest(
                patientId,
                    fromDate,
                   toDate
            ))

        }

    }

    fun getPatientAnalytics(patientAnalyticsRequest: PatientAnalyticsRequest){
        mAnalyticsViewModel.getPatientAnalytics(
           patientAnalyticsRequest
        )


    }

    private fun handleObservations(it: ResponseList<AnalyticsResponse>) {

        when (it.status) {
            ResponseList.Status.LOADING -> showProgressBar()
            ResponseList.Status.SUCCESS -> showObservations(it.data!!)
            ResponseList.Status.ERROR -> showError(it.exception!!)

        }
    }





    private fun showObservations(data: List<AnalyticsResponse>) {
        Log.e("observation", "${data.size}")
        if (!data.isNullOrEmpty()) {
            mAnalyticsList.clear()
            mAnalyticsList.addAll(data)
        }
    }


    private fun showProgressBar() {
        //mProgressDialog?.show()
    }

    private fun showError(error: String) {
        showMessage(error)
        hideProgressbar()
    }

    private fun hideProgressbar() {
        //  mProgressDialog?.hide()
    }

    private fun showMessage(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }



}