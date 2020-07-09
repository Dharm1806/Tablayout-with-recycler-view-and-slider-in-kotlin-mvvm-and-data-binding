package com.tekmindz.covidhealthcare.ui.dashboard

import android.app.ProgressDialog
import android.os.Bundle
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
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.gson.Gson
import com.tekmindz.covidhealthcare.R
import com.tekmindz.covidhealthcare.constants.Constants
import com.tekmindz.covidhealthcare.constants.Constants.ARG_TIME
import com.tekmindz.covidhealthcare.databinding.FragmentTabItemBinding
import com.tekmindz.covidhealthcare.repository.requestModels.DashBoardObservations
import com.tekmindz.covidhealthcare.repository.requestModels.DateFilter
import com.tekmindz.covidhealthcare.repository.responseModel.DashboardCounts
import com.tekmindz.covidhealthcare.repository.responseModel.DashboardObservationsResponse
import com.tekmindz.covidhealthcare.repository.responseModel.observations
import com.tekmindz.covidhealthcare.utills.Resource
import com.tekmindz.covidhealthcare.utills.Utills
import kotlinx.android.synthetic.main.fragment_tab_item.*
import java.util.*
import kotlin.collections.ArrayList


class DashboardItemFragment : Fragment(), OnItemClickListener {
    private var hours: Int = 3
    private lateinit var binding: FragmentTabItemBinding

