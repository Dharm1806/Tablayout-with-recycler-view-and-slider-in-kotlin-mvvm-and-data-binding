package com.tekmindz.covidhealthcare.ui.search

import android.app.ProgressDialog
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.tekmindz.covidhealthcare.R
import com.tekmindz.covidhealthcare.constants.Constants
import com.tekmindz.covidhealthcare.databinding.FragmentTabItemBinding
import com.tekmindz.covidhealthcare.databinding.SearchFragmentBinding
import com.tekmindz.covidhealthcare.repository.responseModel.DashboardObservationsResponse
import com.tekmindz.covidhealthcare.utills.ResponseList
import com.tekmindz.covidhealthcare.utills.Utills

class SearchFragment : Fragment() , OnItemClickListener {
    private lateinit var binding: SearchFragmentBinding

    companion object {
        fun newInstance() = SearchFragment()
    }

    private lateinit var mSearchViewModel: SearchViewModel
    private var mProgressDialog: ProgressDialog? = null
    private lateinit var mSearchAdapter: SearchAdapter
    private  var mSearchList = ArrayList<DashboardObservationsResponse>()
    private var fromTime:String? = null
    private var toTime:String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.search_fragment, container, false
        )
        val view: View = binding.getRoot()
        binding.setLifecycleOwner(this);

        return view
        // return inflater.inflate(R.layout.search_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mProgressDialog = activity?.let { Utills.initializeProgressBar(it) }
        mSearchViewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)
        binding.searchViewModel = mSearchViewModel
        binding.searchView.isIconified= false
        arguments?.takeIf { it.containsKey(Constants.ARG_TIME) }?.apply {
            val hours = getInt(Constants.ARG_TIME)
            fromTime = Utills.getStartDate(hours)
            toTime = Utills.getCurrentDate()


            /*mDashboardViewModel.getDashboardObservations(
                DashBoardObservations(
                    Utills.getStartDate(
                        hours
                    ), Utills.getCurrentDate()
                )
            )*/



        }


        mSearchAdapter = SearchAdapter(mSearchList, this, requireActivity())
        binding.searchList.layoutManager = LinearLayoutManager(requireActivity())
        binding.searchList.adapter = mSearchAdapter


        mSearchViewModel.response().observe(requireActivity()!!, Observer {
            when (it) {

                is ResponseList<DashboardObservationsResponse> -> {
                    handleObservations(it)
                }
            }
        })

    }

    private fun handleObservations(it: ResponseList<DashboardObservationsResponse>) {

        when (it.status) {
            //ResponseList.Status.LOADING -> showProgressBar()
            ResponseList.Status.SUCCESS -> showResults(it.data!!)
            ResponseList.Status.ERROR -> showError(it.exception!!)

        }
    }
    private fun showError(error: String) {
        showMessage(error)
    }
    private fun showMessage(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }
    private fun showResults(data: List<DashboardObservationsResponse>) {
        if (!data.isNullOrEmpty()) {
            mSearchList.clear()
            mSearchList.addAll(data)
            mSearchAdapter.notifyDataSetChanged()
        }
    }

    override fun onItemClicked(mDashboardObservationsResponse: DashboardObservationsResponse) {
        val bundle = bundleOf("patientId" to mDashboardObservationsResponse.patientId.toInt())
        findNavController().navigate(R.id.homeToPatientDetails, bundle)
    }


}