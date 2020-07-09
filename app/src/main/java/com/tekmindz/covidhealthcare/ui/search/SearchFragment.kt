package com.tekmindz.covidhealthcare.ui.search

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.tekmindz.covidhealthcare.R
import com.tekmindz.covidhealthcare.constants.Constants
import com.tekmindz.covidhealthcare.databinding.SearchFragmentBinding
import com.tekmindz.covidhealthcare.repository.requestModels.DateFilter
import com.tekmindz.covidhealthcare.repository.requestModels.SearchRequestModel
import com.tekmindz.covidhealthcare.repository.responseModel.DashboardObservationsResponse
import com.tekmindz.covidhealthcare.repository.responseModel.observations
import com.tekmindz.covidhealthcare.utills.Resource
import com.tekmindz.covidhealthcare.utills.Utills

class SearchFragment : Fragment(), OnItemClickListener {
    private lateinit var binding: SearchFragmentBinding

    companion object {
        fun newInstance() = SearchFragment()
    }

    private lateinit var mSearchViewModel: SearchViewModel
    private var mProgressDialog: ProgressDialog? = null
    private lateinit var mSearchAdapter: SearchAdapter
    private var mSearchList = ArrayList<observations>()
    private var fromTime: String? = null
    private var toTime: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.search_fragment, container, false
        )

        val view: View = binding.root
        binding.lifecycleOwner = this

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mProgressDialog =
            activity?.let { Utills.initializeProgressBar(it, R.style.AppTheme_WhiteAccent) }
        mSearchViewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)
        binding.searchViewModel = mSearchViewModel
        binding.searchView.isIconified = false
        arguments?.takeIf { it.containsKey(Constants.ARG_TIME) }?.apply {
            val hours = getInt(Constants.ARG_TIME)
            fromTime = Utills.getStartDate(hours)
            toTime = Utills.getCurrentDate()

        }
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchQuery(query.toString())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchQuery(newText.toString())
                return true
            }

        })
        mSearchAdapter = SearchAdapter(mSearchList, this, requireActivity())
        binding.searchList.layoutManager = LinearLayoutManager(requireActivity())
        binding.searchList.adapter = mSearchAdapter

        binding.back.setOnClickListener {
            findNavController().navigate(
                R.id.searchToHome, null, NavOptions.Builder()
                    .setPopUpTo(
                        R.id.search,
                        true
                    ).build()
            )
        }
        mSearchViewModel.response().observe(requireActivity(), Observer {
            when (it) {

                is Resource<DashboardObservationsResponse> -> {
                    handleObservations(it)
                }
            }
        })

    }

    private fun searchQuery(query: String) {
        if (query.trim().length != 0) {
            Log.e("mark", "$query")
            if (Utills.verifyAvailableNetwork(requireActivity())) {
                mSearchViewModel.getSearchPatientResults(
                    SearchRequestModel(
                        true,
                        DateFilter(fromDateTime = fromTime!!, toDateTime = toTime!!),
                        query
                    )
                )

            }
        } else {
            mSearchList.clear()
            mSearchAdapter.notifyDataSetChanged()
        }
    }

    private fun handleObservations(it: Resource<DashboardObservationsResponse>) {

        when (it.status) {
            //ResponseList.Status.LOADING -> showProgressBar()
            Resource.Status.SUCCESS ->
                if (it.data?.statusCode == 200 && it.data.body != null) {
                    showResults(it.data.body)
                } else showError(it.data?.message!!)
            Resource.Status.ERROR -> showError(it.exception!!)

        }
    }

    private fun showError(error: String) {
        showMessage(error)
    }

    private fun showMessage(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    private fun showResults(data: List<observations>) {
        if (!data.isNullOrEmpty()) {
            mSearchList.clear()
            mSearchList.addAll(data)
            mSearchAdapter.notifyDataSetChanged()
        }
    }

    override fun onItemClicked(mDashboardObservationsResponse: observations) {
        val bundle = bundleOf("patientId" to mDashboardObservationsResponse.patientId.toInt())
        findNavController().navigate(R.id.homeToPatientDetails, bundle)
    }


}