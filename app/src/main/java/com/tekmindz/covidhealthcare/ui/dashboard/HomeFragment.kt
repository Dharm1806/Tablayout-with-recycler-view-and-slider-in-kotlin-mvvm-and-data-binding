package com.tekmindz.covidhealthcare.ui.dashboard

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.tekmindz.covidhealthcare.R
import kotlinx.android.synthetic.main.fragment_home.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private lateinit var mDashboardViewModel: DashboardViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var demoCollectionAdapter: HomeTabAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mDashboardViewModel = ViewModelProviders.of(this).get(DashboardViewModel::class.java)

        demoCollectionAdapter =
            HomeTabAdapter(this)
        pager.adapter = demoCollectionAdapter
        setDividers()
        TabLayoutMediator(tab_layout, pager) { tab, position ->
           var tabTitle = "3"
            if (position==0){
                tabTitle = getString(R.string.hours_3)
            }else if (position ==1){
                tabTitle=getString(R.string.hours_6)
            }else if (position == 2){
                tabTitle =getString(R.string.hours_12)
            }
            else if (position ==3){
                tabTitle = getString(R.string.hours_24)
            }else{
                tabTitle = getString(R.string.date_range);
            }

            tab.text = tabTitle
            pager.setCurrentItem(tab.position, true)

        }.attach()
        pager.setUserInputEnabled(false);

        if (!mDashboardViewModel.getIsLogin()) {
            findNavController().navigate(
                R.id.homeTologin, null, NavOptions.Builder()
                    .setPopUpTo(
                        R.id.home,
                        true
                    ).build()
            )
        }

    }

    fun setDividers() {
        val root: View = tab_layout.getChildAt(0)
        if (root is LinearLayout) {
            (root as LinearLayout).showDividers = LinearLayout.SHOW_DIVIDER_NONE
            val drawable = GradientDrawable()
            drawable.setColor(resources.getColor(R.color.colorAccent))
            drawable.setSize(2, 1)
            (root as LinearLayout).dividerPadding = 5
            (root as LinearLayout).dividerDrawable = drawable
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}