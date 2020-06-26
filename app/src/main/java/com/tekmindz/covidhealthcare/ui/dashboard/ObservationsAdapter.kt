package com.tekmindz.covidhealthcare.ui.dashboard

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tekmindz.covidhealthcare.R
import com.tekmindz.covidhealthcare.constants.Constants.STATE_CRITICAL
import com.tekmindz.covidhealthcare.constants.Constants.STATE_RECOVERED
import com.tekmindz.covidhealthcare.constants.Constants.STATE_UNDER_CONTROL
import com.tekmindz.covidhealthcare.repository.responseModel.DashboardObservationsResponse
import kotlinx.android.synthetic.main.item_critical_patient_list.view.*


class IssueHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(observation: DashboardObservationsResponse, clickListener: OnItemClickListener, mContext: Context) {
        //load the patient profile image
        Glide.with(mContext).load(observation.imageUrl).into(itemView.img_profile)

        if (observation.status.equals(STATE_CRITICAL)){
            itemView.img_profile.borderColor = mContext.resources.getColor(R.color.red)
            itemView.patient_name.setTextColor(mContext.resources.getColor(R.color.red))
        }else if (observation.status.equals(STATE_UNDER_CONTROL)){
            itemView.img_profile.borderColor = mContext.resources.getColor(R.color.amber)
            itemView.patient_name.setTextColor(mContext.resources.getColor(R.color.amber))
        }else if (observation.status.equals(STATE_RECOVERED)){
            itemView.patient_name.setTextColor(mContext.resources.getColor(R.color.green))
            itemView.img_profile.borderColor = mContext.resources.getColor(R.color.green)
        }


        //set patient name with first charactar capital
        val name =  observation.firstName.capitalize()+" "+observation.lastName.capitalize()
        itemView.patient_name.text =name

        //set patient ward no.
        val wardNo = mContext.getString(R.string.msg_ward_no)+observation.wardNo
        itemView.ward_no.text = wardNo

        //set patient bed no.
        val bedNo = mContext.getString(R.string.msg_bed_no)+observation.bedNumber
        itemView.bad_no.text = bedNo


        //handle patient click event
        itemView.setOnClickListener {
            clickListener.onItemClicked(observation)
        }
    }

}


class ObaserVationsAdapter(private var mObservations: List<DashboardObservationsResponse>,
                    private val itemClickListener: OnItemClickListener, private var mContext:Context) : RecyclerView.Adapter<IssueHolder>() {

    @Override
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): IssueHolder {
        //inflate the view
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_critical_patient_list, parent, false)
        return IssueHolder(view)
    }

    //return the item count of patient list
    @Override
    override fun getItemCount(): Int = mObservations.size

    //bind the viewholder
    @Override
    override fun onBindViewHolder(myHolder: IssueHolder, position: Int) =
            myHolder.bind(mObservations[position], itemClickListener, mContext)
}


interface OnItemClickListener {
    fun onItemClicked(mDashboardObservationsResponse: DashboardObservationsResponse)
}