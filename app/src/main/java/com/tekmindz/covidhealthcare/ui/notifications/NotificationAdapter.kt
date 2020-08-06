package com.tekmindz.covidhealthcare.ui.notifications

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tekmindz.covidhealthcare.R
import com.tekmindz.covidhealthcare.constants.Constants.STATE_CRITICAL
import com.tekmindz.covidhealthcare.constants.Constants.STATE_RECOVERED
import com.tekmindz.covidhealthcare.constants.Constants.STATE_UNDER_CONTROL
import com.tekmindz.covidhealthcare.constants.Constants.UNIT_HEART_RATE
import com.tekmindz.covidhealthcare.constants.Constants.UNIT_RESPIRATION
import com.tekmindz.covidhealthcare.constants.Constants.UNIT_TEMPERATURE
import com.tekmindz.covidhealthcare.databinding.ItemNotificationsBinding
import com.tekmindz.covidhealthcare.repository.responseModel.Body


class NotificationHolder(val binding: ItemNotificationsBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(body: Body, clickListener: OnItemClickListener, mContext: Context) {
        //load the patient profile image
        //Glide.with(mContext).load(observation.imageUrl).into(binding.imgProfile)

        if (body.status.toUpperCase().equals(STATE_CRITICAL)) {
            binding.tvReading.setTextColor(mContext.resources.getColor(R.color.red))
            binding.tvPatientStatus.setBackgroundColor(mContext.resources.getColor(R.color.red))
        } else if (body.status.toUpperCase().equals(STATE_UNDER_CONTROL)) {
            binding.tvPatientStatus.setBackgroundColor(mContext.resources.getColor(R.color.amber))
            binding.tvReading.setTextColor(mContext.resources.getColor(R.color.amber))
        } else if (body.status.toUpperCase().equals(STATE_RECOVERED)) {
            binding.tvPatientStatus.setBackgroundColor(mContext.resources.getColor(R.color.green))
            binding.tvReading.setTextColor(mContext.resources.getColor(R.color.green))
        }

        setNotificationIcon(binding.icNotification, body.obsType, body.status, mContext)

//setNotificationData
        binding.tvNotificationMessage.text = body.patientName
        binding.tvPatientStatus.text = body.status
        binding.tvReading.text = body.obsValue + " " + body.obsType
        binding.tvBedNo.text = mContext.getString(R.string.msg_bed_number) + ": " + body.bedNumber
        binding.tvWardNo.text =
            mContext.getString(R.string.msg_ward_number) + ": " + body.wardNumber

        //handle patient click event
        binding.itemLayoutObservations.setOnClickListener {
            clickListener.onItemClicked(body)
        }

    }

    private fun setNotificationIcon(
        icNotification: ImageView,
        unit: String,
        patientStatus: String,
        mContext: Context
    ) {
        if (patientStatus.toUpperCase().equals(STATE_CRITICAL)) {
            if (unit.equals(UNIT_HEART_RATE)) {
                Glide.with(mContext).load(R.drawable.ic_heart_rate_critical).into(icNotification)
            } else if (unit.equals(UNIT_RESPIRATION)) {
                Glide.with(mContext).load(R.drawable.ic_resp_rate_critical).into(icNotification)
            } else if (unit.equals(UNIT_TEMPERATURE)) {
                Glide.with(mContext).load(R.drawable.ic_temp_critical).into(icNotification)
            }
        } else if (patientStatus.toUpperCase().equals(STATE_UNDER_CONTROL)) {
            if (unit.equals(UNIT_HEART_RATE)) {
                Glide.with(mContext).load(R.drawable.ic_heart_rate_under_control)
                    .into(icNotification)
            } else if (unit.equals(UNIT_RESPIRATION)) {
                Glide.with(mContext).load(R.drawable.ic_resp_rate_under_control)
                    .into(icNotification)
            } else if (unit.equals(UNIT_TEMPERATURE)) {
                Glide.with(mContext).load(R.drawable.ic_temp_under_control).into(icNotification)
            }
        } else if (patientStatus.toUpperCase().equals(STATE_RECOVERED)) {
            if (unit.equals(UNIT_HEART_RATE)) {
                Glide.with(mContext).load(R.drawable.ic_heart_rate_recovered).into(icNotification)
            } else if (unit.equals(UNIT_RESPIRATION)) {
                Glide.with(mContext).load(R.drawable.ic_resp_rate_recovered).into(icNotification)
            } else if (unit.equals(UNIT_TEMPERATURE)) {
                Glide.with(mContext).load(R.drawable.ic_temp_recovered).into(icNotification)
            }
        }

    }

}


class NotificationAdapter(
    private var notificationList: List<Body>,
    private val itemClickListener: OnItemClickListener, private var mContext: Context
) : RecyclerView.Adapter<NotificationHolder>() {

    @Override
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): NotificationHolder {
        //inflate the view
        val mBinding: ItemNotificationsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_notifications, parent, false
        )
        return NotificationHolder(mBinding)
    }

    //return the item count of patient list
    @Override
    override fun getItemCount(): Int = notificationList.size

    //bind the viewholder
    @Override
    override fun onBindViewHolder(myHolder: NotificationHolder, position: Int) =
        myHolder.bind(notificationList[position], itemClickListener, mContext)
}


interface OnItemClickListener {
    fun onItemClicked(body: Body)
}