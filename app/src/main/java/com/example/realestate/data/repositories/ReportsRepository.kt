package com.example.realestate.data.repositories

import com.example.realestate.data.models.Report
import com.example.realestate.data.remote.network.RetrofitService

class ReportsRepository(private val retrofitService: RetrofitService) {
    fun addReport(reportToAdd: Report) = retrofitService.addReport(reportToAdd)
}