package com.tekmindz.covidhealthcare.ui.search

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tekmindz.covidhealthcare.R
import com.tekmindz.covidhealthcare.constants.Constants.STATE_CRITICAL
import com.tekmindz.covidhealthcare.constants.Constants.STATE_RECOVERED
import com.tekmindz.covidhealthcare.constants.Constants.STATE_UNDER_CONTROL
import com.tekmindz.covidhealthcare.databinding.ItemSearchListBinding
import com.tekmindz.covidhealthcare.repository.responseModel.observations


class SearchHolder(val binding: ItemSearchListBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(observation: observations, clickListener: OnItemClickListener, mContext: Context) {
        //load the patient profile image
        Glide.with(mContext).load(observation.imageUrl).into(binding.imgProfile)

        if (observation.status.equals(STATE_CRITICAL)) {
            binding.imgProfile.borderColor = mContext.resources.getColor(R.color.red)
            binding.patientName.setTextColor(mContext.resources.getColor(R.color.red))
        } else if (observation.status.equals(STATE_UNDER_CONTROL)) {
            binding.imgProfile.borderColor = mContext.resources.getColor(R.color.amber)
            binding.patientName.setTextColor(mContext.resources.getColor(R.color.amber))
        } else if (observation.status.equals(STATE_RECOVERED)) {
            binding.patientName.setTextColor(mContext.resources.getColor(R.color.green))
            binding.imgProfile.borderColor = mContext.resources.getColor(R.color.green)
        }

        //set patient name with first charactar capital
        val name = observation.firstName.capitalize() + " " + observation.lastName.capitalize()
        binding.patientName.text = name

        //set patient ward no.
        val wardNo = mContext.getString(R.string.msg_ward_no) + observation.wardNo
        binding.wardNo.text = wardNo

        //set patient bed no.
        val bedNo = mContext.getString(R.string.msg_bed_no) + observation.bedNumber
        binding.badNo.text = bedNo


        //handle patient click event
        binding.itemLayoutObservations.setOnClickListener {
            clickListener.onItemClicked(observation)
        }

    }

}


class SearchAdapter(
    private var searchResults: List<observations>,
    private val itemClickListener: OnItemClickListener, private var mContext: Context
) : RecyclerView.Adapter<SearchHolder>() {

    @Override
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): SearchHolder {
        //inflate the view
        val mBinding: ItemSearchListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_search_list, parent, false
        )
        return SearchHolder(mBinding)
    }

    //return the item count of patient list
    @Override
    override fun getItemCount(): Int = searchResults.size

    //bind the viewholder
    @Override
    override fun onBindViewHolder(myHolder: SearchHolder, position: Int) =
        myHolder.bind(searchResults[position], itemClickListener, mContext)
}


interface OnItemClickListener {
    fun onItemClicked(mSearchResults: observations)
}