package com.example.realestate.data.repositories

import com.example.realestate.data.remote.network.RetrofitService

class StaticDataRepository(private val retrofitService: RetrofitService) {
    fun getCategories() = retrofitService.getCategories()

    fun getReportReasons() = retrofitService.getReportReasons()

    fun getAllCities() = retrofitService.getAllCities()

    fun getCountries() = retrofitService.getAllCountries()

}