package com.tekmindz.covidhealthcare.ui.patientAnalytics

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tekmindz.covidhealthcare.constants.Constants.ARG_TIME
import com.tekmindz.covidhealthcare.constants.Constants.PATIENT_ID
import com.tekmindz.covidhealthcare.utills.Utills


class AnalyticsTabAdapter(
    fragment: Fragment,
    requireActivity: FragmentActivity
) : FragmentStateAdapter(fragment) {
     var  mContext:FragmentActivity
    init{this.mContext = requireActivity}
    override fun getItemCount(): Int = 5

    override fun createFragment(position: Int): Fragment {
        // Return a NEW fragment instance in createFragment(int)
        val hours = Utills.getHours(position)

        val fragment =
            AnalyticsTabFragment()
        fragment.arguments = Bundle().apply {
            // Our object is just an integer :-P

            putInt(ARG_TIME, hours)
            putString(PATIENT_ID,AnalyticsFragment.patientId)
        }
        return fragment
    }
}