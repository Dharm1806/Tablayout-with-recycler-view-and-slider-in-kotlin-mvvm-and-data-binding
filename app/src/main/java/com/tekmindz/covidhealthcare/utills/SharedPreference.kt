package com.tekmindz.covidhealthcare.utills

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.tekmindz.covidhealthcare.R

class SharedPreference(val context: Application) {

    private val PREF_PHOENIX = context.getString(R.string.app_name)
    private val mSharedPref: SharedPreferences =
        context.getSharedPreferences(PREF_PHOENIX, Context.MODE_PRIVATE)


    /**
     * save string data to shared preference using key and value
     */

    fun saveString(KEY_NAME: String, value: String) {

        val editor: SharedPreferences.Editor = mSharedPref.edit()
        editor.putString(KEY_NAME, value)
        editor.apply()

    }

    /**
     * get string data to shared preference using key
     */

    fun getValueString(KEY_NAME: String): String? = mSharedPref.getString(
        KEY_NAME,
        null
    )

    /**
     * save user is login or not
     */

    fun setIsLogin(KEY_NAME: String, value: Boolean) {

        val editor: SharedPreferences.Editor = mSharedPref.edit()
        editor.putBoolean(KEY_NAME, value)
        editor.apply()

    }

    /**
     * check user is login or not and @return login value
     */

    fun getIsLogin(KEY_NAME: String): Boolean = mSharedPref.getBoolean(KEY_NAME, false)
}