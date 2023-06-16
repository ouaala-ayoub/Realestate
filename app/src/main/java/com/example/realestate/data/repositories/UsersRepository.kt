package com.example.realestate.data.repositories

import com.example.realestate.data.remote.network.RetrofitService

class UsersRepository(private val retrofitService: RetrofitService) {
    fun getUserById(userId: String) = retrofitService.getUserById(userId)
}