package com.tekmindz.covidhealthcare.utills

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.GsonBuilder
import com.tekmindz.covidhealthcare.R

class SharedPreference(val context: Application) {

    private val PREF_PHOENIX = context.getString(R.string.app_name)
    val mSharedPref: SharedPreferences =
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


    /**
     * Saves object into the Preferences.
     *
     * @param `object` Object of model class (of type [T]) to save
     * @param key Key with which Shared preferences to
     **/
    fun <T> put(`object`: T, key: String) {
        //Convert object to JSON String.
        val jsonString = GsonBuilder().create().toJson(`object`)
        //Save that String in SharedPreferences
        mSharedPref.edit().putString(key, jsonString).apply()
    }

    /**
     * Used to retrieve object from the Preferences.
     *
     * @param key Shared Preference key with which object was saved.
     **/
    inline fun <reified T> get(key: String): T? {
        //We read JSON String which was saved.
        val value = mSharedPref.getString(key, null)
        //JSON String was found which means object can be read.
        //We convert this JSON String to model object. Parameter "c" (of
        //type Class < T >" is used to cast.
        return GsonBuilder().create().fromJson(value, T::class.java)
    }

}