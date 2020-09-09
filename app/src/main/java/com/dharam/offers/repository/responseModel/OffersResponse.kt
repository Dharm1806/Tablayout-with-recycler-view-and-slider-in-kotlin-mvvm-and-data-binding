package com.dharam.offers.repository.responseModel

data class OffersResponse(val statusCode: Int, val APICODERESULT: String, val result: Result)
data class Result(
    val description_title: String,
    val decription_image: String,
    val description_body: String,
    val banner: List<String>,
    val latitudes: String,
    val longitude: String,
    val cupons: List<Coupons>
)

data class Coupons(val title: String, val description: String, val price: String)

data class SliderItem(val imageUrl:String)