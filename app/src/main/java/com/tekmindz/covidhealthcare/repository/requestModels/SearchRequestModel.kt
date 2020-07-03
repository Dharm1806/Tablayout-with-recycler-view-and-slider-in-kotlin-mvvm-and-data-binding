package com.tekmindz.covidhealthcare.repository.requestModels

data class SearchRequestModel(val fromDateTime :String, val toDateTime:String, val query:String)