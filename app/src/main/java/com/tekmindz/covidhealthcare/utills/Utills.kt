package com.tekmindz.covidhealthcare.utills


import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CompositeDateValidator
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.tekmindz.covidhealthcare.R
import com.tekmindz.covidhealthcare.application.App
import com.tekmindz.covidhealthcare.constants.Constants
import com.tekmindz.covidhealthcare.constants.Constants.APP_DATE_FORMAT
import com.tekmindz.covidhealthcare.constants.Constants.BASIC
import com.tekmindz.covidhealthcare.constants.Constants.CLIENT_ID
import com.tekmindz.covidhealthcare.constants.Constants.CLIENT_SECRET
import com.tekmindz.covidhealthcare.constants.Constants.HEALTH_CARE_WORKER_ROLE
import com.tekmindz.covidhealthcare.constants.Constants.SERVER_DATE_FORMAT
import com.tekmindz.covidhealthcare.constants.Constants.SERVER_DOB_DATE_FORMAT
import com.tekmindz.covidhealthcare.constants.Constants.SERVER_GRAPH_DATE_FORMAT
import com.tekmindz.covidhealthcare.constants.Constants.SUPERVISOR_ROLE
import com.tekmindz.covidhealthcare.constants.Constants.SUPER_ADMIN_ROLE
import com.tekmindz.covidhealthcare.constants.UserTypes
import com.tekmindz.covidhealthcare.repository.responseModel.DateRange
import com.tekmindz.covidhealthcare.repository.responseModel.UserInfoBody
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


object Utills {
    var destination: String = ""
    var dateRange: MutableLiveData<DateRange> = MutableLiveData<DateRange>()

    fun dateRange(dateRangeValue: String) {
        //  Log.e("date", "$dateRangeValue")
        this.dateRange.value = DateRange(dateRangeValue)
    }


    fun getDateRangeValue(): MutableLiveData<DateRange> = this.dateRange

    //Email validation pattern

    private val EMAIL_ADDRESS_PATTERN: Pattern = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    /**
     * hide action bar of activity
     */

    fun hideActionBar(activity: AppCompatActivity) {

        activity.supportActionBar?.hide()
    }

    /**initialize progressbar and @return progressbar instance
     */

    fun initializeProgressBar(context: Context, style: Int): ProgressDialog {

        // return setProgressDialog(context, "Loading..")
        val progressDialog = ProgressDialog(context)
        progressDialog.setIndeterminateDrawable(context.getDrawable(R.drawable.bg_progress))
        progressDialog.setCancelable(false)
        progressDialog.setTitle(context.getString(R.string.msg_please_wait))
        return progressDialog

    }

    fun setProgressDialog(context: Context, message: String): AlertDialog {
        val llPadding = 30
        val ll = LinearLayout(context)
        ll.orientation = LinearLayout.HORIZONTAL
        ll.setPadding(llPadding, llPadding, llPadding, llPadding)
        ll.gravity = Gravity.CENTER
        var llParam = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        llParam.gravity = Gravity.CENTER
        ll.layoutParams = llParam

        val progressBar = ProgressBar(context)

        progressBar.isIndeterminate = true
        progressBar.setPadding(0, 0, llPadding, 0)
        progressBar.layoutParams = llParam

        llParam = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        llParam.gravity = Gravity.CENTER
        val tvText = TextView(context)
        tvText.text = message
        tvText.setTextColor(Color.parseColor("#000000"))
        tvText.textSize = 20.toFloat()
        tvText.layoutParams = llParam

        ll.addView(progressBar)
        ll.addView(tvText)

        val builder = AlertDialog.Builder(context)
        builder.setCancelable(true)
        builder.setView(ll)

        val dialog = builder.create()
        val window = dialog.window
        if (window != null) {
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dialog.window?.attributes)
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
            dialog.window?.attributes = layoutParams
        }

