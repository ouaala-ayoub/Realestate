package com.example.realestate.data.models

import android.util.Log
import com.example.realestate.utils.SessionCookie
import com.google.firebase.auth.FirebaseAuth
import okhttp3.Cookie
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class RequestInterceptor : Interceptor {

    companion object {
        private const val TAG = "RequestInterceptor"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val builder: Request.Builder = chain.request().newBuilder()

        //add the session cookie stored in the shared prefs
        SessionCookie.prefs.get()?.apply {
            Log.d(TAG, "session_cookie = $this")
            builder.addHeader("Cookie", "session_cookie=$this")
        }

        return chain.proceed(builder.build())
    }
}
