package com.tekmindz.covidhealthcare.ui.notifications

import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.tekmindz.covidhealthcare.R
import com.tekmindz.covidhealthcare.constants.Constants
import com.tekmindz.covidhealthcare.databinding.FragmentNotificationsBinding
import com.tekmindz.covidhealthcare.repository.responseModel.Body
import com.tekmindz.covidhealthcare.repository.responseModel.NotificationResponse
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
    private var mNotificationList = ArrayList<Body>()
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
        setHasOptionsMenu(true)

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
        fetchNotifications()

        mNotificationViewModel.response().observe(requireActivity(), Observer {
            when (it) {

                is Resource<NotificationResponse> -> {
                    handleObservations(it)
                }
            }
        })
        if (mNotificationViewModel.isPatient()) {
            bt_sos.visibility = View.VISIBLE
        } else {
            bt_sos.visibility = View.GONE
        }
        binding.btSos.setOnClickListener {
            Utills.callPhoneNumber(requireActivity())
        }

    }

    private fun fetchNotifications() = mNotificationViewModel.getNotifications(requireActivity())

    private fun handleObservations(it: Resource<NotificationResponse>) {

        when (it.status) {
            //ResponseList.Status.LOADING -> showProgressBar()
            Resource.Status.SUCCESS ->
                if (it.data?.statusCode == 200 && it.data.body != null) {
                    showResults(it.data.body)
                } else if (it.data?.statusCode == 401) {
                    mNotificationViewModel.refreshToken(requireActivity())
                    Handler().postDelayed({
                        fetchNotifications()
                    }, Constants.DELAY_IN_API_CALL)

                }else if (it.data?.body == null) {
                    showError(getString(R.string.no_record_found))
                } else showError(it.data.message)
            Resource.Status.ERROR -> showError(it.exception!!)

        }
    }

    private fun showError(error: String) {
        showMessage(error)
    }

    private fun showMessage(message: String) {
        Utills.showAlertMessage(requireActivity(), message)

        //    Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    private fun showResults(data: List<Body>) {
        if (!data.isNullOrEmpty()) {
            mNotificationList.clear()
            mNotificationList.addAll(data)
            mNotificationAdapter.notifyDataSetChanged()
        }
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(
            (mBroadCastReceiver),
            IntentFilter(Constants.BROADCAST_RECEIVER_NAME)
        )
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val item = menu.findItem(R.id.updateProfile)
        if (item != null) item.isVisible = false
    }

    @Override
    override fun onStop() {
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(mBroadCastReceiver)
        super.onStop()
    }

    override fun onItemClicked(body: Body) {

        Utills.hideKeyboard(requireActivity())
        Log.e("patientId", "${body.patientId},${Gson().toJson(body)}")
        val bundle = bundleOf("patientId" to body.patientId.toInt())
        findNavController().navigate(R.id.homeToPatientDetails, bundle)

    }

    private val mBroadCastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {

            val patientId = intent.extras?.get(Constants.PATIENT_ID)
            Log.e("pataientId", "$patientId")
            val obsType = intent.extras?.get(Constants.EXTRA_OBSERVATION_TYPE)
            val obsValue = intent.extras?.get(Constants.EXTRA_OBSERVATION_VALUE)
            val status = intent.extras?.get(Constants.EXTRA_STATUS)
            val patientName = intent.extras?.get(Constants.EXTRA_PATIENT_NAME)
            val wardNumber = intent.extras?.get(Constants.EXTRA_WARD_NUMBER)
            val bedNumber = intent.extras?.get(Constants.EXTRA_BED_NUMBER)
            val message = intent.extras?.get(Constants.EXTRA_MESSAGE)
            val notificationTime = intent.extras?.get(Constants.EXTRA_NOTIFICATION_TIME)
            val notificationId = intent.extras?.get(Constants.EXTRA_NOTIFICATION_ID)

            val body = Body(
                notificationId = notificationId.toString().toInt(),
                obsType = obsType.toString(),
                obsValue = obsValue.toString(),
                patientName = patientName.toString(),
                status = status.toString(),
                wardNumber = wardNumber.toString(),
                bedNumber = bedNumber.toString(),
                patientId = patientId.toString().toLong(),
                message = message.toString(),
                notificationTime = notificationTime.toString()
            )
            mNotificationList.add(body)
            mNotificationAdapter.notifyDataSetChanged()


        }
    }

}