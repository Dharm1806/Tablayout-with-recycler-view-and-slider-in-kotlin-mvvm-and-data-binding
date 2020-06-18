package com.tekmindz.covidhealthcare.adapters

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tekmindz.covidhealthcare.constants.Constants.ARG_OBJECT
import com.tekmindz.covidhealthcare.destinations.DemoObjectFragment

class DemoCollectionAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 100

    override fun createFragment(position: Int): Fragment {
        // Return a NEW fragment instance in createFragment(int)
        val fragment = DemoObjectFragment()
        fragment.arguments = Bundle().apply {
            // Our object is just an integer :-P
            Log.e("adapter create fragment",  "$position+1")
            putInt(ARG_OBJECT, position + 1)
        }
        return fragment
    }
}