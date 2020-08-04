package com.tekmindz.covidhealthcare.ui.patientAnalytics

import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.EntryXComparator
import com.google.android.material.datepicker.MaterialDatePicker
import com.tekmindz.covidhealthcare.R
import com.tekmindz.covidhealthcare.constants.Constants
import com.tekmindz.covidhealthcare.constants.Constants.ARG_PATIENT_NAME
import com.tekmindz.covidhealthcare.constants.Constants.ARG_TIME
import com.tekmindz.covidhealthcare.constants.Constants.PATIENT_ID
import com.tekmindz.covidhealthcare.constants.UserTypes
import com.tekmindz.covidhealthcare.databinding.FragmentAnalyticsTabBinding
import com.tekmindz.covidhealthcare.repository.requestModels.PatientAnalyticsRequest
import com.tekmindz.covidhealthcare.repository.responseModel.Analytics
import com.tekmindz.covidhealthcare.repository.responseModel.AnalyticsResponse
import com.tekmindz.covidhealthcare.utills.Resource
import com.tekmindz.covidhealthcare.utills.Utills
import kotlinx.android.synthetic.main.fragment_analytics_tab.*
import kotlinx.android.synthetic.main.fragment_tab_item.select_date
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class AnalyticsTabFragment : Fragment() {
    private lateinit var patientAnalyticsRequest: PatientAnalyticsRequest
    private var patientId: String = "0"
    private var hours: Int = 3
    private lateinit var binding: FragmentAnalyticsTabBinding

    var posture: ArrayList<String>? = null
    var postureYaxis: ArrayList<String>? = null
    var tempGraphEntry: ArrayList<Entry>? = null
    var helthGraphEntry: ArrayList<Entry>? = null
    var heartGraphEntry: ArrayList<Entry>? = null
    var respirationGraphEntry: ArrayList<Entry>? = null

    private lateinit var mAnalyticsViewModel: AnalyticsViewModel
    private var mProgressDialog: ProgressDialog? = null
    private var mAnalyticsList = ArrayList<Analytics>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_analytics_tab, container, false
        )
        val view: View = binding.root
        binding.lifecycleOwner = this
        setHasOptionsMenu(true)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mProgressDialog =
            activity?.let { Utills.initializeProgressBar(it, R.style.AppTheme_WhiteAccent) }

        mAnalyticsViewModel = ViewModelProviders.of(this).get(AnalyticsViewModel::class.java)

        binding.patientAnalytics = (mAnalyticsViewModel)
        posture = ArrayList<String>()
        postureYaxis = ArrayList<String>()
        postureYaxis?.add(getString(R.string.posture_sitting).capitalize())
        postureYaxis?.add(getString(R.string.posture_sleeping).capitalize())
        postureYaxis?.add(getString(R.string.posture_standing).capitalize())
        postureYaxis?.add(getString(R.string.posture_lying).capitalize())
        postureYaxis?.add(getString(R.string.posture_motion).capitalize())
        //for graph entry
        tempGraphEntry = ArrayList()
        helthGraphEntry = ArrayList()
        heartGraphEntry = ArrayList()
        respirationGraphEntry = ArrayList()

        binding.selectDate.setOnClickListener { showDateRangePicker() }
        Log.e("patientNAme", "${arguments?.getString(ARG_PATIENT_NAME)}")
        binding.patientName.text = arguments?.getString(ARG_PATIENT_NAME)
        arguments?.takeIf { it.containsKey(ARG_TIME) }?.apply {
            hours = getInt(ARG_TIME)

            patientId = getString(PATIENT_ID)!!
            if (hours == -1) {
                binding.selectDate.visibility = View.VISIBLE
                showDateRangePicker()

            } else {
                Utills.dateRange(Constants.DATE_RANGE)

                binding.selectDate.visibility = View.GONE

                patientAnalyticsRequest = PatientAnalyticsRequest(
                    patientId,
                    Utills.getStartDate(hours),
                    Utills.getCurrentDate()
                )
                getPatientAnalytics()

            }
        }


        // binding.listPatient.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)

        mAnalyticsViewModel.response().observe(requireActivity(), Observer {
            when (it) {

                is Resource<AnalyticsResponse> -> {
                    handleObservations(it)
                }
            }
        })
        if (mAnalyticsViewModel.getUserType() == UserTypes.PATIENT.toString()) {
            binding.btSos.visibility = View.VISIBLE
        } else {
            binding.btSos.visibility = View.GONE
        }
        binding.btSos.setOnClickListener { Utills.callPhoneNumber(requireActivity()) }

    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val item = menu.findItem(R.id.updateProfile)
        if (item != null) item.isVisible = false
    }

    private fun showDateRangePicker() {
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        //builder.setTheme(R.style.TimePickerTheme)
        val now = Calendar.getInstance()
        builder.setSelection(androidx.core.util.Pair(now.timeInMillis, now.timeInMillis))
        val picker = builder.build()
        picker.show(activity?.supportFragmentManager!!, picker.toString())
        picker.addOnNegativeButtonClickListener {
            // Utills.dateRange(Constants.DATE_RANGE)
            picker.dismiss()
        }
        picker.addOnPositiveButtonClickListener {
            val fromDate = Utills.getDate(it.first!!)
            val toDate = Utills.getDate(it.second!!)
            select_date.text = Constants.parseDate(fromDate) + " - " + Constants.parseDate(toDate)
            patientAnalyticsRequest = PatientAnalyticsRequest(
                patientId,
                fromDate,
                toDate
            )
            Utills.dateRange(Constants.parseDate(fromDate) + " - " + Constants.parseDate(toDate))

            getPatientAnalytics()

        }

    }

    fun getPatientAnalytics() {
        if (Utills.verifyAvailableNetwork(requireActivity()))
            mAnalyticsViewModel.getPatientAnalytics(
                patientAnalyticsRequest
            )
    }

    private fun handleObservations(it: Resource<AnalyticsResponse>) {

        when (it.status) {
            Resource.Status.LOADING -> showProgressBar()
            Resource.Status.SUCCESS -> {
                if (it.data?.statusCode == 200 && it.data.body != null) showObservations(it.data.body)
                else if (it.data?.statusCode == 401) {
                    mAnalyticsViewModel.refreshToken()

                    Handler().postDelayed({
                        getPatientAnalytics()
                    }, Constants.DELAY_IN_API_CALL)

                }
            }
            Resource.Status.ERROR -> showError(it.exception!!)

        }
    }


    private fun showObservations(data: List<Analytics>) {
Log.e("data", "123 t ${data.size}")
        if (!data.isNullOrEmpty()) {
            mAnalyticsList.clear()
            posture?.clear()
            tempGraphEntry?.clear()
            heartGraphEntry?.clear()
            respirationGraphEntry?.clear()
            helthGraphEntry?.clear()
            mAnalyticsList.addAll(data)
            filterData(mAnalyticsList)
        }
    }

    private fun filterData(mAnalyticsList: java.util.ArrayList<Analytics>) {
        mAnalyticsList.forEach {
            if (posture != null && posture?.size != 0 && it.posture in posture!!) {
            } else {
                posture?.add(it.posture)
            }
            var time = mAnalyticsViewModel.getTimeFloat(it.observationDateTime, requireActivity())
            tempGraphEntry!!.add(Entry(time, it.bodyTemprature))
            heartGraphEntry!!.add((Entry(time, it.heartRate)))
            respirationGraphEntry!!.add(Entry(time, it.respirationRate))
            helthGraphEntry!!.add(
                Entry(
                    time,
                    mAnalyticsViewModel.getIntPosture(it.posture, requireActivity())
                )
            )

        }
        setTempGraph(temp_chart, tempGraphEntry!!)
        setTempGraph(heart_rate_chart, heartGraphEntry!!)
        setTempGraph(respiration_rate_chart, respirationGraphEntry!!)
        settingYLevel(helthGraphEntry!!)

    }


    private fun setTempGraph(
        mychart: LineChart,
        graphEntry: ArrayList<Entry>
    ) {

        Collections.sort(graphEntry, EntryXComparator())
        var lineDataSet = LineDataSet(graphEntry, "")
        //setting draw values
        lineDataSet.setDrawValues(false)
        //setting line width
        lineDataSet.lineWidth = 2f

        // holo circle
        lineDataSet.setDrawCircleHole(true)
        //circle radius
        lineDataSet.circleRadius = 5f
        //circle holo radius
        lineDataSet.circleHoleRadius = 3f
        val color = getColorGraph(mychart)
        //seting line color
        lineDataSet.color = color
        //crircle color
        lineDataSet.setCircleColor(color)


        var lineDataSetList = ArrayList<LineDataSet>()
        lineDataSetList.add(lineDataSet)
        var lineData = LineData(lineDataSetList as List<ILineDataSet>?)
        mychart.data = lineData
        //mychart.getAxisLeft().setDrawGridLines(false);
        mychart.xAxis.setDrawGridLines(false)
        mychart.axisRight.isEnabled = false
        mychart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        mychart.description.isEnabled = false
        mychart.legend.isEnabled = false
        mychart.animate()

        //formating x axis label
        mychart.xAxis.valueFormatter = object : ValueFormatter() {

            private val mFormat: SimpleDateFormat = SimpleDateFormat(getString(R.string.graph_time))

            override fun getFormattedValue(value: Float): String? {

                val millis: Long = TimeUnit.HOURS.toMillis(value.toLong())
                mFormat.timeZone = TimeZone.getTimeZone(getString(R.string.timezone))
                return mFormat.format(Date(millis))
            }
        }

        mychart.invalidate()
    }

    private fun getColorGraph(mychart: LineChart): Int {
        if (mychart.id == R.id.heart_rate_chart) {
            return requireActivity().resources.getColor(R.color.heart_rate_color)
        } else if (mychart.id == R.id.temp_chart) {
            return requireActivity().resources.getColor(R.color.body_temp_color)
        } else {
            return requireActivity().resources.getColor(R.color.respiration_color)
        }


    }

    private fun settingYLevel(heartEntry: ArrayList<Entry>) {

        Collections.sort(heartEntry, EntryXComparator())
        var lineDataSet = LineDataSet(heartEntry, "")
        //setting draw values
        lineDataSet.setDrawValues(false)
        //setting line width
        lineDataSet.lineWidth = 2f
        //seting line color
        lineDataSet.color = requireActivity().resources.getColor(R.color.health_alert_color)
        // holo circle
        lineDataSet.setDrawCircleHole(true)
        //circle radius
        lineDataSet.circleRadius = 5f
        //circle holo radius
        lineDataSet.circleHoleRadius = 3f
        //crircle color

        lineDataSet.setCircleColor(requireActivity().resources.getColor(R.color.health_alert_color))


        var lineDataSetList = ArrayList<LineDataSet>()
        lineDataSetList.add(lineDataSet)
        var lineData = LineData(lineDataSetList as List<ILineDataSet>?)
        helth_chart.data = lineData
        //mychart.getAxisLeft().setDrawGridLines(false);
        helth_chart.xAxis.setDrawGridLines(false)
        helth_chart.axisRight.isEnabled = false

        //mychart.getAxisRight().setDrawGridLines(false);
        //mychart.axisRight.setDrawGridLines(false);
        //mychart.setDrawGridBackground(false)

        //Uncomment to show Y axis lables in Text
        val yAxis = helth_chart.axisLeft
        yAxis.labelCount = 5
        yAxis.axisMinimum = 0f
        yAxis.mAxisMaximum = 5f


        yAxis.valueFormatter = IndexAxisValueFormatter(postureYaxis)
        // yAxis.axisMaximum=posture!!.size.toFloat()
        //yAxis.axisMinimum=0f
        // yAxis.mAxisRange = 5f

        helth_chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        helth_chart.description.isEnabled = false
        helth_chart.legend.isEnabled = false
        helth_chart.xAxis.valueFormatter = object : ValueFormatter() {
            private val mFormat: SimpleDateFormat =
                SimpleDateFormat(getString(R.string.graph_time))

            override fun getFormattedValue(value: Float): String? {
                val millis: Long = TimeUnit.HOURS.toMillis(value.toLong())
                mFormat.timeZone = TimeZone.getTimeZone(getString(R.string.timezone))

                return mFormat.format(Date(millis))
            }

        }
        helth_chart.invalidate()
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