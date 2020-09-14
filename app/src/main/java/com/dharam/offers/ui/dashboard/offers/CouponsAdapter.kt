package com.dharam.offers.ui.dashboard.offers

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dharam.offers.R
import com.dharam.offers.databinding.ItemCouponsListBinding
import com.dharam.offers.repository.responseModel.Coupons


class OffersViewHolder(val binding: ItemCouponsListBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(coupon: Coupons, clickListener: OnItemClickListener, mContext: Context) {
      //  Glide.with(mContext).load(observation.imageUrl).placeholder((R.drawable.ic_placeholder)).into(binding.imgProfile)


        val title = coupon.title.capitalize()
        binding.tvCouponTitle.text = title

        val description =  coupon.description.capitalize()
        binding.tvCouponDescription.text = description

        val savings = mContext.getString(R.string.esitmated_savings) + coupon.price
        binding.tvCouponSaving.text = savings


        //handle patient click event
        binding.btnRedem.setOnClickListener {
            clickListener.onItemRedeem(coupon)
        }
    }

}


class CouponsAdapter(
    private var mCoupons: List<Coupons>,
    private val itemClickListener: OnItemClickListener, private var mContext: Context
) : RecyclerView.Adapter<OffersViewHolder>() {

    @Override
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): OffersViewHolder {
        //inflate the view
        val mBinding: ItemCouponsListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_coupons_list, parent, false
        )
        return OffersViewHolder(mBinding)
    }

    //return the item count of patient list
    @Override
    override fun getItemCount(): Int = mCoupons.size

    //bind the viewholder
    @Override
    override fun onBindViewHolder(myHolder: OffersViewHolder, position: Int) =
        myHolder.bind(mCoupons[position], itemClickListener, mContext)
}


interface OnItemClickListener {
    fun onItemRedeem(coupons: Coupons)
}