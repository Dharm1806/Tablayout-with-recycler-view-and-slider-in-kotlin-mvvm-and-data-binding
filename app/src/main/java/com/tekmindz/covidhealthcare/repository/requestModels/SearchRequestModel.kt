package com.tekmindz.covidhealthcare.repository.requestModels

data class SearchRequestModel(
    val requireObservation: Boolean,
    val dateFilter: DateFilter,
    val search: String
)