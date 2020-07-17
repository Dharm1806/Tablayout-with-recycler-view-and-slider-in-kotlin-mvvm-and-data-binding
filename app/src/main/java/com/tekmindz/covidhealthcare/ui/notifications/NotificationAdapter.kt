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
import com.tekmindz.covidhealthcare.repository.responseModel.Notification
import com.tekmindz.covidhealthcare.utills.Utills


class NotificationHolder(val binding: ItemNotificationsBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(notifiction: Notification, clickListener: OnItemClickListener, mContext: Context) {
        //load the patient profile image
       //Glide.with(mContext).load(observation.imageUrl).into(binding.imgProfile)

        if (notifiction.body.patientStatus.equals(STATE_CRITICAL)) {
            binding.tvReading.setTextColor(mContext.resources.getColor(R.color.red))
            binding.tvPatientStatus.setBackgroundColor(mContext.resources.getColor(R.color.red))
        } else if (notifiction.body.patientStatus.equals(STATE_UNDER_CONTROL)) {
            binding.tvPatientStatus.setBackgroundColor(mContext.resources.getColor(R.color.amber))
            binding.tvReading.setTextColor(mContext.resources.getColor(R.color.amber))
        } else if (notifiction.body.patientStatus.equals(STATE_RECOVERED)) {
            binding.tvPatientStatus.setBackgroundColor(mContext.resources.getColor(R.color.green))
            binding.tvReading.setTextColor(mContext.resources.getColor(R.color.green))
        }

        setNotificationIcon(binding.icNotification, notifiction.body.unit, notifiction.body.patientStatus, mContext)

        //set patient name with first charactar capital

        binding.tvNotificationMessage.text = notifiction.body.message
        binding.tvPatientStatus.text = Utills.parseDate(notifiction.time)
        binding.tvReading.text = notifiction.body.reading
        binding.tvBedNo.text = mContext.getString(R.string.msg_bed_number)+": "+notifiction.body.bedNo
        binding.tvWardNo.text =mContext.getString(R.string.msg_ward_number)+": "+notifiction.body.wardNo

        //handle patient click event
        binding.itemLayoutObservations.setOnClickListener {
            clickListener.onItemClicked(notifiction)
        }

    }

    private fun setNotificationIcon(
        icNotification: ImageView,
        unit: String,
        patientStatus: String,
        mContext: Context
    ) {
        if (patientStatus.equals(STATE_CRITICAL)) {
           if (unit.equals(UNIT_HEART_RATE)){
               Glide.with(mContext).load(R.drawable.ic_heart_rate_critical).into(icNotification)
           }
            else if (unit.equals(UNIT_RESPIRATION)){
               Glide.with(mContext).load(R.drawable.ic_resp_rate_critical).into(icNotification)
           }else if (unit.equals(UNIT_TEMPERATURE)){
               Glide.with(mContext).load(R.drawable.ic_temp_critical).into(icNotification)
           }
        } else if (patientStatus.equals(STATE_UNDER_CONTROL)) {
            if (unit.equals(UNIT_HEART_RATE)){
                Glide.with(mContext).load(R.drawable.ic_heart_rate_under_control).into(icNotification)
            }
            else if (unit.equals(UNIT_RESPIRATION)){
                Glide.with(mContext).load(R.drawable.ic_resp_rate_under_control).into(icNotification)
            }else if (unit.equals(UNIT_TEMPERATURE)){
                Glide.with(mContext).load(R.drawable.ic_temp_under_control).into(icNotification)
            }
        } else if (patientStatus.equals(STATE_RECOVERED)) {
            if (unit.equals(UNIT_HEART_RATE)){
                Glide.with(mContext).load(R.drawable.ic_heart_rate_recovered).into(icNotification)
            }
            else if (unit.equals(UNIT_RESPIRATION)){
                Glide.with(mContext).load(R.drawable.ic_resp_rate_recovered).into(icNotification)
            }else if (unit.equals(UNIT_TEMPERATURE)){
                Glide.with(mContext).load(R.drawable.ic_temp_recovered).into(icNotification)
            }
        }

    }

}


class NotificationAdapter(
    private var notificationList: List<Notification>,
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
    fun onItemClicked(mNotification: Notification)
}