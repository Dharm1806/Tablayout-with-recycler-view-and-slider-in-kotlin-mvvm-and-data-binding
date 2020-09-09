package com.dharam.offers.application

import android.app.Application
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner

import com.google.gson.GsonBuilder
import com.dharam.offers.constants.Constants.BASE_URL

import com.dharam.offers.repository.api.OffersApis
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class App : Application(), LifecycleObserver {

    //For the sake of simplicity, for now we use this instead of Dagger
    companion object {

        lateinit var offersApi: OffersApis
        private lateinit var retrofit: Retrofit


    }

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        //create the gsonBuilder instance
        val gGson = GsonBuilder()

        //create the retrofit instance

        retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gGson.create()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(BASE_URL)
            .build()
        offersApi = retrofit.create(OffersApis::class.java)



    }



    // function to return interCepter
    private fun provideCacheInterceptor(): Interceptor? = Interceptor { chain ->

        val response: Response = chain.proceed(chain.request())
        // re-write response header to force use of cache
        val cacheControl = CacheControl.Builder()
            // .maxAge(CACHE_TIME_IN_HOURS, TimeUnit.HOURS)
            .build()
        response.newBuilder()
            .header("Cache-Control", cacheControl.toString())
            .build()
    }



    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        Log.d("MyApp", "App in background")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        Log.d("MyApp", "App in foreground")
    }
}
