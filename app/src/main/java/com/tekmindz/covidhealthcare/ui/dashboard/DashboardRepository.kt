package com.tekmindz.covidhealthcare.ui.dashboard

import com.tekmindz.covidhealthcare.application.App.Companion.mSharedPrefrenceManager
import com.tekmindz.covidhealthcare.constants.Constants.PREF_IS_LOGIN



class DashboardRepository {


    fun getIsLogin():Boolean = mSharedPrefrenceManager.getIsLogin(PREF_IS_LOGIN)

}
