package com.dharam.offers.ui.dashboard.offers

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import com.dharam.offers.R
import com.dharam.offers.constants.Constants
import com.dharam.offers.databinding.FragmentTabItemBinding
import com.dharam.offers.repository.responseModel.Coupons
import com.dharam.offers.repository.responseModel.OffersResponse
import com.dharam.offers.repository.responseModel.SliderItem
import com.dharam.offers.utills.Resource
import com.dharam.offers.utills.Utills
import java.util.*
import kotlin.collections.ArrayList


class OffersItemFragment : Fragment(), OnItemClickListener {
    private var position: Int = 0
    private lateinit var binding: FragmentTabItemBinding

    private lateinit var mOffersViewModel: OffersViewModel
    private var mProgressDialog: ProgressDialog? = null
    private lateinit var mCouponsAdapter: CouponsAdapter
    private var mCouponsList = ArrayList<Coupons>()
    private lateinit var mSliderAdapterExample: SliderAdapterExample
    var latitude: String? = null
    var longitude: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_tab_item, container, false
        )
        val view: View = binding.root
        binding.lifecycleOwner = this

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mProgressDialog =
            activity?.let { Utills.initializeProgressBar(it, R.style.AppTheme_WhiteAccent) }

        mOffersViewModel = ViewModelProviders.of(this).get(OffersViewModel::class.java)

        binding.offersViewModel = (mOffersViewModel)
        mSliderAdapterExample = SliderAdapterExample(requireActivity());
        binding.imageSlider.setSliderAdapter(mSliderAdapterExample);

        binding.imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        binding.imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        binding.imageSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        binding.imageSlider.setIndicatorSelectedColor(Color.WHITE);
        binding.imageSlider.setIndicatorUnselectedColor(Color.GRAY);
        binding.imageSlider.setScrollTimeInSec(4); //set scroll delay in seconds :
        binding.imageSlider.startAutoCycle();


        showProgressBar()
        getOffersResponse()


        mCouponsAdapter = CouponsAdapter(mCouponsList, this, requireActivity())
        binding.listOffers.adapter = mCouponsAdapter
        binding.listOffers.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        mOffersViewModel.getOffersResponse().observe(requireActivity(), Observer {
            when (it) {
                is Resource<OffersResponse> -> {
                    handleResponse(it)
                }
            }
        })
        binding?.tvMap.setOnClickListener {
            val gmmIntentUri = Uri.parse(java.lang.String.format(Locale.ENGLISH, "geo:%f,%f", latitude?.toFloat(), longitude?.toFloat()))
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }

        binding.tvCall.setOnClickListener { Utills?.callPhoneNumber(requireActivity()) }

    }


    fun getOffersResponse() {

        if (Utills.verifyAvailableNetwork(activity = requireActivity())) {
            Log.e("api call making", "yes")
            mOffersViewModel.getOffersCount()
        }
    }

    /*
       @Override
       override fun onPrepareOptionsMenu(menu: Menu) {
            val item: MenuItem = menu.findItem(R.id.sos)
            Log.e("userType", mDashboardViewModel.getUserType())

            item.isVisible = false //mDashboardViewModel.getUserType() == UserTypes.PATIENT.toString()
        }*/
    private fun handleResponse(it: Resource<OffersResponse>) {
        Log.e("it.data", "${Gson().toJson(it)}, ${it.data}")

        when (it.status) {
            Resource.Status.LOADING -> showProgressBar()
            Resource.Status.SUCCESS -> {
                // showObservations(it.data?.body!!)
                if (it.data?.statusCode == 200) {
                    binding.tvNoRecordFound.visibility = View.GONE

                    showResponse(it.data)
                } else {

                    binding.listOffers.visibility = View.GONE
                    if (it.data == null || it.data.result == null) {
                        binding.tvNoRecordFound.visibility = View.VISIBLE

                    }
                    if (it.data != null && it.data.APICODERESULT != null) {
                        showError(it.data.APICODERESULT)
                    }
                }
            }
            Resource.Status.ERROR -> {
                Log.e("error", "erryr")
                showError(it.exception!!)
            }

        }
    }


    private fun showResponse(data: OffersResponse?) {
        Log.e("size", "${Gson().toJson(data)}")
        if (data != null) {
            Constants.mOffersResponse = data
        }
        latitude = data?.result?.latitudes
        longitude = data?.result?.longitude

        hideProgressbar()
        if (data?.result?.banner != null) {
            //var bannerList:List<SliderItem>
            for (banner in data?.result?.banner) {
                mSliderAdapterExample.addItem(SliderItem(banner))
                Log.e("banner", "$banner")
            }
        }
        if (data != null && !data?.result?.cupons?.isEmpty()) {
            binding.listOffers.visibility = View.VISIBLE
            mCouponsList.clear()
            mCouponsList.addAll(data.result.cupons)
            mCouponsAdapter.notifyDataSetChanged()
            binding.tvNoRecordFound.visibility = View.GONE

        } else {
            // Utills.showAlertMessage(requireActivity(), getString(R.string.no_record_found))
            binding.tvNoRecordFound.visibility = View.VISIBLE
            binding.listOffers.visibility = View.GONE
        }
    }


    private fun showProgressBar() {
        //mProgressDialog?.show()
    }

    private fun showError(error: String) {
        showMessage(error)
        hideProgressbar()
    }

    private fun hideProgressbar() {
        //   mProgressDialog?.hide()
    }

    private fun showMessage(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    override fun onItemRedeem(mCoupons: Coupons) {
        Utills.hideKeyboard(requireActivity())
        Toast.makeText(requireActivity(), "redeem ${mCoupons.title}", Toast.LENGTH_LONG).show()
        val bundle = bundleOf("observation" to mCoupons)
        // findNavController().navigate(R.id.homeToPatientDetails, bundle)
    }


}