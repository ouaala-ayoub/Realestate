package com.example.realestate

import android.app.Application
import android.content.Context
import com.cloudinary.Configuration
import com.cloudinary.android.MediaManager
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory

class RealEstateApp : Application() {
    companion object {
        lateinit var appContext: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()

        appContext = applicationContext

        //firebase initialisation
        FirebaseApp.initializeApp(this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )

        //cloudinary initialisation
//        val config = MediaManager.get().config.newBuilder()
//            .cloudName("your_cloud_name")
//            .apiKey("your_api_key")
//            .apiSecret("your_api_secret")
//            .build()
//
        val config = Configuration.Builder()
            .setCloudName("dc4bar8fq")
            .setApiKey("735146312324787")
            .setApiSecret("SEHKmSQ2ikNZ4g5zQ7ARwfpSidY")
            .build()

        MediaManager.init(this, config)

    }
}