        return dialog
    }

    /** show internet error
     */

    fun showEnableInternetMessage(context: Context) = Toast.makeText(
        context,
        context.getString(R.string.msg_please_connect_to_internet),
        Toast.LENGTH_LONG
    ).show()

    /**
     * vaidate email
     */

    fun isValidEmail(email: String): Boolean = EMAIL_ADDRESS_PATTERN.matcher(email).matches()

    /**
     * check device is connected to internet or not
     */

    fun verifyAvailableNetwork(activity: FragmentActivity): Boolean {

        val connectivityManager =
            activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        val isInternet = networkInfo != null && networkInfo.isConnected
        if (!isInternet) {
            showInternetAlertDailog(activity)
        }
        return isInternet
    }

    /**
     * validate password
     */

    fun isValidPassword(password: String): Boolean {

        var isValid = true
        if (password.length == 0) isValid = false
        return isValid
    }


    /**
     * match password and confirm password ar same or not
     */

    fun matchPassword(password: String, confrimPassword: String): Boolean {

        var isMatched: Boolean = true
        if (!password.equals(confrimPassword)) {
            isMatched = false
        }
        return isMatched
    }


    /**
     *  create authrization to send in login and signup api header
     */

    fun getHeaderToken(): String = BASIC + base64Encode(CLIENT_ID + ":" + CLIENT_SECRET)


    /**
     * encode string into base64
     */

    private fun base64Encode(value: String): String =
        Base64.encodeToString(value.toByteArray(), Base64.NO_WRAP)

    /**
     * get current date
     */

    fun getCurrentDate(): String {

        val date = Date()
        return formatDateIntoFilterFormat(date)
        // return "2020-06-26T06:32:37Z"
    }

    fun getRealCurrentDate(): String {

        val date = Date()
        return formatDateIntoFilterFormat(date)
        //  return "2020-06-26T06:32:37Z"
    }

    fun getDate(milis: Long): String {

        val date = Date(milis)
        return formatDateIntoFilterFormat(date)
        // return "2020-06-26T06:32:37Z"
    }

    fun getStartDate(hours: Int): String {

        val date = Date(System.currentTimeMillis() - hours * 60 * 60 * 1000)

        return formatDateIntoFilterFormat(date)
        //return "2019-06-26T09:32:37Z"
    }

    /**
     * get start date for charging history i.e. 1 month more than end date
     */

    fun getFilterStartDate(): String {

        val cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, -1)
        val date = cal.time
        return formatDateIntoFilterFormat(date)
    }

    /**
     * format date as per filter date format
     */

    private fun formatDateIntoFilterFormat(date: Date): String {

        val formatter = SimpleDateFormat(SERVER_DATE_FORMAT, Locale.ENGLISH)
        return formatter.format(date)

    }


    fun parseDate(date: String): String {
        Log.e("date", "$date")
        var parser = SimpleDateFormat(SERVER_DOB_DATE_FORMAT)
        val formatter = SimpleDateFormat(APP_DATE_FORMAT)
        if (date.trim().length == 0) {
            return ""
        } else {
            try {
                Log.e("dsafsaf", "asfsa")
                return formatter.format(parser.parse(date))
            } catch (ex: Exception) {
                parser = SimpleDateFormat(SERVER_GRAPH_DATE_FORMAT)
                Log.e("parser", "dsadsa")
                return formatter.format(parser.parse(date))
            }

        }
    }

    fun getTabTitile(position: Int, context: Context): String {

        var tabTitle = "3"
        if (position == 0) tabTitle = context.getString(R.string.hours_3)
        else if (position == 1) tabTitle = context.getString(R.string.hours_6)
        else if (position == 2) tabTitle = context.getString(R.string.hours_12)
        else if (position == 3) tabTitle = context.getString(R.string.hours_24)
        else if (position == 4) tabTitle = context.getString(R.string.date_range)
        return tabTitle

    }

    fun getHours(position: Int): Int {

        var hours = 3

        if (position == 0) hours = 3
        else if (position == 1) hours = 6
        else if (position == 2) hours = 12
        else if (position == 3) hours = 24
        else if (position == 4) hours = -1

        return hours
    }

    fun showInternetAlertDailog(context: Activity) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AlertDialogTheme))

        builder.setTitle(context.getString(R.string.no_internet))
        builder.setMessage(context.getString(R.string.msg_please_connect))
        builder.setPositiveButton(android.R.string.ok) { dialog, which ->
            val intent = Intent(Settings.ACTION_SETTINGS)
            context.startActivity(intent)
        }

        builder.setCancelable(false)

        builder.show()
    }


    fun showAlertNoPatient(context: Activity) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AlertDialogTheme))

        //  builder.setTitle(context.getString(R.string.no_internet))
        builder.setMessage(context.getString(R.string.you_are_not_being_monitered))
        builder.setPositiveButton(android.R.string.ok) { dialog, which ->
            /*    val intent = Intent(Settings.ACTION_SETTINGS)
                context.startActivity(intent)*/
        }

        builder.setCancelable(true)

        builder.show()
    }

    fun hideKeyboard(activity: Activity) {
        val inputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        // Check if no view has focus
        val currentFocusedView = activity.currentFocus
        currentFocusedView?.let {
            inputMethodManager.hideSoftInputFromWindow(
                currentFocusedView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    fun round(value: String): String? {
        return BigDecimal(value).setScale(2, RoundingMode.HALF_EVEN).toString()
    }

    fun userType(): String {
        return App.mSharedPrefrenceManager.getValueString(Constants.PREF_USER_TYPE)
            ?: UserTypes.PATIENT.toString()

    }


    @Throws(Exception::class)
    fun decoded(JWTEncoded: String): String {
        var usertYPE = ""
        try {
            val split =
                JWTEncoded.split("\\.".toRegex()).toTypedArray()

            val body = getJson(split[1])
            val bodyObject = JSONObject(body.toString())
            val realmAccess = bodyObject.getJSONObject("realm_access")
            val roles = realmAccess.getJSONArray("roles")
            for (i in 0..roles.length()) {
                if (roles.get(i) == "supervisor") {
                    usertYPE = UserTypes.HEALTH_WORKER.toString()
                    break
                }
                if (roles.get(i) == "patient") {
                    usertYPE = UserTypes.PATIENT.toString()
                    break

                }
            }

        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            //Error

        }

        return usertYPE
    }

    @Throws(UnsupportedEncodingException::class)
    private fun getJson(strEncoded: String): String {
        val decodedBytes =
            Base64.decode(strEncoded, Base64.URL_SAFE)
        return String(decodedBytes, Charsets.UTF_8)
    }

    fun callPhoneNumber(requireActivity: Activity) {
        try {
            if (Build.VERSION.SDK_INT > 22) {
                if (ActivityCompat.checkSelfPermission(
                        requireActivity,
                        Manifest.permission.CALL_PHONE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        requireActivity,
                        arrayOf(Manifest.permission.CALL_PHONE),
                        101
                    )
                    return
                }
                val callIntent = Intent(Intent.ACTION_CALL)
                callIntent.data = Uri.parse("tel:" + Constants.SOS_NUMBER)
                requireActivity.startActivity(callIntent)
            } else {
                val callIntent = Intent(Intent.ACTION_CALL)
                callIntent.data = Uri.parse("tel:" + Constants.SOS_NUMBER)
                requireActivity.startActivity(callIntent)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun isPatient(userInfoBody: UserInfoBody?): Boolean {
        return !(userInfoBody != null && userInfoBody.roles != null && ((userInfoBody.roles.contains(
            SUPERVISOR_ROLE
        )) || userInfoBody.roles.contains(SUPER_ADMIN_ROLE) || userInfoBody.roles.contains(
            HEALTH_CARE_WORKER_ROLE
        )))
    }

    fun isPatientAndHc(userInfoBody: UserInfoBody?): Boolean {
        return isPatient(userInfoBody)
        /* return ((userInfoBody?.roles?.contains(SUPERVISOR_ROLE)!!) || userInfoBody.roles.contains(
             SUPER_ADMIN_ROLE
         ) || userInfoBody.roles.contains(
             HEALTH_CARE_WORKER_ROLE
         )) && userInfoBody.roles.contains(
             PATIENT_ROLE
         )*/
    }

    fun getPatientId(): String? {
        val userInfoBody = App.mSharedPrefrenceManager.get<UserInfoBody>(Constants.PREF_USER_INFO)
        return userInfoBody?.patientId?.toString()
    }

    fun hide(context: Context) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
        if (imm?.isAcceptingText!!) {
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        }

    }

    fun dateValidator(maxDate: Long): CalendarConstraints.Builder {
        val constraintsBuilder = CalendarConstraints.Builder()
        val validators: ArrayList<CalendarConstraints.DateValidator> = ArrayList()
        //validators.add(DateValidatorPointForward.from(minDate))
        validators.add(DateValidatorPointBackward.before(maxDate))
        constraintsBuilder.setValidator(CompositeDateValidator.allOf(validators))
        return constraintsBuilder
    }


    fun showAlertMessage(context: Activity, message: String) {

        val dialog = Dialog(context)
        //  dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.show_alert_message_layout)
        val window: Window = dialog.window!!
        window.setLayout(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        val tvMessage = dialog.findViewById(R.id.tvMessage) as TextView
        val tvOk = dialog.findViewById(R.id.tvOk) as TextView
        tvMessage.text = message
        tvOk.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                Utills.hide(context)

                dialog.dismiss()
                return v?.onTouchEvent(event) ?: true
            }
        })
        dialog.show()

    }
}