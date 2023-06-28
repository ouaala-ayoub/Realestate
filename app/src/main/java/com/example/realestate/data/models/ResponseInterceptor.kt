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
        val cookies = originalResponse.headers("Set-Cookie")
        if (cookies.isNotEmpty()) {
            for (cookie in cookies) {
                // Handle the session cookie here
                if (cookie.startsWith("session_cookie=")) {
                    val sessionCookie = cookie.substringAfter("session_cookie=")
                    // Store or process the session cookie as needed
                    Log.d(TAG, "session_cookie = $sessionCookie")
                    SessionCookie.prefs.set(sessionCookie)
                }
            }
        }

        return originalResponse
    }
}