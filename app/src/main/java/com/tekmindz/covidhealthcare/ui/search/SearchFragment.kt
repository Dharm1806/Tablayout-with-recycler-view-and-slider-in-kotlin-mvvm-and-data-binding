package com.tekmindz.covidhealthcare.ui.search

import android.app.Activity
import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
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
import com.google.gson.Gson
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
    lateinit var mQuery:String

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
                //  Log.e("querytext submit", "$query")

                mQuery = query.toString()
                searchQuery()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                //    Log.e("querytext change", "$newText")
              mQuery = newText.toString()

                searchQuery()
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

    private fun searchQuery() {
        Log.e("mark", "3434 $mQuery")
        if (mQuery.trim().length != 0) {

            if (Utills.verifyAvailableNetwork(requireActivity())) {
                mSearchViewModel.getSearchPatientResults(
                    SearchRequestModel(
                        true,
                        DateFilter(fromDateTime = fromTime!!, toDateTime = toTime!!),
                        mQuery
                    ), requireActivity()
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
            Resource.Status.SUCCESS -> {
                if (it.data?.statusCode == 200 && it.data.body != null) {
                    showResults(it.data.body)
                    binding.text1.visibility = View.GONE
                    binding.searchList.visibility = View.VISIBLE

                } else if (it.data?.statusCode == 401) {
                    binding.text1.visibility = View.GONE

                    mSearchViewModel.refreshToken(requireActivity())
                    Handler().postDelayed({
                        searchQuery()
                    }, Constants.DELAY_IN_API_CALL)

                } else if (it.data?.statusCode == 404) {
                    showError(it.data.message)
                    binding.text1.visibility = View.GONE
                    binding.searchList.visibility = View.GONE


                } else if (it.data?.body == null) {
                    binding.text1.visibility = View.VISIBLE
                    binding.searchList.visibility = View.GONE
                    // showError(getString(R.string.no_record_found))
                    handleUi()
                } else {
                    showError(it.data.message)
                }
                Log.e("datasearch", "${Gson().toJson(it)}")

            }
            Resource.Status.ERROR -> showError(it.exception!!)

        }
    }

    private fun handleUi() {
        mSearchList.clear()
        mSearchAdapter.notifyDataSetChanged()
    }

    private fun showError(error: String) {
        mSearchList.clear()
        mSearchAdapter.notifyDataSetChanged()
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
        }else{
            mSearchList.clear()
            mSearchAdapter.notifyDataSetChanged()
        }
    }

    override fun onItemClicked(mDashboardObservationsResponse: observations) {
        Utills.hideKeyboard(requireActivity())
        val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)

        val bundle = bundleOf("observation" to mDashboardObservationsResponse)
        findNavController().navigate(R.id.homeToPatientDetails, bundle)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val item = menu.findItem(R.id.updateProfile)
        if (item != null) item.isVisible = false


    }

}