    private lateinit var mDashboardViewModel: DashboardViewModel
    private var mProgressDialog: ProgressDialog? = null
    private lateinit var mObservationAdapter: ObaserVationsAdapter
    private var mObservationList = ArrayList<observations>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_tab_item, container, false
        )
        val view: View = binding.root
        binding.lifecycleOwner = this

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mProgressDialog =
            activity?.let { Utills.initializeProgressBar(it, R.style.AppTheme_WhiteAccent) }

        mDashboardViewModel = ViewModelProviders.of(this).get(DashboardViewModel::class.java)

        binding.dashboardViewModel = (mDashboardViewModel)
        binding.selectDate.setOnClickListener { showDateRangePicker() }

        arguments?.takeIf { it.containsKey(ARG_TIME) }?.apply {
            hours = getInt(ARG_TIME)
            Log.e("hours", "$hours")
            if (hours == -1) {
                binding.selectDate.visibility = View.VISIBLE
                showDateRangePicker()

            } else {
                Utills.dateRange(Constants.DATE_RANGE)
                binding.selectDate.visibility = View.GONE
                getDashBoardObservation(
                    DashBoardObservations(
                        false, DateFilter(
                            Utills.getStartDate(
                                hours
                            ), Utills.getCurrentDate()
                        )
                    )
                )
                getDashboardCount(
                    DateFilter(
                        Utills.getStartDate(hours),
                        Utills.getCurrentDate()
                    )
                )
            }
        }

        mObservationAdapter = ObaserVationsAdapter(mObservationList, this, requireActivity())
        binding.listPatient.adapter = mObservationAdapter

        mDashboardViewModel.response().observe(requireActivity(), Observer {
            when (it) {
                is Resource<DashboardObservationsResponse> -> {
                    Log.e("it", "${Gson().toJson(it.data)}")
                    handleObservations(it)
                }
            }
        })

        mDashboardViewModel.dashBoardCounts().observe(requireActivity(), Observer {
            when (it) {
                is Resource<DashboardCounts> -> {
                    handleCounts(it)
                }
            }
        })

        binding.searchPatient.setOnClickListener {
            val bundle = bundleOf(Constants.ARG_TIME to hours)
            findNavController().navigate(R.id.homeToSearch, bundle)
        }

    }

    private fun showDateRangePicker() {
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        val now = Calendar.getInstance()
        builder.setSelection(androidx.core.util.Pair(now.timeInMillis, now.timeInMillis))
        val picker = builder.build()
        picker.show(activity?.supportFragmentManager!!, picker.toString())
        picker.addOnNegativeButtonClickListener {
            picker.dismiss()
            Utills.dateRange(Constants.DATE_RANGE)
        }
        picker.addOnPositiveButtonClickListener {
            val fromDate = Utills.getDate(it.first!!)
            val toDate = Utills.getDate(it.second!!)
            select_date.text = Constants.parseDate(fromDate) + " - " + Constants.parseDate(toDate)
            getDashBoardObservation(
                DashBoardObservations(
                    false, DateFilter(
                        fromDate, toDate
                    )
                )
            )
            getDashboardCount(
                DateFilter(
                    fromDate,
                    toDate
                )
            )
            Utills.dateRange(Constants.parseDate(fromDate) + " - " + Constants.parseDate(toDate))
        }

    }

    fun getDashBoardObservation(dashBoardObservations: DashBoardObservations) {
        if (Utills.verifyAvailableNetwork(activity = requireActivity())) {
            mDashboardViewModel.getDashboardObservations(
                dashBoardObservations
            )
        }


    }

    fun getDashboardCount(dateFilter: DateFilter) {
        if (Utills.verifyAvailableNetwork(activity = requireActivity())) {
            mDashboardViewModel.getDashBoardCounts(
                dateFilter
            )
        }
    }

    private fun handleObservations(it: Resource<DashboardObservationsResponse>) {

        when (it.status) {
            Resource.Status.LOADING -> showProgressBar()
            Resource.Status.SUCCESS -> {
                // showObservations(it.data?.body!!)

                if (it.data?.statusCode == 200) {
                    showObservations(it.data.body)
                } else {
                    showError(it.data?.message!!)
                }
            }
            Resource.Status.ERROR -> showError(it.exception!!)

        }
    }

    private fun handleCounts(it: Resource<DashboardCounts>) {

        when (it.status) {
            Resource.Status.LOADING -> showProgressBar()
            Resource.Status.SUCCESS -> {
                if (it.data?.statusCode != 200 && it.data?.body != null) {
                    showError(it.data.message)
                } else {
                    showCounts(it.data!!)
                }
            }
            Resource.Status.ERROR -> showError(it.exception!!)
        }
    }

    private fun showCounts(data: DashboardCounts) {

        val mCasesList: ArrayList<PieEntry> = ArrayList()

        mCasesList.add(PieEntry(data.body.critical.toFloat(), getString(R.string.msg_critical)))
        mCasesList.add(
            PieEntry(
                data.body.underControl.toFloat(),
                getString(R.string.under_control)
            )
        )
        mCasesList.add(PieEntry(data.body.recovered.toFloat(), getString(R.string.msg_receovered)))
        val pieDataSet = PieDataSet(mCasesList, "")
        pieDataSet.setColors(intArrayOf(R.color.red, R.color.amber, R.color.green), context)
        pieDataSet.setDrawValues(false)
        pieDataSet.sliceSpace = 2f
        pieDataSet.setDrawIcons(false)

        val pieData = PieData(pieDataSet)
        binding.graphTotalPatient.data = pieData
        binding.graphTotalPatient.setDrawEntryLabels(false)
        binding.graphTotalPatient.setDrawCenterText(true)
        binding.graphTotalPatient.isDrawHoleEnabled = true
        binding.graphTotalPatient.holeRadius = 60f
        binding.graphTotalPatient.centerText = "Total\n" +
                " ${data.body.total}"
        binding.graphTotalPatient.setCenterTextSize(21f)
        binding.graphTotalPatient.setCenterTextColor(R.color.dashboard_text_color)

        binding.graphTotalPatient.animateXY(500, 500)
        binding.graphTotalPatient.description.isEnabled = false

        val legend: Legend = binding.graphTotalPatient.legend
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.xEntrySpace = 20f
        legend.yEntrySpace = 20f
        legend.textSize = 12f
        legend.textColor = R.color.legend_text_color
        legend.setDrawInside(false)
    }


    private fun showObservations(data: List<observations>) {
        Log.e("size", "${data.size}")
        if (!data.isNullOrEmpty()) {
            mObservationList.clear()
            mObservationList.addAll(data)
            mObservationAdapter.notifyDataSetChanged()
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

    override fun onItemClicked(mDashboardObservationsResponse: observations) {
        val bundle = bundleOf("patientId" to mDashboardObservationsResponse.patientId.toInt())
        findNavController().navigate(R.id.homeToPatientDetails, bundle)
    }

}