package com.tekmindz.covidhealthcare.ui.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tekmindz.covidhealthcare.constants.Constants.ARG_TIME

class HomeTabAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 5

    override fun createFragment(position: Int): Fragment {
        // Return a NEW fragment instance in createFragment(int)
        val fragment =
            TabItemFragment()
        fragment.arguments = Bundle().apply {
            // Our object is just an integer :-P
            var hours = 3
            if (position ==0){
                hours =3
            }else if (position ==1){
                hours = 6
            } else if (position ==2){
                hours = 12
            } else if (position ==3){
                hours = 24
            }else{
                hours = 0
            }

            putInt(ARG_TIME, hours)
        }
        return fragment
    }
}