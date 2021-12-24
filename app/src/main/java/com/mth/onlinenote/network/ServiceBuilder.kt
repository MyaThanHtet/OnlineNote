package com.mth.onlinenote.network


import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


object ServiceBuilder {
    private val client = OkHttpClient.Builder().build()
    var gson = GsonBuilder()
        .setLenient()
        .create()

    /* retrofit with ScalarsConverterFactory*/
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://myatech.000webhostapp.com")
        .addConverterFactory(ScalarsConverterFactory.create())
        .client(client)
        .build()

    fun <T> buildService(service: Class<T>): T {
        return retrofit.create(service)
    }


    /* retrofit with GsonConverterFactory*/
    private val retrofit2 = Retrofit.Builder()
        .baseUrl("https://myatech.000webhostapp.com")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(client)
        .build()

    fun <T> buildService2(service: Class<T>): T {
        return retrofit2.create(service)
    }
}