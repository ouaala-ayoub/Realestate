package com.example.realestate.data.models

import android.util.Log
import com.example.realestate.utils.SessionCookie
import okhttp3.Interceptor
import okhttp3.Response

class ResponseInterceptor : Interceptor {
    companion object {
        private const val TAG = "ResponseInterceptor"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())

        // Check if the response contains a Set-Cookie header
        val cookies = originalResponse.headers("set-cookie")
        Log.d(TAG, "cookies: $cookies")
        if (cookies.isNotEmpty()) {
            for (cookie in cookies) {
                // Handle the session cookie here
                if (cookie.startsWith("session=")) {
                    val sessionCookie = cookie.substringAfter("session=")
                    val sessionValue = sessionCookie.split(";")[0]
                    // Store or process the session cookie as needed
                    Log.d(TAG, "session = $sessionValue")
                    SessionCookie.prefs.set(sessionValue)
                }
            }
        }

        return originalResponse
    }
}