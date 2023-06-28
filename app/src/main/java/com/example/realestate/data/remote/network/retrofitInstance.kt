package com.example.realestate.data.remote.network

import com.example.realestate.data.models.RequestInterceptor
import com.example.realestate.data.models.ResponseInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Retrofit {
    private const val BASE_URL = "https://realestatefy.vercel.app/api/"
    private var retrofitService: RetrofitService? = null


    fun getInstance(): RetrofitService {
        val client = OkHttpClient
            .Builder()
            .addInterceptor(RequestInterceptor())
            .addInterceptor(ResponseInterceptor())

        if (retrofitService == null) {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client.build())
                .build()
            retrofitService = retrofit.create(RetrofitService::class.java)
        }
        return retrofitService!!
    }
}