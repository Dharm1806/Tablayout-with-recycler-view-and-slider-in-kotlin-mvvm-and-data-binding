package com.tekmindz.covidhealthcare.ui.dashboard

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
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

class TabItemFragment : Fragment() {
    private lateinit var mDashboardViewModel: DashboardViewModel
    private var mProgressDialog: ProgressDialog? = null
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
            Log.e("EndDAte", Utills.getCurrentDate())
            Log.e("StartDAte", Utills.getStartDate(hours))
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
            Resource.Status.LOADING -> showProgressBar()
            Resource.Status.SUCCESS -> showObservations(it.data!!)
            Resource.Status.ERROR -> showError(it.exception!!)

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
    }

    private fun showObservations(data: List<DashboardObservationsResponse>) {
        Log.e("data", "${data.size}")
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