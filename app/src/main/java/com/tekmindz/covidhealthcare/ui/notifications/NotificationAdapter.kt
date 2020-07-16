package com.tekmindz.covidhealthcare.ui.notifications

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
import com.tekmindz.covidhealthcare.databinding.ItemNotificationsBinding
import com.tekmindz.covidhealthcare.databinding.ItemSearchListBinding
import com.tekmindz.covidhealthcare.repository.responseModel.Notification
import com.tekmindz.covidhealthcare.repository.responseModel.observations
import com.tekmindz.covidhealthcare.utills.Utills


class NotificationHolder(val binding: ItemNotificationsBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(notifiction: Notification, clickListener: OnItemClickListener, mContext: Context) {
        //load the patient profile image
       /* Glide.with(mContext).load(observation.imageUrl).into(binding.imgProfile)

        if (observation.status.equals(STATE_CRITICAL)) {
            binding.imgProfile.borderColor = mContext.resources.getColor(R.color.red)
            binding.patientName.setTextColor(mContext.resources.getColor(R.color.red))
        } else if (observation.status.equals(STATE_UNDER_CONTROL)) {
            binding.imgProfile.borderColor = mContext.resources.getColor(R.color.amber)
            binding.patientName.setTextColor(mContext.resources.getColor(R.color.amber))
        } else if (observation.status.equals(STATE_RECOVERED)) {
            binding.patientName.setTextColor(mContext.resources.getColor(R.color.green))
            binding.imgProfile.borderColor = mContext.resources.getColor(R.color.green)
        }*/

        //set patient name with first charactar capital

        binding.notificationMessage.text = notifiction.body.message
        binding.notificationTime.text = Utills.parseDate(notifiction.time)
        //handle patient click event
        binding.itemLayoutObservations.setOnClickListener {
            clickListener.onItemClicked(notifiction)
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