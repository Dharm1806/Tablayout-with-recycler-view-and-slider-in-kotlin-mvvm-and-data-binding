package com.tekmindz.covidhealthcare.repository.api

import android.util.Log
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class BasicAuthInterceptor(var id:String,var password:String):Interceptor {

    private val credentials: String = Credentials.basic(id, password)

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val authenticatedRequest = request.newBuilder()
            .header("Authorization", credentials).build()
      //  Log.e("Log:::", String.format("\nheaders:\n%s", authenticatedRequest.headers()))
        return chain.proceed(authenticatedRequest)
    }

}