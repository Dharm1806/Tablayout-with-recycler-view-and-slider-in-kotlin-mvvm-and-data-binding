package com.tekmindz.covidhealthcare.destinations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tekmindz.covidhealthcare.R
import com.tekmindz.covidhealthcare.constants.Constants.ARG_OBJECT
import kotlinx.android.synthetic.main.fragment_tab_item.*

class TabItemFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_tab_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf { it.containsKey(ARG_OBJECT) }?.apply {
            text1.text = getString(R.string.app_name) + " " + getInt(ARG_OBJECT).toString()
        }
    }
}