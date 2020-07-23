package com.tekmindz.covidhealthcare.ui.notifications

import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.tekmindz.covidhealthcare.R
import com.tekmindz.covidhealthcare.constants.Constants
import com.tekmindz.covidhealthcare.constants.UserTypes
import com.tekmindz.covidhealthcare.databinding.FragmentNotificationsBinding
import com.tekmindz.covidhealthcare.databinding.SearchFragmentBinding
import com.tekmindz.covidhealthcare.repository.requestModels.DateFilter
import com.tekmindz.covidhealthcare.repository.requestModels.SearchRequestModel
import com.tekmindz.covidhealthcare.repository.responseModel.DashboardObservationsResponse
import com.tekmindz.covidhealthcare.repository.responseModel.Notification
import com.tekmindz.covidhealthcare.repository.responseModel.NotificationResponse
import com.tekmindz.covidhealthcare.repository.responseModel.observations
import com.tekmindz.covidhealthcare.utills.Resource
import com.tekmindz.covidhealthcare.utills.Utills
import kotlinx.android.synthetic.main.fragment_analytics_tab.*

class NotificationFragment : Fragment(), OnItemClickListener {
    private lateinit var binding: FragmentNotificationsBinding

    companion object {
        fun newInstance() = NotificationFragment()
    }

    private lateinit var mNotificationViewModel: NotificationViewModel
    private var mProgressDialog: ProgressDialog? = null
    private lateinit var mNotificationAdapter: NotificationAdapter
    private var mNotificationList = ArrayList<Notification>()
   /* private var fromTime: String? = null
    private var toTime: String? = null
    lateinit var mQuery:String*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_notifications, container, false
        )

        val view: View = binding.root
        binding.lifecycleOwner = this
       // setHasOptionsMenu(true);

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mProgressDialog =
            activity?.let { Utills.initializeProgressBar(it, R.style.AppTheme_WhiteAccent) }
        mNotificationViewModel = ViewModelProviders.of(this).get(NotificationViewModel::class.java)
        binding.searchViewModel = mNotificationViewModel

        arguments?.takeIf { it.containsKey(Constants.ARG_TIME) }?.apply {
            val hours = getInt(Constants.ARG_TIME)
            /*fromTime = Utills.getStartDate(hours)
            toTime = Utills.getCurrentDate()*/

        }

        mNotificationAdapter = NotificationAdapter(mNotificationList, this, requireActivity())
        binding.notificationList.layoutManager = LinearLayoutManager(requireActivity())
        binding.notificationList.adapter = mNotificationAdapter


        mNotificationViewModel.response().observe(requireActivity(), Observer {
            when (it) {

                is Resource<NotificationResponse> -> {
                    handleObservations(it)
                }
            }
        })
        if(mNotificationViewModel.getUserType() == UserTypes.PATIENT.toString()){
            bt_sos.visibility = View.VISIBLE
        }else{
            bt_sos.visibility = View.GONE
        }
        binding.btSos.setOnClickListener { Utills.callPhoneNumber(requireActivity())
        }

    }

    private fun searchQuery() {
        mNotificationViewModel.getNotifications()
    }

    private fun handleObservations(it: Resource<NotificationResponse>) {

        when (it.status) {
            //ResponseList.Status.LOADING -> showProgressBar()
            Resource.Status.SUCCESS ->
                if (it.data?.statusCode == 200 && it.data.body != null) {
                    showResults(it.data.body)
                }
                else if (it.data?.statusCode == 401){
                    mNotificationViewModel.refreshToken()
                    Handler().postDelayed({
                        searchQuery()
                    }, Constants.DELAY_IN_API_CALL)

                }
            else showError(it.data?.message!!)
            Resource.Status.ERROR -> showError(it.exception!!)

        }
    }

    private fun showError(error: String) {
        showMessage(error)
    }

    private fun showMessage(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    private fun showResults(data: List<Notification>) {
        if (!data.isNullOrEmpty()) {
            mNotificationList.clear()
            mNotificationList.addAll(data)
            mNotificationAdapter.notifyDataSetChanged()
        }
    }

    public override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(
            (mBroadCastReceiver),
            IntentFilter(Constants.BROADCAST_RECEIVER_NAME)
        )
    }

   /* override fun onPrepareOptionsMenu(menu: Menu) {
        val item: MenuItem = menu.findItem(R.id.sos)
        item.isVisible = mNotificationViewModel.getUserType() == UserTypes.PATIENT.toString()
    }
*/
    @Override
    override fun onStop() {
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(mBroadCastReceiver)
        super.onStop()
    }
    override fun onItemClicked(mNotification: Notification) {
        Utills.hideKeyboard(requireActivity())

        val bundle = bundleOf("patientId" to mNotification.body.patientId.toInt())
        findNavController().navigate(R.id.homeToPatientDetails, bundle)
    }
    private val mBroadCastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {

            val patientId = intent.extras?.get(Constants.PATIENT_ID)
         Log.e("pataientId", "$patientId")
        }
    }

}