package com.example.realestate

import android.app.Application
import android.content.Context
import android.util.Log
import com.cloudinary.Cloudinary
import com.cloudinary.Configuration
import com.cloudinary.android.MediaManager
import com.cloudinary.android.signed.Signature
import com.cloudinary.android.signed.SignatureProvider
import com.example.realestate.data.models.ParamsToSign
import com.example.realestate.data.models.SignResult
import com.example.realestate.data.remote.network.Retrofit
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import okio.IOException


class RealEstateApp : Application() {
    companion object {
        private const val TAG = "RealEstateApp"
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
//        val config = Configuration.Builder()
//            .setCloudName("dc4bar8fq")
//            .setApiKey("735146312324787")
//            .setApiSecret("SEHKmSQ2ikNZ4g5zQ7ARwfpSidY")
//            .build()
//
//        MediaManager.init(this, config)

//        val config: MutableMap<String, String> = HashMap()
//        config["cloud_name"] = "realestatefy"

//        val config = Configuration.Builder()
//            .setCloudName("realestatefy")
//            .build()
//        val properties = Properties()
//        val localPropertiesPath = "local.properties" // Replace with the correct path to your local.properties file
//        val localPropertiesFile = FileInputStream(localPropertiesPath)
//        properties.load(localPropertiesFile)

//        val apiKey = properties.getProperty("API_KEY")
        val config: MutableMap<String, String> = HashMap()
        config["cloud_name"] = "realestatefy"
        config["api_key"] = "861679653415449"
        config["api_secret"] = "F2zGLtHOsTqubB1w0EmB3T-idOI"

        MediaManager.init(this, config)

    }

    fun signUpload(options: MutableMap<Any?, Any?>): SignResult {
        val timestamp = System.currentTimeMillis() / 1000

        val call = Retrofit.getInstance().generateSignature(timestamp)
        val response = call.execute()

        if (response.isSuccessful) {
            return response.body() ?: throw IllegalStateException("Signature response body is null")
        } else {
            throw IOException("Error generating signature: ${response.code()} ${response.message()}")
        }
    }
}