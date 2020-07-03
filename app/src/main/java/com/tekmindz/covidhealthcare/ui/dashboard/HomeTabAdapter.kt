package com.tekmindz.covidhealthcare.ui.dashboard

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.datepicker.MaterialDatePicker
import com.tekmindz.covidhealthcare.constants.Constants.ARG_FROM_TIME
import com.tekmindz.covidhealthcare.constants.Constants.ARG_TIME
import com.tekmindz.covidhealthcare.constants.Constants.ARG_TO_TIME
import com.tekmindz.covidhealthcare.utills.Utills
import java.util.*

class HomeTabAdapter(
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
            TabItemFragment()
        fragment.arguments = Bundle().apply {
            // Our object is just an integer :-P

            putInt(ARG_TIME, hours)
        }
        return fragment
    }
}