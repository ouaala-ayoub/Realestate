package com.example.realestate.data.models

import com.example.realestate.R

class CurrentUser {
    companion object {
        private var current: User? = null
        private const val name = "user"
        private const val keyRes = R.string.cookie_token
        val prefs = PrefsCRUD(name, keyRes)

        fun isConnected() = current != null
        fun get() = current
        fun set(user: User?) {
            current = user
        }

        fun logout() {
            current = null
        }

        fun isUserIdStored() = prefs.get() != null
    }
}