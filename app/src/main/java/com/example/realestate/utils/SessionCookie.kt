package com.example.realestate.utils

import com.example.realestate.R
import com.example.realestate.data.models.PrefsCRUD

class SessionCookie {
    companion object {
        private const val name = "cookie"
        private const val keyRes = R.string.cookie_token
        val prefs = PrefsCRUD(name, keyRes)
    }
}