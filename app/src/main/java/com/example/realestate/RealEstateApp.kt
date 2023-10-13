package com.example.realestate

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.example.realestate.data.models.NewsElement
import com.example.realestate.data.remote.network.Retrofit
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RealEstateApp : Application() {
    companion object {
        private const val TAG = "RealEstateApp"
        lateinit var appContext: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()

        //apply dark theme
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        appContext = applicationContext
        //firebase initialisation
        FirebaseApp.initializeApp(this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )
        Retrofit.getInstance().getNews().enqueue(object : Callback<NewsElement> {
            override fun onResponse(
                call: Call<NewsElement>,
                response: Response<NewsElement>
            ) {
                if (response.isSuccessful) {
                    val resBody = response.body()
                    Log.d(TAG, "resBody: $resBody")
                }
            }

            override fun onFailure(call: Call<NewsElement>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }
}