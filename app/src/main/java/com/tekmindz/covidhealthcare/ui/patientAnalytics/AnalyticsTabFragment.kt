package com.tekmindz.covidhealthcare.ui.patientAnalytics

import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
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
import com.google.gson.Gson
import com.tekmindz.covidhealthcare.R
import com.tekmindz.covidhealthcare.constants.Constants
import com.tekmindz.covidhealthcare.constants.Constants.ARG_PATIENT_NAME
import com.tekmindz.covidhealthcare.constants.Constants.ARG_TIME
import com.tekmindz.covidhealthcare.constants.Constants.LABEL_COUNT
import com.tekmindz.covidhealthcare.constants.Constants.PATIENT_ID
import com.tekmindz.covidhealthcare.databinding.FragmentAnalyticsTabBinding
import com.tekmindz.covidhealthcare.repository.requestModels.PatientAnalyticsRequest
import com.tekmindz.covidhealthcare.repository.responseModel.Analytics
import com.tekmindz.covidhealthcare.repository.responseModel.AnalyticsResponse
import com.tekmindz.covidhealthcare.repository.responseModel.ECGResponse
import com.tekmindz.covidhealthcare.utills.Resource
import com.tekmindz.covidhealthcare.utills.Utills
import kotlinx.android.synthetic.main.fragment_analytics_tab.*
import kotlinx.android.synthetic.main.fragment_tab_item.select_date
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AnalyticsTabFragment : Fragment(), View.OnClickListener, View.OnTouchListener {
    private lateinit var fromDate: String
    private lateinit var toDate: String
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
    var ecgGraphEntry: ArrayList<Entry>? = null

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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        ecg_expand_icon.setOnTouchListener(this)
        ecg_collapse_icon.setOnTouchListener(this)
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        when (view) {
            ecg_collapse_icon -> {
                setECGVisible(false)
            }
            ecg_expand_icon -> {
                setECGVisible(true)
            }
        }
        return true
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
        ecgGraphEntry = ArrayList()

        binding.selectDate.setOnClickListener { showDateRangePicker() }
        //Log.e("patientNAme", "${arguments?.getString(ARG_PATIENT_NAME)}")
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
                toDate = Utills.getCurrentDate()
                fromDate = Utills.getStartDate(hours)
                patientAnalyticsRequest = PatientAnalyticsRequest(
                    patientId,
                    Utills.getStartDate(hours),
                    Utills.getCurrentDate()
                )
                Log.e("hours", "$hours")
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
        if (mAnalyticsViewModel.isPatient()) {
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
        builder.setCalendarConstraints(Utills.dateValidator(now.timeInMillis).build())
        builder.setSelection(androidx.core.util.Pair(now.timeInMillis, now.timeInMillis))

        val picker = builder.build()
        picker.show(activity?.supportFragmentManager!!, picker.toString())
        picker.addOnNegativeButtonClickListener {
            // Utills.dateRange(Constants.DATE_RANGE)
            picker.dismiss()
        }
        picker.addOnPositiveButtonClickListener {
            fromDate = Utills.getDate(it.first!!)
            toDate = Utills.getDate(it.second!!)
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
        if (Utills.verifyAvailableNetwork(requireActivity())) {
            mAnalyticsViewModel.getPatientAnalytics(
                patientAnalyticsRequest, requireActivity()
            )
            showProgressBar()
        }
    }

    private fun handleObservations(it: Resource<AnalyticsResponse>) {

        when (it.status) {
            Resource.Status.LOADING -> showProgressBar()
            Resource.Status.SUCCESS -> {
                hideProgressbar()

                if (it.data?.statusCode == 200 && it.data.body != null) {
                    showObservations(it.data.body)
                    showGraph(true)
                } else if (it.data?.statusCode == 401) {
                    mAnalyticsViewModel.refreshToken(requireActivity())

                    Handler().postDelayed({
                        getPatientAnalytics()
                    }, Constants.DELAY_IN_API_CALL)

                } else if (it.data?.body == null) {
                    showError(getString(R.string.no_record_found))
                    showGraph(false)
                } else {
                    showError(it.data.message)
                }
            }
            Resource.Status.ERROR -> showError(it.exception!!)

        }
    }

    /*function to show or hide view when no data available draw graph*/
    private fun showGraph(isShow: Boolean) {
        if (isShow) {
            health_alert.visibility = View.VISIBLE
            body_temp.visibility = View.VISIBLE
            heart_rate.visibility = View.VISIBLE
            respiration_rate.visibility = View.VISIBLE
        } else {
            health_alert.visibility = View.GONE
            body_temp.visibility = View.GONE
            heart_rate.visibility = View.GONE
            respiration_rate.visibility = View.GONE
        }
    }

    private fun showObservations(data: List<Analytics>) {
        //  Log.e("data", "123 t ${data.size}")
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
//Log.e("filter data set", "analytics")
        mAnalyticsList.forEach {
            // Log.e("inside list", "yes")
            if (posture != null && posture?.size != 0 && it.posture in posture!!) {
            } else {
                posture?.add(it.posture)
            }

            var time = mAnalyticsViewModel.getTimeFloat(it.observationDateTime, requireActivity())
            tempGraphEntry!!.add(Entry(time.toFloat(), it.bodyTemperature))

            heartGraphEntry!!.add((Entry(time.toFloat(), it.heartRate)))
            respirationGraphEntry!!.add(Entry(time.toFloat(), it.respirationRate))
            helthGraphEntry!!.add(
                Entry(
                    time.toFloat(),
                    mAnalyticsViewModel.getIntPosture(it.posture, requireActivity())
                )
            )
        }
        setTempGraph(false, temp_chart, tempGraphEntry!!)
        setTempGraph(false, heart_rate_chart, heartGraphEntry!!)
        setTempGraph(false, respiration_rate_chart, respirationGraphEntry!!)
        settingYLevel(helthGraphEntry!!)

    }


    private fun setTempGraph(
        isEcgGraph: Boolean,
        mychart: LineChart,
        graphEntry: ArrayList<Entry>
    ) {

        mychart.clear()

        Collections.sort(graphEntry, EntryXComparator())
        var lineDataSet = LineDataSet(graphEntry, "")
        //setting draw values
        lineDataSet.setDrawValues(false)
        lineDataSet.setDrawCircles(false)
        //setting line width
        lineDataSet.lineWidth = 2f
        //seting line color
        //lineDataSet.color = requireActivity().resources.getColor(R.color.health_alert_color)
        lineDataSet.color = getColorGraph(mychart)
        // holo circle
        lineDataSet.setDrawCircleHole(false)
        //circle radius
        lineDataSet.circleRadius = 2f
        //circle holo radius
        lineDataSet.circleHoleRadius = 1f
        var max = mAnalyticsViewModel.getTimeFloat(toDate, requireActivity())
        var min = mAnalyticsViewModel.getTimeFloat(fromDate, requireActivity())
        //crircle color
        if (isEcgGraph) {
            lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        } else {

            mychart.xAxis.axisMaximum = max.toFloat()
            mychart.xAxis.axisMinimum = min.toFloat()
        }
        lineDataSet.setCircleColor(requireActivity().resources.getColor(R.color.health_alert_color))

        var lineDataSetList = ArrayList<LineDataSet>()
        lineDataSetList.add(lineDataSet)
        var lineData = LineData(lineDataSetList as List<ILineDataSet>?)
        mychart.data = lineData
        //mychart.getAxisLeft().setDrawGridLines(false);
        mychart.xAxis.setDrawGridLines(false)
        mychart.axisRight.isEnabled = false

        //mychart.getAxisRight().setDrawGridLines(false);
        //mychart.axisRight.setDrawGridLines(false);
        //mychart.setDrawGridBackground(false)

        //Uncomment to show Y axis lables in Text

        //helth_chart.setViewPortOffsets(20f, 0f, 10f, 60f);
        mychart.xAxis.setLabelCount(LABEL_COUNT, true)
        mychart.xAxis.setAvoidFirstLastClipping(true)
        mychart.xAxis.setDrawLimitLinesBehindData(false)

        if (hours != null && hours != 0) {
            // mychart.xAxis.granularity = mAnalyticsViewModel.getGranuality(hours).toFloat()
        }
        mychart.xAxis.setAvoidFirstLastClipping(true)
        mychart.xAxis.setLabelCount(LABEL_COUNT, true)
        //  mychart.invalidate()
        mychart.xAxis.setCenterAxisLabels(false)

        mychart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        mychart.description.isEnabled = false
        mychart.legend.isEnabled = false

        // val labelList = mAnalyticsViewModel.getXAXIS(toDate, fromDate, requireActivity())
        //  mychart.xAxis.valueFormatter = IndexAxisValueFormatter(labelList)
        // Log.e("labelList", "${labelList.size}  , $labelList")
        val mFormatValue = mAnalyticsViewModel.getFormat(max, min)
        Log.e("mFOrmat", mFormatValue)

        if (!isEcgGraph) {
            mychart.xAxis.valueFormatter = object : ValueFormatter() {
                val mFormat: SimpleDateFormat =
                    SimpleDateFormat(mFormatValue)

                override fun getFormattedValue(value: Float): String? {
                    var formated = mFormat.format(value)
                    if (formated.contains(" ")) {
                        val newFormated = formated.split(" ")
                        if (newFormated.size == 2) {
                            formated = newFormated[0] + "\n" + newFormated[1]
                            mychart.xAxis.labelRotationAngle = Constants.ANGLE
                            Log.e("newFOrmated", formated)
                        }
                    } else if (formated.contains("/")) {
                        mychart.xAxis.labelRotationAngle = Constants.ANGLE
                    }
                    return formated
                }

            }
        }
        mychart.setExtraOffsets(0f, 0f, 0f, 5f)
        mychart.invalidate()
        Log.e(
            "chart",
            "${mychart.xAxis.labelCount} , ${mychart.xAxis.axisMaximum.toDouble()} ,${mychart.xAxis.granularity} ,${
            mychart.xAxis
                .isAxisMaxCustom
            } , $mychart"
        )
        Log.e("labels", "${mychart.scaleX}")
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
        helth_chart.clear()

        Collections.sort(heartEntry, EntryXComparator())
        var lineDataSet = LineDataSet(heartEntry, "")
        //setting draw values
        lineDataSet.setDrawValues(false)
        //setting line width
        lineDataSet.lineWidth = 1f
        //seting line color
        //lineDataSet.color = requireActivity().resources.getColor(R.color.health_alert_color)
        lineDataSet.color = getColorGraph(helth_chart)
        lineDataSet.setDrawCircles(false)
        // holo circle
        lineDataSet.setDrawCircleHole(false)
        //circle radius
        lineDataSet.circleRadius = 2f
        //circle holo radius
        lineDataSet.circleHoleRadius = 1f
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
        //helth_chart.setViewPortOffsets(20f, 0f, 10f, 60f);

        var max = mAnalyticsViewModel.getTimeFloat(toDate, requireActivity())
        var min = mAnalyticsViewModel.getTimeFloat(fromDate, requireActivity())
        helth_chart.xAxis.setLabelCount(LABEL_COUNT, true)
        helth_chart.xAxis.setAvoidFirstLastClipping(true)

        helth_chart.xAxis.axisMaximum = max.toFloat()
        helth_chart.xAxis.axisMinimum = min.toFloat()
        if (hours != null && hours != 0) {
            // helth_chart.xAxis.granularity = mAnalyticsViewModel.getGranuality(hours).toFloat()
        }
        helth_chart.xAxis.setAvoidFirstLastClipping(true)
        helth_chart.xAxis.setLabelCount(LABEL_COUNT, true)
        //  helth_chart.invalidate()
        helth_chart.xAxis.setCenterAxisLabels(false)

        helth_chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        helth_chart.description.isEnabled = false
        helth_chart.legend.isEnabled = false
        // val labelList = mAnalyticsViewModel.getXAXIS(toDate, fromDate, requireActivity())
        //  helth_chart.xAxis.valueFormatter = IndexAxisValueFormatter(labelList)
        // Log.e("labelList", "${labelList.size}  , $labelList")

        val mFormatValue = mAnalyticsViewModel.getFormat(max, min)
        helth_chart.xAxis.valueFormatter = object : ValueFormatter() {
            val mFormat: SimpleDateFormat =
                SimpleDateFormat(mFormatValue)

            override fun getFormattedValue(value: Float): String? {
                var formated = mFormat.format(value)
                if (formated.contains(" ")) {
                    val newFormated = formated.split(" ")
                    if (newFormated.size == 2) {
                        formated = newFormated[0] + "\n" + newFormated[1]
                        helth_chart.xAxis.labelRotationAngle = Constants.ANGLE
                        Log.e("newFOrmated", formated)
                    }
                } else if (formated.contains("/")) {
                    helth_chart.xAxis.labelRotationAngle = Constants.ANGLE
                }
                return formated
            }

        }
        helth_chart.setExtraOffsets(10f, 10f, 10f, 5f)

        helth_chart.invalidate()
    }

    private fun showProgressBar() {
        mProgressDialog?.show()
    }

    private fun showError(error: String) {
        hideProgressbar()
        showGraph(false)
        showMessage(error)

    }

    private fun hideProgressbar() {

        binding.scrollView.visibility = View.VISIBLE
        mProgressDialog?.hide()
    }

    private fun showMessage(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ecg_expand_icon -> setECGVisible(true)
            R.id.ecg_collapse_icon -> setECGVisible(false)
        }
    }

    /*will show or hide ECG graph when user click to expand/collapse  */
    private fun setECGVisible(visible: Boolean) {
        if (visible) {
            ecg_graph_layout.visibility = View.VISIBLE
            ecg_collapse_icon.visibility = View.VISIBLE
            ecg_expand_icon.visibility = View.GONE
            showECGChart()
        } else {
            ecg_graph_layout.visibility = View.GONE
            ecg_collapse_icon.visibility = View.GONE
            ecg_expand_icon.visibility = View.VISIBLE
        }
    }

    /*will use to bind ecg data with chart*/
    private fun showECGChart() {
        try {
            val gson = Gson()
            var data =
                gson.fromJson(getString(R.string.ecg_temp_data), ECGResponse::class.java)
            val liveData = data.liveData
            liveData.forEach { root ->
                liveData
                var i = 0
                root.ECG0.forEach {
                    if (it != null && root.ECG1[i] != null) {
                        ecgGraphEntry!!.add(Entry(root.ECG1[i], it))
                    }
                    i++
                }
            }
            setTempGraph(true, ecg_chart, ecgGraphEntry!!)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}