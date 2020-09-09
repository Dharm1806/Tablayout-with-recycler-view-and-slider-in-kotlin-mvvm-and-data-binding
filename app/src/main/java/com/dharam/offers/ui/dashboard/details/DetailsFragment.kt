package com.dharam.offers.ui.dashboard.details

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.dharam.offers.R
import com.dharam.offers.databinding.FragmentDetailsBinding
import com.dharam.offers.databinding.FragmentTabItemBinding
import com.dharam.offers.repository.responseModel.OffersResponse
import com.dharam.offers.utills.Utills

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetailsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentDetailsBinding

    private lateinit var mDetailsViewModel: OfferDetailsViewModel
    private var mProgressDialog: ProgressDialog? = null

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
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_details, container, false
        )
        val view: View = binding.root
        binding.lifecycleOwner = this

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mProgressDialog =
            activity?.let { Utills.initializeProgressBar(it, R.style.AppTheme_WhiteAccent) }

        mDetailsViewModel = ViewModelProviders.of(this).get(OfferDetailsViewModel::class.java)

        binding.detailsViewModel = (mDetailsViewModel)

       mDetailsViewModel.getDetails()

        mDetailsViewModel.getDetailsResponse().observe(requireActivity(), Observer {
            when (it) {
                is OffersResponse -> {
                    handleResponse(it)
                }
            }
        })


    }

    private fun handleResponse(it: OffersResponse) {
        if (it!=null){
            binding.tvDescriptions.text = it?.result?.description_body
            Glide.with(requireActivity()).load(it?.result?.decription_image).placeholder((R.drawable.ic_placeholder)).into(binding.ivDescription)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DetailsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}