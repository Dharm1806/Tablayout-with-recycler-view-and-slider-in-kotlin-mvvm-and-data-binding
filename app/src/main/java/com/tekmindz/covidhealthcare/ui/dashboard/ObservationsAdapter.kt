package com.tekmindz.covidhealthcare.ui.dashboard

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
import com.tekmindz.covidhealthcare.databinding.ItemCriticalPatientListBinding
import com.tekmindz.covidhealthcare.repository.responseModel.observations


class ObserVationHolder(val binding: ItemCriticalPatientListBinding) :
    RecyclerView.ViewHolder(binding.root) {

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


class ObaserVationsAdapter(
    private var mObservations: List<observations>,
    private val itemClickListener: OnItemClickListener, private var mContext: Context
) : RecyclerView.Adapter<ObserVationHolder>() {

    @Override
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ObserVationHolder {
        //inflate the view
        val mBinding: ItemCriticalPatientListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_critical_patient_list, parent, false
        )
        return ObserVationHolder(mBinding)
    }

    //return the item count of patient list
    @Override
    override fun getItemCount(): Int = mObservations.size

    //bind the viewholder
    @Override
    override fun onBindViewHolder(myHolder: ObserVationHolder, position: Int) =
        myHolder.bind(mObservations[position], itemClickListener, mContext)
}


interface OnItemClickListener {
    fun onItemClicked(mDashboardObservationsResponse: observations)
}