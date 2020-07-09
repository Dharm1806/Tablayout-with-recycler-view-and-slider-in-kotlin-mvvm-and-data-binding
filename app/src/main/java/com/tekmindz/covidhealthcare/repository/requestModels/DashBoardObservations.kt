package com.tekmindz.covidhealthcare.repository.requestModels

data class DashBoardObservations(
    val requireObservation: Boolean,
    val dateFilter: DateFilter,
    val search: String = ""
)

data class DateFilter(val fromDateTime: String, val toDateTime: String)