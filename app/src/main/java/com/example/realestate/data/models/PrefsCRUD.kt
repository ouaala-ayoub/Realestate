package com.example.realestate.data.models

import android.content.Context
import android.content.SharedPreferences
import com.example.realestate.RealEstateApp

open class PrefsCRUD(name: String, private val keyRes: Int) {

    private val sharedPrefs: SharedPreferences =
        RealEstateApp.appContext.getSharedPreferences(name, Context.MODE_PRIVATE)

    fun set(value: String) {
        val context = RealEstateApp.appContext
        with(sharedPrefs.edit()) {
            putString(context.resources.getString(keyRes), value)
            commit()
        }
    }

    fun get(): String? {
        val accessToken: String?
        val context = RealEstateApp.appContext
        context.apply {
            accessToken = sharedPrefs
                .getString(
                    resources.getString(keyRes), null
                )
        }
        return accessToken
    }

    fun delete(): Boolean {
        try {
            val context = RealEstateApp.appContext

            val editor = sharedPrefs.edit()
            editor
                .remove(context.resources.getString(keyRes))
                .apply()
        } catch (e: Throwable) {
            e.printStackTrace()
            return false
        }
        return true
    }

}