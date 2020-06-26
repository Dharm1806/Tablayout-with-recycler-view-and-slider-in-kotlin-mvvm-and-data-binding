package com.tekmindz.covidhealthcare.utills


import android.app.ProgressDialog
import android.content.Context
import android.net.ConnectivityManager
import android.util.Base64
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tekmindz.covidhealthcare.R
import com.tekmindz.covidhealthcare.constants.Constants.BASIC
import com.tekmindz.covidhealthcare.constants.Constants.CLIENT_ID
import com.tekmindz.covidhealthcare.constants.Constants.CLIENT_SECRET
import com.tekmindz.covidhealthcare.constants.Constants.SERVER_DATE_FORMAT
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


object Utills {

    //Email validation pattern

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

    fun initializeProgressBar(context: Context): ProgressDialog {

        val progressDialog = ProgressDialog(context)
        progressDialog.setCancelable(false)
        progressDialog.setTitle(context.getString(R.string.msg_please_wait))
        return progressDialog

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

    fun verifyAvailableNetwork(activity: AppCompatActivity): Boolean {

        val connectivityManager =
            activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected

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
        //return formatDateIntoFilterFormat(date)
        return "2020-06-26T06:32:37Z"
    }

    fun getStartDate(hours: Int): String {

        val date = Date(System.currentTimeMillis() - hours * 60 * 60 * 1000)

//        return formatDateIntoFilterFormat(date)
        return "2019-06-26T09:32:37Z"
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


}