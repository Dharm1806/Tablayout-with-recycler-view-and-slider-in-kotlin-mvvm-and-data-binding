package com.tekmindz.covidhealthcare.application

import android.app.Application
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner

import com.google.gson.GsonBuilder
import com.tekmindz.covidhealthcare.constants.Constants.BASE_URL
import com.tekmindz.covidhealthcare.constants.Constants.LOGIN_BASE_URL
import com.tekmindz.covidhealthcare.repository.api.HealthCareApis
import com.tekmindz.covidhealthcare.utills.SharedPreference
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File


class App : Application(), LifecycleObserver {

    //For the sake of simplicity, for now we use this instead of Dagger
    companion object {
        private lateinit var retrofitAll: Retrofit
        lateinit var healthCareApi: HealthCareApis
        private lateinit var retrofit: Retrofit
        lateinit var healthCareApiLogin: HealthCareApis
        lateinit var mSharedPrefrenceManager: SharedPreference
        var isForeGround: Boolean = false

    }

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        //create the gsonBuilder instance
        val gGson = GsonBuilder()
        mSharedPrefrenceManager = SharedPreference(this)
        //create the retrofit instance
        retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gGson.create()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(provideOkHttpClient()!!)
            .baseUrl(LOGIN_BASE_URL)
            .build()


        healthCareApiLogin = retrofit.create(HealthCareApis::class.java)
       initalize()
    }
  public fun initalize(

    ){
        BASE_URL =   mSharedPrefrenceManager.getValueString("base_url")?:  "http://34.216.159.69:8081/"
      Log.e("base_url", "$BASE_URL")
      if (!BASE_URL.contains("http")){
          BASE_URL =    "http://34.216.159.69:8081/"
      }
        val gGson = GsonBuilder()
        retrofitAll = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gGson.create()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(provideOkHttpClient()!!)
            .baseUrl(BASE_URL)
            .build()

        healthCareApi = retrofitAll.create(HealthCareApis::class.java)

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

    //return okHttp client
    private fun provideOkHttpClient(): OkHttpClient? = OkHttpClient.Builder()
        //.addNetworkInterceptor(provideCacheInterceptor()!!)
        //    .cache(provideCache())
        .build()

    //return the cache object with properties
    private fun provideCache(): Cache? {

        var cache: Cache? = null
        try {
            cache = Cache(
                File(applicationContext.cacheDir, "http-cache"),
                10 * 1024 * 1024
            ) // 10 MB
        } catch (e: Exception) {
        }
        return cache
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        isForeGround = false
        Log.d("MyApp", "App in background")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        isForeGround = true
        Log.d("MyApp", "App in foreground")
    }
}
