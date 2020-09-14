package com.dharam.offers.ui.dashboard.offers

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dharam.offers.ui.dashboard.details.DetailsFragment

class OffersTabAdapter(
    fragment: Fragment,
    requireActivity: FragmentActivity
) : FragmentStateAdapter(fragment) {
    var mContext: FragmentActivity

    init {
        this.mContext = requireActivity
    }

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        // Return a NEW fragment instance in createFragment(int)
        /* // val hours = Utills.getHours(position)*/
        var fragment:Fragment?=null
        if (position ==0) {
             fragment =
                OffersItemFragment()
        }else {
        fragment = DetailsFragment()
        }
Log.e("positions adapter", "$position")
        return fragment!!
    }
}