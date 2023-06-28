package com.example.realestate.data.repositories

import com.example.realestate.data.models.AdditionalInfo
import com.example.realestate.data.remote.network.RetrofitService

class UsersRepository(private val retrofitService: RetrofitService) {
    fun getUserById(userId: String) = retrofitService.getUserById(userId)

    fun addData(userId: String, data: AdditionalInfo) = retrofitService.addData(userId, data)

    fun login(token: String) = retrofitService.login("Bearer $token")
}