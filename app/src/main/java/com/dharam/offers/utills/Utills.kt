package com.dharam.offers.utills


import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.dharam.offers.R
import com.dharam.offers.constants.Constants


object Utills {


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


    fun getTabTitile(position: Int, context: Context): String {

        var tabTitle = "3"
        if (position == 0) tabTitle = context.getString(R.string.offers)
        else if (position == 1) tabTitle = context.getString(R.string.details)

        return tabTitle

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





}