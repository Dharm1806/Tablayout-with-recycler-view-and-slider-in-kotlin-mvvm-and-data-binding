package com.tekmindz.covidhealthcare.ui.dashboard

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.gson.Gson
import com.tekmindz.covidhealthcare.R
import com.tekmindz.covidhealthcare.constants.Constants.ARG_TIME
import com.tekmindz.covidhealthcare.repository.requestModels.DashBoardObservations
import com.tekmindz.covidhealthcare.repository.responseModel.DashboardCounts
import com.tekmindz.covidhealthcare.repository.responseModel.DashboardObservationsResponse
import com.tekmindz.covidhealthcare.utills.Resource
import com.tekmindz.covidhealthcare.utills.ResponseList
import com.tekmindz.covidhealthcare.utills.Utills
import kotlinx.android.synthetic.main.fragment_tab_item.*
import kotlinx.android.synthetic.main.item_critical_patient_list.*


class TabItemFragment : Fragment() ,OnItemClickListener{

    private lateinit var mDashboardViewModel: DashboardViewModel
    private var mProgressDialog: ProgressDialog? = null
    private lateinit var mObservationAdapter: ObaserVationsAdapter
    private  var mObservationList = ArrayList<DashboardObservationsResponse>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_tab_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mProgressDialog = activity?.let { Utills.initializeProgressBar(it) }

        mDashboardViewModel = ViewModelProviders.of(this).get(DashboardViewModel::class.java)

        arguments?.takeIf { it.containsKey(ARG_TIME) }?.apply {
            val hours = getInt(ARG_TIME)

            mDashboardViewModel.getDashboardObservations(
                DashBoardObservations(
                    Utills.getStartDate(
                        hours
                    ), Utills.getCurrentDate()
                )
            )
            text1.text = getString(R.string.app_name) + " " + getInt(ARG_TIME).toString()
            mDashboardViewModel.getDashBoardCounts(
                DashBoardObservations(
                    Utills.getStartDate(hours),
                    Utills.getCurrentDate()
                )
            )

        }

        list_patient.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        mObservationAdapter = ObaserVationsAdapter(mObservationList, this, requireActivity())
        list_patient.adapter = mObservationAdapter

        mDashboardViewModel.response().observe(requireActivity()!!, Observer {
            when (it) {

                is ResponseList<DashboardObservationsResponse> -> {
                    handleObservations(it)
                }
            }
        })

        mDashboardViewModel.dashBoardCounts().observe(requireActivity()!!, Observer {
            when (it) {
                is Resource<DashboardCounts> -> {
                    handleCounts(it)
                }
            }
        })
    }

    private fun handleObservations(it: ResponseList<DashboardObservationsResponse>) {

        when (it.status) {
            ResponseList.Status.LOADING -> showProgressBar()
            ResponseList.Status.SUCCESS -> showObservations(it.data!!)
            ResponseList.Status.ERROR -> showError(it.exception!!)

        }
    }

    private fun handleCounts(it: Resource<DashboardCounts>) {

        when (it.status) {
            Resource.Status.LOADING -> showProgressBar()
            Resource.Status.SUCCESS -> showCounts(it.data!!)
            Resource.Status.ERROR -> showError(it.exception!!)
        }
    }

    private fun showCounts(data: DashboardCounts) {
        Log.e("Countsdata", "${Gson().toJson(data)}")

        val mCasesList: ArrayList<PieEntry> = ArrayList()

        mCasesList.add(PieEntry(data.critical.toFloat(), getString(R.string.msg_critical)))
        mCasesList.add(PieEntry(data.underControl.toFloat(), getString(R.string.under_control)))
        mCasesList.add(PieEntry(data.recovered.toFloat(), getString(R.string.msg_receovered)))
        val pieDataSet = PieDataSet(mCasesList, "")
        pieDataSet.setColors(intArrayOf(R.color.red,R.color.amber, R.color.green), context)
        pieDataSet.setDrawValues(false)
        pieDataSet.sliceSpace = 2f
        pieDataSet.setDrawIcons(false)

        val pieData = PieData(pieDataSet)
        graph_total_patient.data = pieData
        graph_total_patient.setDrawEntryLabels(false)
        graph_total_patient.setDrawCenterText(true)
        graph_total_patient.isDrawHoleEnabled = true
        graph_total_patient.holeRadius =60f
        graph_total_patient.centerText = "Total\n" +
                " ${data.total}"
        graph_total_patient.setCenterTextSize(21f)
        graph_total_patient.setCenterTextColor(R.color.dashboard_text_color)

        graph_total_patient.animateXY(500, 500)
        graph_total_patient.description.isEnabled  = false

        val legend: Legend = graph_total_patient.getLegend()
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.xEntrySpace = 20f
        legend.yEntrySpace = 20f
        legend.textSize = 12f
        legend.textColor = R.color.legend_text_color
        legend.setDrawInside(false)
    }


    private fun showObservations(data: List<DashboardObservationsResponse>) {
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

    override fun onItemClicked(mDashboardObservationsResponse: DashboardObservationsResponse) {
        val bundle = bundleOf("patientId" to mDashboardObservationsResponse.patientId.toInt())
        findNavController().navigate(R.id.homeToPatientDetails, bundle)
    }
}