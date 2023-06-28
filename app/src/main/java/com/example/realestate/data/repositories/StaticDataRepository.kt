package com.example.realestate.data.repositories

import com.example.realestate.data.remote.network.RetrofitService

class StaticDataRepository(private val retrofitService: RetrofitService) {
    //    fun getCountries() {
//
//    }
    fun getCategories() = retrofitService.getCategories()

//    fun get
}