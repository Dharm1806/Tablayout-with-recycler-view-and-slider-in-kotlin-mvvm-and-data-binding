package com.tekmindz.covidhealthcare.repository.requestModels

data class DashBoardObservations(val requireObservation:Boolean,val  dateFilter:DateFilter)

data class DateFilter(val fromDateTime :String, val toDateTime:String)