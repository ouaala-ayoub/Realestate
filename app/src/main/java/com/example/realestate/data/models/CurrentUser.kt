package com.example.realestate.data.models

import com.example.realestate.R

class CurrentUser {
    companion object {
        private const val name = "user"
        private const val keyRes = R.string.cookie_token
        val prefs = PrefsCRUD(name, keyRes)

        fun isConnected() = prefs.get() != null
    }
}