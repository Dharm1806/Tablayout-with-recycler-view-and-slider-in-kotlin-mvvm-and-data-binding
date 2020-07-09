package com.tekmindz.covidhealthcare.utills


class ResponseList<T> private constructor(
    val status: ResponseList.Status,
    val data: List<T>?,
    val exception: String?
) {
    enum class Status {
        SUCCESS, ERROR, LOADING
    }

    companion object {

        fun <T> success(data: List<T>?): ResponseList<T> {
            return ResponseList(Status.SUCCESS, data, null)
        }

        fun <T> error(exception: String?): ResponseList<T> {
            return ResponseList(Status.ERROR, null, exception)
        }

        fun <T> loading(): ResponseList<T> {
            return ResponseList(Status.LOADING, null, null)
        }
    }
}